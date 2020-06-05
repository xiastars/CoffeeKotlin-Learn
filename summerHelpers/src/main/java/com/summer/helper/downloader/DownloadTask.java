package com.summer.helper.downloader;

import android.text.TextUtils;

import com.summer.helper.utils.Logs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by dzc on 15/11/21.
 */
public class DownloadTask implements Runnable {
    private DownloadDBEntity dbEntity;
    private DownloadDao downloadDao;
    private DownloadManager downloadManager;
    private OkHttpClient client;


    private String id;
    private long toolSize;
    private long completedSize;         //  Download section has been completed
    //    private float percent;        //  Percent Complete
    private String url;
    private String saveDirPath;
    private RandomAccessFile file;
    private int UPDATE_SIZE = 50 * 1024;    // The database is updated once every 50k
    private int downloadStatus = DownloadStatus.DOWNLOAD_STATUS_INIT;

    int downloadIndex;
    private String fileName;    // File name when saving


    private List<DownloadTaskListener> listeners;

    public DownloadTask() {
        listeners = new ArrayList<>();
    }
    
    public DownloadTask(String url,String saveDirPath,String name) {
    	this.url = url;
    	this.id = url;
    	this.saveDirPath = saveDirPath;
    	this.fileName = name;
    	build();
    }
    
    public DownloadTask(String id,String url,String saveDirPath,String name) {
    	this.url = url;
    	this.id = id;
    	this.saveDirPath = saveDirPath;
    	this.fileName = name;
        build();
    }
    
    private void build(){
    	File file = new File(saveDirPath);
    	if(!file.exists()){
    		file.mkdirs();
    	}
        File sFile = new File(saveDirPath + fileName);
        if(sFile.exists()){
    		sFile.delete();
    	}
        listeners = new ArrayList<>();
    }

    @Override
    public void run() {
        downloadStatus = DownloadStatus.DOWNLOAD_STATUS_PREPARE;
        onPrepare();
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        try {
            dbEntity = downloadDao.load(id);
            file = new RandomAccessFile(saveDirPath + fileName, "rwd");
            if(dbEntity!=null){
                completedSize = dbEntity.getCompletedSize();
                toolSize = dbEntity.getToolSize();
            }
            Logs.i("下载路径:"+saveDirPath + fileName+",总共大小:"+toolSize);
            if (file.length() < completedSize) {
                completedSize = file.length();
            }
            long fileLength = file.length();
            if(fileLength != 0 && toolSize <= fileLength){
                downloadStatus = DownloadStatus.DOWNLOAD_STATUS_COMPLETED;
                toolSize = completedSize = fileLength;
                dbEntity = new DownloadDBEntity(id, toolSize, toolSize, url, saveDirPath, fileName, downloadStatus);
                downloadDao.insertOrReplace(dbEntity);
                onCompleted();
                return;
            }
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_START;
            onStart();
            Request request = new Request.Builder()
                    .url(url)
                    .header("RANGE", "bytes=" + completedSize + "-")    //  Http value set breakpoints RANGE
                    .build();
            file.seek(completedSize);
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            
            if (responseBody != null) {
                downloadStatus = DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING;
                toolSize = responseBody.contentLength();

                inputStream = responseBody.byteStream();
                bis = new BufferedInputStream(inputStream);
                byte[] buffer = new byte[2 * 1024];
                int length = 0;
                int buffOffset = 0;
                if (dbEntity == null) {
                    dbEntity = new DownloadDBEntity(id, toolSize, 0L, url, saveDirPath, fileName, downloadStatus);
                    downloadDao.insertOrReplace(dbEntity);
                }
                Logs.i("responseBody:"+toolSize+"--"+downloadStatus+"--"+buffer);
                while ((length = bis.read(buffer)) > 0 && downloadStatus != DownloadStatus.DOWNLOAD_STATUS_CANCEL &&downloadStatus!=DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                    file.write(buffer, 0, length);
                    completedSize += length;
                    buffOffset += length;
                    if (buffOffset >= UPDATE_SIZE) {
                        // Update download information database
                        buffOffset = 0;
                        dbEntity.setCompletedSize(completedSize);
                        downloadDao.update(dbEntity);
                        onDownloading();
                    }
                }
                //这两句根据需要自行选择是否注释，注释掉的话由于少了数据库的读取，速度会快一点，但同时如果在下载过程程序崩溃的话，程序不会保存最新的下载进度
                dbEntity.setCompletedSize(completedSize);
                downloadDao.update(dbEntity);
                onDownloading();
            }
        } catch (FileNotFoundException e) {
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            onError(DownloadTaskListener.DOWNLOAD_ERROR_FILE_NOT_FOUND);
            Logs.i("xia",e.toString());
            return;
//            e.printStackTrace();
        } catch (IOException e) {
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            onError(DownloadTaskListener.DOWNLOAD_ERROR_IO_ERROR);
            Logs.i("xia",e.toString());
            return;
        } finally {
        	if(completedSize < toolSize){
                onError(DownloadTaskListener.DOWNLOAD_ERROR_IO_ERROR);
        		Logs.i("未知错误");
        	}else{
                dbEntity.setCompletedSize(completedSize);
                downloadDao.update(dbEntity);
            }
            if (bis != null) try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (file != null) try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(toolSize==completedSize)downloadStatus=DownloadStatus.DOWNLOAD_STATUS_COMPLETED;
        dbEntity.setDownloadStatus(downloadStatus);
        downloadDao.update(dbEntity);


        switch (downloadStatus){
            case DownloadStatus.DOWNLOAD_STATUS_COMPLETED:
                onCompleted();
                break;
            case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                onPause();
                break;
            case DownloadStatus.DOWNLOAD_STATUS_CANCEL:
                downloadDao.delete(dbEntity);
                File temp = new File(saveDirPath + fileName);
                if(temp.exists())temp.delete();
                onCancel();
                break;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public float getPercent() {
        if(toolSize != 0 ){
            return completedSize * 100 / toolSize;
        }
        return 0;
    }


    public long getToolSize() {
        return toolSize;
    }

    public void setTotalSize(long toolSize) {
        this.toolSize = toolSize;
    }


    public long getCompletedSize() {
        return completedSize;
    }

    public void setCompletedSize(long completedSize) {
        this.completedSize = completedSize;
    }

    public String getSaveDirPath() {
        return saveDirPath;
    }

    public void setSaveDirPath(String saveDirPath) {
        this.saveDirPath = saveDirPath;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public void setDownloadDao(DownloadDao downloadDao) {
        this.downloadDao = downloadDao;
    }

    public void setDbEntity(DownloadDBEntity dbEntity) {
        this.dbEntity = dbEntity;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDonwlownIndex(int downloadIndex){
        this.downloadIndex = downloadIndex;
    }

    public void setHttpClient(okhttp3.OkHttpClient client) {
        this.client = client;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public void cancel() {
        setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_CANCEL);
        File temp = new File(saveDirPath + fileName);
        if(temp.exists())temp.delete();
    }

    public void pause(){
        setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_PAUSE);
        for (DownloadTaskListener listener : listeners) {
            listener.onPause(this);
        }
    }
    private void onPrepare() {
//        for (DownloadTaskListener listener : listeners) {
//            listener.onPrepare(this);
//        }
    }

    private void onStart() {
//        for (DownloadTaskListener listener : listeners) {
//            listener.onStart(this);
//        }
    }

    private void onDownloading() {
        for (DownloadTaskListener listener : listeners) {
            listener.onDownloading(this);
        }
    }

    private void onCompleted() {
        for (DownloadTaskListener listener : listeners) {
//            listener.onCompleted(this);
            listener.onDownloading(this);
        }
    }

    private void onPause() {
        for (DownloadTaskListener listener : listeners) {
            listener.onPause(this);
        }
    }

    private void onCancel() {
//        for (DownloadTaskListener listener : listeners) {
//            listener.onCancel(this);
//        }
    }

    private void onError(int errorCode) {
        for (DownloadTaskListener listener : listeners) {
            listener.onError(this, errorCode);
        }
    }

    public void addDownloadListener(DownloadTaskListener listener) {
        listeners.add(listener);
    }

    /**
     * if listener is null,clear all listener
     * @param listener
     */
    public void removeDownloadListener(DownloadTaskListener listener) {
        if(listener==null){
            listeners.clear();
        }else{
            listeners.remove(listener);
        }
    }

    public void setDownloadManager(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DownloadTask)) {
            return false;
        }
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(saveDirPath)) {
            return false;
        }
        return url.equals(((DownloadTask) o).url) && saveDirPath.equals(((DownloadTask) o).saveDirPath);
    }

    public static DownloadTask parse(DownloadDBEntity entity) {
        DownloadTask task = new DownloadTask();
        task.setDownloadStatus(entity.getDownloadStatus());
        task.setId(entity.getDownloadId());
        task.setUrl(entity.getUrl());
        task.setFileName(entity.getFileName());
        task.setSaveDirPath(entity.getSaveDirPath());
        task.setCompletedSize(entity.getCompletedSize());
        task.setDbEntity(entity);
        task.setTotalSize(entity.getToolSize());
        return task;
    }
}

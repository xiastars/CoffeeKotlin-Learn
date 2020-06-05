package com.summer.helper.utils;

import android.app.Activity;

import com.summer.helper.downloader.DownloadStatus;
import com.summer.helper.downloader.DownloadTask;
import com.summer.helper.downloader.DownloadTaskListener;
import com.summer.helper.listener.DownloadPhotoListener;
import com.summer.helper.server.EasyHttp;

/**
 * Created by xiastars on 2018/1/17.
 */

public class DownloadPhotoHelper implements DownloadPhotoListener {

    public static DownloadPhotoHelper getInstance() {
        return new DownloadPhotoHelper();
    }

    @Override
    public void download(final Activity activity, String path) {
        final String fileName = System.currentTimeMillis() + ".png";
        EasyHttp.download(activity, path, SFileUtils.getDownloadDirectory(), fileName, new DownloadTaskListener() {
            @Override
            public void onDownloading(DownloadTask downloadTask) {
                Logs.i(downloadTask.getPercent() + ",,");
                if (downloadTask.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String path = SFileUtils.getDownloadDirectory() + fileName;
                            SUtils.notifyLocalAlbum(activity, path);
                            SUtils.makeToast(activity, "保存成功，位置：" + SFileUtils.getDownloadDirectory() + fileName);
                        }
                    });

                }
            }

            @Override
            public void onPause(DownloadTask downloadTask) {

            }

            @Override
            public void onError(DownloadTask downloadTask, int errorCode) {

            }
        });
    }
}

package com.summer.zipparser;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.malata.summer.helper.R;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;
import java.util.List;

/**
 * ZIP解析并放到一个新的文件夹
 *
 * @author malata_xiaqiliang
 * @time 2016年5月27日
 */
public class ZipPaser {

    private String copyPath;//复制的ZIP路径
    private String toPath;

    private Context mContext;
    private OnProgressListener listener;

    //Constructor
    public ZipPaser(Context mContext, OnProgressListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        startPaser();
    }

    public ZipPaser(Context context) {
        mContext = context;
    }

    public final void startPaser() {
        copyPath = getSDPath() + "/" + mContext.getResources().getString(R.string.copy_file);
        ;
        toPath = getSDPath() + "/" + mContext.getResources().getString(R.string.to_file);

        new Thread(new Runnable() {
            public void run() {
                startCopy();
            }
        }).start();
    }

    private void startCopy() {
        try {
            long time = System.currentTimeMillis();

            UnZipFolder(copyPath, toPath);
            listener.onParse(100);
            Log.i("ZipPaser", "解析完毕-^o^-共耗时" + (System.currentTimeMillis() - time) / 1000 + "秒");
        } catch (Exception e) {
            Log.i("ZipPaser", "解析失败--" + e.toString());
            e.printStackTrace();
        }
    }

    public String UnZipFolder(String zipFileString, String outPathString) {
        ZipFile zipFile = null;
        List<FileHeader> headers = null;
        try {
            zipFile = new ZipFile(zipFileString);
            headers = zipFile.getFileHeaders();

        } catch (ZipException e) {
            e.printStackTrace();
        }

        if (headers != null) {
            int totalSize = headers.size();
            for (int i = 0; i < headers.size(); i++) {
                try {
                    if (null != listener) {
                        listener.onParse((int) (((float) i / totalSize) * 100));
                    }
                    FileHeader header = headers.get(i);
                    header.setFileNameUTF8Encoded(false);
                    zipFile.extractFile(header, outPathString);

                } catch (ZipException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        return null;
    }

    /**** 取SD卡路径不带/ ****/
    public String getSDPath() {
        File sdDir = null;
        try {
            boolean sdCardExist = android.os.Environment
                    .getExternalStorageState().equals(
                            android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
            if (sdCardExist) {
                sdDir = android.os.Environment
                        .getExternalStorageDirectory();// 获取跟目录
            } else {
                File file = new File(Environment.getDataDirectory()
                        + "/sdcard");
                if (file.canRead()) {
                    return file.toString();
                } else {
                    return "";
                }
            }
            if (sdDir != null) {
                return sdDir.toString();
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return "";
    }
}

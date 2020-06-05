package com.github.hiteshsondhi88.libffmpeg;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;

class FFmpegLoadLibraryAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private final String cpuArchNameFromAssets;
    private final FFmpegLoadBinaryResponseHandler ffmpegLoadBinaryResponseHandler;
    private final Context context;

    FFmpegLoadLibraryAsyncTask(Context context, String cpuArchNameFromAssets, FFmpegLoadBinaryResponseHandler ffmpegLoadBinaryResponseHandler) {
        this.context = context;
        this.cpuArchNameFromAssets = cpuArchNameFromAssets;
        this.ffmpegLoadBinaryResponseHandler = ffmpegLoadBinaryResponseHandler;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        File ffmpegFile = new File(FileUtils.getFFmpeg(context));
        if (ffmpegFile.exists() && isDeviceFFmpegVersionOld() && !ffmpegFile.delete()) {
            return false;
        }
        if (!ffmpegFile.exists()) {
            /* 从aseets读取改为从内存卡获取 */
            boolean isFileCopied = FileUtils.copyBinaryFromAssetsToData(context,
                    getSDPath() + "/" + "fsxq/.file/" + cpuArchNameFromAssets + "_" + FileUtils.ffmpegFileName,
                    FileUtils.ffmpegFileName);

            // make file executable
            if (isFileCopied) {
                if (!ffmpegFile.canExecute()) {
                    Log.d("FFmpeg is not executable, trying to make it executable ...");
                    if (ffmpegFile.setExecutable(true)) {
                        return true;
                    }
                } else {
                    Log.d("FFmpeg is executable");
                    return true;
                }
            }
        }
        return ffmpegFile.exists() && ffmpegFile.canExecute();
    }

    public static String getSDPath() {
        File sdDir = null;
        try {
            boolean sdCardExist = android.os.Environment.getExternalStorageState()
                    .equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
            if (sdCardExist) {
                sdDir = android.os.Environment.getExternalStorageDirectory();
            } else {
                File file = new File(Environment.getDataDirectory() + "/sdcard");
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
            android.util.Log.e("Error", e.getMessage());
        }
        return "";
    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        if (ffmpegLoadBinaryResponseHandler != null) {
            if (isSuccess) {
                ffmpegLoadBinaryResponseHandler.onSuccess();
            } else {
                ffmpegLoadBinaryResponseHandler.onFailure();
            }
            ffmpegLoadBinaryResponseHandler.onFinish();
        }
    }

    private boolean isDeviceFFmpegVersionOld() {
        return CpuArch.fromString(FileUtils.SHA1(FileUtils.getFFmpeg(context))).equals(CpuArch.NONE);
    }
}

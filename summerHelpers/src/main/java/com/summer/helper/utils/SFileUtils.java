package com.summer.helper.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 处理数据相关
 *
 * @author xiaqiliang
 */
public class SFileUtils {

    /**
     * 初始文件夹名称
     */
    public static String DIR_NAME = "SummerDemo";

    /**
     * 根目录
     */
    public static String ROOT_PATH = SUtils.getSDPath() + "/";
    /**
     * 资源根目录
     */
    public static String SOURCE_PATH = ROOT_PATH + DIR_NAME + "/";
    /**
     * 我的录音根目录
     */
    public static String ROOT_AUDIO = SOURCE_PATH + "audio/";
    /**
     * 背景图根目录
     */
    public static String ROOT_BACKGROUND = SOURCE_PATH + "pics/";

    /**
     * 背景图根目录
     */
    public static String ROOT_HIDEPIC = SOURCE_PATH + ".hidepics/";

    /**
     * 背景图根目录
     */
    public static String ROOT_DOWNLOAD = SOURCE_PATH + "download/";
    /**
     * 缓存目录
     */
    public static String ROOT_CACHE = SOURCE_PATH + "cache/";
    /**
     * 图书目录
     */
    public static String ROOT_BOOK = SOURCE_PATH + ".books/";

    /**
     * 图书目录
     */
    public static String ROOT_FILE = SOURCE_PATH + "file/";

    /**
     * 预览图根目录
     */
    public static String ROOT_AVATAR = SOURCE_PATH + "avatar/";

    /**
     * 视频缓存目录
     */
    public static String ROOT_VIDEO = SOURCE_PATH + "video/";


    private static String INTERVAL_CACHE = "INTERVAL_CACHE";

    /**
     * 资源后缀
     */
    public class FileType {
        public static final String FILE_MP4 = ".mp4";
        public static final String FILE_PNG = ".png";
        public static final String FILE_APK = ".apk";
        public static final String FILE_JPG = ".jpg";
        public static final String FILE_MP3 = ".mp3";
    }

    /**
     * 安全获取目录
     *
     * @param path
     * @return
     */
    public static String getDirectorySafe(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 安全获取目录
     *
     * @return
     */
    public static String getBookDirectory() {
        String path = ROOT_BOOK;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 安全获取目录
     *
     * @return
     */
    public static String getFileDirectory() {
        String path = ROOT_FILE;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 安全获取目录
     *
     * @return
     */
    public static String getAudioDirectory() {
        String path = ROOT_AUDIO;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 安全获取目录
     *
     * @return
     */
    public static String getHidePicRoot() {
        String path = ROOT_HIDEPIC;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 安全获取目录
     *
     * @return
     */
    public static String getAvatarDirectory() {
        String path = ROOT_AVATAR;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 安全获取目录
     *
     * @return
     */
    public static String getVideoDirectory() {
        String path = ROOT_VIDEO;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 安全获取目录
     *
     * @return
     */
    public static File getVideoDirectoryFile() {
        String path = ROOT_VIDEO;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 安全获取目录
     *
     * @return
     */
    public static String getImageViewDirectory() {
        String path = ROOT_BACKGROUND;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 安全获取目录
     *
     * @return
     */
    public static String getDownloadDirectory() {
        String path = ROOT_DOWNLOAD;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 缓存目录
     *
     * @return
     */
    public static String getCacheDirectory() {
        String path = ROOT_CACHE;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 根据名称获取该名称下的图片集合
     *
     * @param name
     * @return
     */
    public static String[] getResourceByName(String name) {
        File file = new File(name);
        if (file.exists() && file.isDirectory()) {
            String[] files = file.list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String filename) {
                    return !(!filename.endsWith(".png") && !filename.endsWith(".jpg"));
                }
            });
            for (int i = 0; i < files.length; i++) {
                files[i] = file.getAbsolutePath() + "/" + files[i];
            }
            return files;
        }
        return null;
    }

    /***
     * 定时清理,三天清理一次
     */
    public static void initCache(Context context) {
        long time = SUtils.getLongData(context, INTERVAL_CACHE);
        int day = 1000 * 60 * 60 * 24 * 3;
        if (time == 0) {
            SUtils.saveLongData(context, INTERVAL_CACHE, System.currentTimeMillis());
            return;
        }
        if (System.currentTimeMillis() - time < day) {
            Logs.i("未到清理时间" + SUtils.parseSceneTime(time));
            return;
        }
        SUtils.saveLongData(context, INTERVAL_CACHE, System.currentTimeMillis());
        File file = new File(SOURCE_PATH);
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            deleteFileOfWeekAgo(file);
        }
    }

    /**
     * 删除缓存数据，过滤掉用户主动下载的数据及FFMEPG插件
     *
     * @param file
     */
    private static void deleteFileOfWeekAgo(File file) {
        int day = 1000 * 60 * 60 * 24 * 7;
        File[] files = file.listFiles();
        for (File f : files) {
            //过滤文件与下载路径
            if (f.isDirectory()) {
                if (f.getPath().endsWith("file") || f.getPath().endsWith("download")) {
                    continue;
                }
                deleteFileOfWeekAgo(f);
            } else {
                long createTime = f.lastModified();
                if (System.currentTimeMillis() - createTime > day) {
                    f.delete();
                }
            }
        }
    }

    /**
     * 将Assets里的文件拷贝到SD卡
     *
     * @param context
     * @param fileNameFromAssets
     * @param outputFileName
     * @return
     */
    public static boolean copyBinaryFromAssetsToData(Context context, String fileNameFromAssets, String outputFileName) {

        InputStream is;
        try {
            is = context.getAssets().open(fileNameFromAssets);
            final FileOutputStream os = new FileOutputStream(new File(outputFileName));
            byte[] buffer = new byte[1024 * 4];

            int n;
            while (-1 != (n = is.read(buffer))) {
                os.write(buffer, 0, n);
            }

            os.close();
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }

            return true;
        } catch (IOException e) {
            Logs.i("issue in coping binary from assets to data. ", e.toString());
        }
        return false;
    }

    /**
     * copy file
     *
     * @param sourceFilePath
     * @param destFilePath
     * @return
     * @throws RuntimeException if an error occurs while operator
     *                          FileOutputStream
     */
    public static boolean copyFile(String sourceFilePath, String destFilePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(sourceFilePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        }
        return writeFile(destFilePath, inputStream);
    }

    /**
     * write file, the bytes will be written to the begin of the file
     *
     * @param filePath
     * @param stream
     * @return
     * @see {@link #writeFile(String, InputStream, boolean)}
     */
    public static boolean writeFile(String filePath, InputStream stream) {
        return writeFile(filePath, stream, false);
    }

    /**
     * write file
     *
     * @param stream the input stream
     * @param append if <code>true</code>, then bytes will be written to the end
     *               of the file rather than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator
     *                          FileOutputStream
     */
    public static boolean writeFile(String filePath, InputStream stream, boolean append) {
        return writeFile(filePath != null ? new File(filePath) : null, stream, append);
    }

    /**
     * write file, the bytes will be written to the begin of the file
     *
     * @param file
     * @param stream
     * @return
     * @see {@link #writeFile(File, InputStream, boolean)}
     */
    public static boolean writeFile(File file, InputStream stream) {
        return writeFile(file, stream, false);
    }

    /**
     * write file
     *
     * @param file   the file to be opened for writing.
     * @param stream the input stream
     * @param append if <code>true</code>, then bytes will be written to the end
     *               of the file rather than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator
     *                          FileOutputStream
     */
    public static boolean writeFile(File file, InputStream stream, boolean append) {
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (o != null) {
                try {
                    o.close();
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * Creates the directory named by the trailing filename of this file,
     * including the complete directory path required to create this directory. <br/>
     * <br/>
     * <ul>
     * <strong>Attentions:</strong>
     * <li>makeDirs("C:\\Users\\Trinea") can only create users folder</li>
     * <li>makeFolder("C:\\Users\\Trinea\\") can create Trinea folder</li>
     * </ul>
     *
     * @param filePath
     * @return true if the necessary directories have been created or the target
     * directory already exists, false one of the directories can not be
     * created.
     */
    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) || folder.mkdirs();
    }

    /**
     * get folder name from path
     * <p>
     * <pre>
     *      getFolderName(null)               =   null
     *      getFolderName("")                 =   ""
     *      getFolderName("   ")              =   ""
     *      getFolderName("a.mp3")            =   ""
     *      getFolderName("a.b.rmvb")         =   ""
     *      getFolderName("abc")              =   ""
     *      getFolderName("c:\\")              =   "c:"
     *      getFolderName("c:\\a")             =   "c:"
     *      getFolderName("c:\\a.b")           =   "c:"
     *      getFolderName("c:a.txt\\a")        =   "c:a.txt"
     *      getFolderName("c:a\\b\\c\\d.txt")    =   "c:a\\b\\c"
     *      getFolderName("/home/admin")      =   "/home"
     *      getFolderName("/home/admin/a.txt/b.mp3")  =   "/home/admin/a.txt"
     * </pre>
     *
     * @param filePath
     * @return
     */
    public static String getFolderName(String filePath) {

        if (isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }

    /**
     * is null or its length is 0
     * <p>
     * <pre>
     * isEmpty(null) = true;
     * isEmpty(&quot;&quot;) = true;
     * isEmpty(&quot;  &quot;) = false;
     * </pre>
     *
     * @param str
     * @return if string is null or its size is 0, return true, else return
     * false.
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }

    public static void clearDirectory(String path) {
        clearDirectory(new File(path));
    }

    /**
     * 清空文件夹下的所有东西，但保留文件夹
     */
    public static void clearDirectory(File file) {
        if (file == null || !file.exists()) {
            return;
        } else {
            if (file.isFile()) {
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    return;
                }
                for (File f : childFile) {
                    deleteFile(f);
                }
            }
        }
    }

    public static void deleteFile(String path) {
        deleteFile(new File(path));
    }

    /*
     * 删除文件和文件夹
     * @param file 要的根目录
     */
    public static void deleteFile(File file) {
        if (file == null || !file.exists()) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    deleteFile(f);
                }
                file.delete();
            }
        }
    }

    /**
     * 获取指定文件夹的大小
     */
    public static long getFolderSize(File f) {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFolderSize(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 获取指定文件大小
     */
    public static long getFileSize(File file) {
        long size = 0;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            size = fis.available();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return size;
    }

    /**
     * 获取文件的名称
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {

        if (isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(filePosi+1, filePath.length());
    }

    /**
     * 获取文件的后缀
     * @param filePath
     * @return
     */
    public static String getFileType(String filePath) {

        if (isEmpty(filePath)) {
            return "";
        }

        int filePosi = filePath.lastIndexOf(".");
        return (filePosi == -1) ? "" : filePath.substring(filePosi+1, filePath.length());
    }

}


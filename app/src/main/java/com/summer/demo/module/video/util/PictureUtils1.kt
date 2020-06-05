package com.summer.demo.module.video.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * ================================================
 * 作    者：顾修忠-guxiuzhong@youku.com/gfj19900401@163.com
 * 版    本：
 * 创建日期：2017/4/4-下午6:56
 * 描    述：
 * 修订历史：
 * ================================================
 */

object PictureUtils {
    val POSTFIX = ".jpeg"
    private val EDIT_PATH = "/EditVideo/"

    fun saveImageToSDForEdit(bmp: Bitmap?, dirPath: String, fileName: String): String {
        if (bmp == null) {
            return ""
        }
        val appDir = File(dirPath)
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        val file = File(appDir, fileName)
        try {
            val fos = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file.absolutePath
    }

    fun deleteFile(f: File) {
        if (f.isDirectory) {
            val files = f.listFiles()
            if (files != null && files.size > 0) {
                for (i in files.indices) {
                    deleteFile(files[i])
                }
            }
        }
        f.delete()
    }

    fun getSaveEditThumbnailDir(context: Context): String {
        val state = Environment.getExternalStorageState()
        val rootDir = if (state == Environment.MEDIA_MOUNTED) Environment.getExternalStorageDirectory() else context.cacheDir
        val folderDir = File(rootDir.absolutePath + EDIT_PATH)
        if (!folderDir.exists() && folderDir.mkdirs()) {

        }
        return folderDir.absolutePath
    }

}

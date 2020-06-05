package com.summer.demo.helper

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ProgressBar

import com.summer.demo.AppContext
import com.summer.demo.R
import com.summer.demo.dialog.BaseTipsDialog
import com.summer.helper.downloader.DownloadManager
import com.summer.helper.downloader.DownloadStatus
import com.summer.helper.downloader.DownloadTask
import com.summer.helper.downloader.DownloadTaskListener
import com.summer.helper.server.EasyHttp
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SFileUtils
import com.summer.helper.utils.SUtils
import com.summer.zipparser.OnProgressListener
import com.summer.zipparser.ZipPaser

import java.io.File

/**
 * Created by xiastars on 2017/7/14.
 */

object FFMepgHelper {

    //下载链接
    val DOWNLOAD_URL = "https://file.fensixingqiu.com/Android/src/datas.zip"

    fun initFFMepg() {
        init()
    }

    private fun init() {
        if (checkFFMepgInit()) {
            return
        }
        if (SUtils.getNetWorkType(AppContext.instance) != SUtils.NetState.WIFI) {
            return
        }
        val savePath = SFileUtils.getFileDirectory() + "datas.zip"
        EasyHttp.download(AppContext.instance, DOWNLOAD_URL, SFileUtils.getFileDirectory(), "datas.zip", object : DownloadTaskListener {
            override fun onDownloading(downloadTask: DownloadTask) {
                if (downloadTask.downloadStatus == DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                    zipFile(savePath)
                }
            }

            override fun onPause(downloadTask: DownloadTask) {

            }

            override fun onError(downloadTask: DownloadTask, errorCode: Int) {

            }
        })

    }

    /**
     * 先弹窗再下载
     *
     * @param context
     * @return
     */
    fun initWithDialog(context: Context): Boolean {
        if (checkFFMepgInit()) {
            return true
        }
        val tipDialog = BaseTipsDialog(context, "您缺失视频压缩插件，请点击下载，建议在WiFi环境在下载哦", object : BaseTipsDialog.DialogAfterClickListener {

            override fun onSure() {
                showDownloadingDialog(context)
            }

            override fun onCancel() {

            }
        })
        tipDialog.hideTitle()
        tipDialog.okContent = "下载"
        tipDialog.show()
        return false
    }

    private fun showDownloadingDialog(context: Context) {
        val tipDialog = BaseTipsDialog(context, R.layout.dialog_downloading, object : BaseTipsDialog.DialogAfterClickListener {

            override fun onSure() {

            }

            override fun onCancel() {

            }
        })
        tipDialog.show()
        val load_pb = tipDialog.findViewById<View>(R.id.load_pb) as ProgressBar
        val savePath = SFileUtils.getFileDirectory() + "datas.zip"
        val isDownling = EasyHttp.existDownload(context, DOWNLOAD_URL)
        Logs.i("isDonwloadiNg:$isDownling")
        val downloadTaskListener = object : DownloadTaskListener {
            override fun onDownloading(downloadTask: DownloadTask) {
                val progress = downloadTask.percent
                (context as Activity).runOnUiThread { load_pb.progress = progress.toInt() }
                Logs.i("progress:$progress")
                if (downloadTask.downloadStatus == DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                    zipFile(savePath)
                    tipDialog.cancel()
                }
            }

            override fun onPause(downloadTask: DownloadTask) {

            }

            override fun onError(downloadTask: DownloadTask, errorCode: Int) {

            }
        }
        tipDialog.setTitle("正在下载中...")
        //判断是否正在下载中
        if (isDownling) {
            Logs.i("xxxxx")
            val manager = DownloadManager.getInstance(context)
            manager.addDownloadListener(manager.getCurrentTaskById(DOWNLOAD_URL), downloadTaskListener)
            return
        }
        SFileUtils.deleteFile(savePath)
        EasyHttp.download(AppContext.instance, DOWNLOAD_URL, SFileUtils.getFileDirectory(), "datas.zip", downloadTaskListener)

    }

    /**
     * 检查有没有初始化好
     *
     * @return
     */
    fun checkFFMepgInit(): Boolean {
        val path = SFileUtils.getFileDirectory() + "armeabi-v7a_ffmpeg"
        val file = File(path)
        if (file.exists()) {
            Logs.i("FFMepg已加载")
            return true
        }
        return false
    }

    /**
     * 将下载的zip包解压
     *
     * @param filePath
     */
    private fun zipFile(filePath: String) {
        val zipPaser = ZipPaser(AppContext.instance, OnProgressListener { size -> Logs.i("ffmpeg加载完毕$size") })
        try {
            zipPaser.UnZipFolder(filePath, SFileUtils.getFileDirectory())
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}

package com.summer.demo.utils

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Message
import android.widget.ProgressBar
import android.widget.TextView

import com.summer.demo.R
import com.summer.demo.bean.BookBean
import com.summer.helper.db.CommonService
import com.summer.helper.downloader.DownloadManager
import com.summer.helper.downloader.DownloadStatus
import com.summer.helper.downloader.DownloadTask
import com.summer.helper.downloader.DownloadTaskListener
import com.summer.helper.server.EasyHttp
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SFileUtils
import com.summer.helper.utils.SUtils

import java.io.File
import java.lang.ref.WeakReference

class AppBeansHelper(private val context: Context, private val mProgressBar: ProgressBar,
                     private val mDownload: TextView, internal var listener: BookDownloadedListener) {

    private var mBookBean: BookBean? = null
    internal var SAVE_VERSION: String? = null
    internal var mDownloadManager: DownloadManager? = null
    internal var myHandler: MyHandler
    internal lateinit var commonService: CommonService
    internal var downloadIndex = 0
    /* 图书名称 */
    internal lateinit var bookName: String
    /* 保存的目录名 */
    internal lateinit var directory: String
    /* 下载链接 */
    internal lateinit var downloadUrl: String

    internal var isFirst = false
    internal var downloadCount = 0

    init {
        myHandler = MyHandler(this)
    }

    fun setDownloadManager(downloadManager: DownloadManager) {
        this.mDownloadManager = downloadManager
    }

    fun setEntity(appsEn: BookBean) {
        this.mBookBean = appsEn
        SAVE_VERSION = appsEn.apk_url
        bookName = appsEn.name
        mProgressBar.max = 100
        directory = SFileUtils.getBookDirectory() + bookName + "/"
        downloadUrl = appsEn.apk_url
        initData()
    }

    private fun initData() {
        val downloadTask = mDownloadManager!!.getCurrentTaskById(downloadUrl)
        if (downloadTask != null) {
            downloadTask.addDownloadListener(object : DownloadTaskListener {
                override fun onDownloading(downloadTask: DownloadTask) {
                    myHandler.sendEmptyMessage(0)
                }

                override fun onPause(downloadTask: DownloadTask) {

                }

                override fun onError(downloadTask: DownloadTask, errorCode: Int) {

                }
            })
        } else {
            loadStatusChange()
        }
    }

    /**
     * 改变 界面显示下载状态 和文字
     */
    fun loadStatusChange() {
        if (null == mBookBean) return
        (context as Activity).runOnUiThread {
            transAppBeanToAppEntity(mBookBean)
            mProgressBar.progress = mBookBean!!.progress
            val status = mBookBean!!.status
            mDownload.setTextColor(context.getResources().getColor(R.color.black))
            when (status) {
                DownloadStatus.DOWNLOAD_STATUS_ERROR // 失败——》重新加载
                -> {
                    setProgressDrawable(R.drawable.progress_downloading)
                    mDownload.text = "重试"
                }
                DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING -> {
                    setProgressDrawable(R.drawable.progress_downloading)
                    val content = mBookBean!!.progress.toString() + "%"
                    mDownload.text = content
                }
                DownloadStatus.DOWNLOAD_STATUS_COMPLETED -> {
                    setProgressDrawable(R.drawable.progress_open)
                    mDownload.setTextColor(context.getResources().getColor(R.color.white))
                    mDownload.text = "打开"
                }
                DownloadStatus.DOWNLOAD_STATUS_PAUSE -> {
                    setProgressDrawable(R.drawable.progress_pause)
                    mDownload.text = "继续"
                }
                DownloadStatus.DOWNLOAD_STATUS_INIT -> {
                    setProgressDrawable(R.drawable.progress_pause)
                    mDownload.text = "下载"
                }
            }
        }
    }

    private fun setProgressDrawable(id: Int) {
        mProgressBar.progressDrawable = context.resources.getDrawable(id)
    }

    /**
     * 将AppBean转为BookBean
     *
     * @return
     */
    private fun transAppBeanToAppEntity(bookBean: BookBean?) {
        val downloadTask = mDownloadManager!!.getCurrentTaskById(downloadUrl)
        if (downloadTask != null) {
            bookBean!!.status = downloadTask.downloadStatus
            bookBean.progress = downloadTask.percent.toInt()
        }
        if (checkFileExist()) {
            bookBean!!.status = DownloadStatus.DOWNLOAD_STATUS_COMPLETED
        }
    }

    /**
     * 检查文件是否已存在在本地
     * @return
     */
    private fun checkFileExist(): Boolean {
        val file = File(directory, bookName + SFileUtils.FileType.FILE_APK)
        return if (file.exists()) {
            true
        } else false
    }

    /**
     * @return
     */
    fun startDownload(): Boolean {
        if (checkFileExist()) {
            SUtils.makeToast(context, "文件已下载")
            return false
        }
        val downloadTask = mDownloadManager!!.getCurrentTaskById(downloadUrl)
        if (downloadTask != null) {
            if (downloadTask.downloadStatus == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING) {
                EasyHttp.pauseDownload(context, downloadUrl)
                loadStatusChange()
                return false
            }
        }
        downloadData()
        return false
    }

    private fun downloadData() {
        SUtils.makeToast(context, downloadUrl)
        EasyHttp.download(context, downloadUrl, downloadUrl,
                directory, bookName + SFileUtils.FileType.FILE_APK, object : DownloadTaskListener {

            override fun onError(downloadTask: DownloadTask, errorCode: Int) {
                loadStatusChange()
            }

            override fun onDownloading(downloadTask: DownloadTask) {
                Logs.i("xia", ".." + downloadTask.percent + ",status:" + downloadTask.downloadStatus)
                myHandler.sendEmptyMessage(0)
            }

            override fun onPause(downloadTask: DownloadTask) {
                Logs.i("xia", downloadTask.percent.toString() + "")
                loadStatusChange()
            }
        })
    }

    fun setCommonService(commonService: CommonService) {
        this.commonService = commonService
    }

    class MyHandler(activity: AppBeansHelper) : Handler() {
        private val mActivity: WeakReference<AppBeansHelper>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity = mActivity.get()
            if (null != activity) {
                when (msg.what) {
                    0 -> activity.loadStatusChange()
                    1 -> {
                    }
                }
            }
        }
    }

    interface BookDownloadedListener {
        fun onCallback(bean: BookBean)
    }

    companion object {
        var isOnAnim = false

        /**
         * rename 文件重命名
         *
         * @param to
         * @param from
         * @return
         */
        fun rename(from: File, to: File): File {
            try {
                val newPath = to.path
                val oldPath = from.path
                if (oldPath != newPath) {
                    if (!to.exists()) {
                        from.renameTo(to)
                    }
                }
            } catch (ex: Exception) {
                Logs.i("Exception:$ex")
            }

            return to
        }
    }


}

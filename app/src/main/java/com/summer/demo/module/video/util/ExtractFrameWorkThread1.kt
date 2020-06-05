package com.summer.demo.module.video.util

import android.os.Handler

/**
 * ================================================
 * 作    者：顾修忠-guxiuzhong@youku.com/gfj19900401@163.com
 * 版    本：
 * 创建日期：2017/3/2-下午7:53
 * 描    述：
 * 修订历史：
 * ================================================
 */

class ExtractFrameWorkThread(extractW: Int, extractH: Int, mHandler: Handler, private val videoPath: String, private val OutPutFileDirPath: String,
                             private val startPosition: Long, private val endPosition: Long, private val thumbnailsCount: Int) : Thread() {
    private val mVideoExtractFrameAsyncUtils: VideoExtractFrameAsyncUtils?

    init {
        this.mVideoExtractFrameAsyncUtils = VideoExtractFrameAsyncUtils(extractW, extractH, mHandler)
    }

    override fun run() {
        super.run()
        mVideoExtractFrameAsyncUtils!!.getVideoThumbnailsInfoForEdit(
                videoPath,
                OutPutFileDirPath,
                startPosition,
                endPosition,
                thumbnailsCount)
    }

    fun stopExtract() {
        mVideoExtractFrameAsyncUtils?.stopExtract()
    }

    companion object {
        val MSG_SAVE_SUCCESS = 0
    }
}

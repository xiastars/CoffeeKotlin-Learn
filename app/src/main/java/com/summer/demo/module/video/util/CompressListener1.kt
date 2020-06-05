package com.summer.demo.module.video.util

/**
 * Created by karan on 13/2/15.
 */
interface CompressListener {
    fun onExecSuccess(message: String)

    fun onExecFail(reason: String)

    fun onExecProgress(message: String)
}

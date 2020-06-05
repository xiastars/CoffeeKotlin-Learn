package com.summer.demo.module.video.util

import android.net.Uri

/**
 * Created by dell on 2017/6/28.
 */

interface OnTrimVideoListener {
    fun onTrimStarted()

    fun getResult(uri: Uri)

    fun cancelAction()

    fun onError(message: String)
}

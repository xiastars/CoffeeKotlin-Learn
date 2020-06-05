package com.summer.demo.module.video.util

/**
 * Created by karan on 13/2/15.
 */
interface InitListener {
    fun onLoadSuccess()

    fun onLoadFail(reason: String)
}

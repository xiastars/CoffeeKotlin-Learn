package com.summer.demo.module.album

import android.content.Context
import android.webkit.JavascriptInterface

import com.summer.demo.module.album.util.ImageItem
import com.summer.demo.module.video.ViewVideoActivity
import com.summer.helper.utils.JumpTo
import com.summer.helper.utils.SUtils
import com.summer.helper.web.WebContainerActivity

/**
 * 用于打开网页中的视频与图片
 * Created by xiastars on 2017/8/26.
 */

class MediaHandleJavascriptInterface(private val context: Context) {

    @JavascriptInterface
    fun openImage(img: String) {
        if (!SUtils.isEmptyArrays(webImsg)) {
            val count = webImsg!!.size
            for (i in 0 until count) {
                val item = webImsg!![i]
                val url = item.img
                if (url != null && url == img) {
                    ViewBigPhotoActivity.show(context, webImsg!!, i)
                    return
                }
            }
        }
        JumpTo.getInstance().commonJump(context, ViewBigPhotoActivity::class.java, img)
    }

    @JavascriptInterface
    fun openVideo(img: String) {
        JumpTo.getInstance().commonJump(context, ViewVideoActivity::class.java, img)
    }

    @JavascriptInterface
    fun openUrl(url: String) {
        JumpTo.getInstance().commonJump(context, WebContainerActivity::class.java, url)
    }

    companion object {
        var webImsg: MutableList<ImageItem>? = null//暂时没有合适的方法处理WEBIVEW里的图片大图浏览轮滑，先这样处理
    }

}

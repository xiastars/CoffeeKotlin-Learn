package com.summer.demo.adapter

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView

/**
 * Created with IntelliJ IDEA.
 * User: sash0k
 * Date: 07.06.13
 * Time: 11:15
 * Для возможности вертикального скролла на Android 2.x
 * see http://stackoverflow.com/a/9925980
 */
class ExtendedWebView : WebView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    fun canScrollVertical(direction: Int): Boolean {
        val offset = computeVerticalScrollOffset()
        val range = computeVerticalScrollRange() - computeVerticalScrollExtent()
        return if (range == 0)
            false
        else
            if (direction < 0) offset > 0 else offset < range - 1
    }
}


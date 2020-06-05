package com.summer.demo.utils

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

open class NfClickableSpan(colorRes: Int) : ClickableSpan() {
    internal var DEFAULT_COLOR = Color.parseColor("#617AB5")

    init {
        this.DEFAULT_COLOR = colorRes
    }

    override fun onClick(widget: View) {

    }

    override fun updateDrawState(ds: TextPaint) {
        ds.color = DEFAULT_COLOR
        ds.isUnderlineText = false
    }
}

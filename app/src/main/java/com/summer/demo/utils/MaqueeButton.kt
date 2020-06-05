package com.summer.demo.utils

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.Button

/**
 * 滚动的字符
 *
 * @author 无名英雄
 */
class MaqueeButton(context: Context, attrs: AttributeSet) : Button(context, attrs) {

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFoRect: Rect?) {
        if (focused)
            super.onFocusChanged(focused, direction, previouslyFoRect)

    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        if (hasWindowFocus)
            super.onWindowFocusChanged(hasWindowFocus)
    }

    override fun isFocused(): Boolean {
        return true
    }

}
package com.summer.demo.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

import com.summer.demo.R
import com.summer.helper.utils.SUtils

class CommonSureView : TextView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        SUtils.clickTransColor(this)
    }

    fun changeStyle(enable: Boolean) {
        this.isEnabled = enable
        if (enable) {
            this.setBackgroundResource(R.drawable.so_blue56_8)
            this.setTextColor(context.resources.getColor(R.color.white))
        } else {
            this.setBackgroundResource(R.drawable.so_greyd2_8)
            this.setTextColor(context.resources.getColor(R.color.grey_95))
        }
    }
}

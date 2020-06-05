package com.summer.demo.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

import com.summer.demo.R

class CommonSureView5 : TextView {

    var isNeedBackgroud = true
    var enableColor: Int = 0

    constructor(context: Context) : super(context) {
        init(false)
    }

    private fun init(b: Boolean) {
        changeStyle(b)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(false)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(false)
    }

    fun changeStyle(enable: Boolean) {
        if (enable) {
            this.isEnabled = true
            if (isNeedBackgroud) {
                this.setBackgroundResource(R.drawable.so_blue56_5)
            }
            this.setTextColor(context.resources.getColor(if (enableColor != 0) enableColor else R.color.white))
        } else {
            this.isEnabled = false
            if (isNeedBackgroud) {
                this.setBackgroundResource(R.drawable.so_greyd2_5)
            }
            this.setTextColor(context.resources.getColor(R.color.grey_95))
        }
        if (!isNeedBackgroud) {
            this.setBackgroundResource(R.drawable.trans)
        }
    }
}

package com.summer.demo.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.LinearLayout
import android.widget.ScrollView

import com.summer.demo.R

class CustomScrollView : ScrollView {
    private var llTop: LinearLayout? = null
    private var mTopHeight: Int = 0
    private var downX: Int = 0
    private var downY: Int = 0
    private var mTouchSlop: Int = 0


    constructor(context: Context) : super(context) {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        val action = e.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                downX = e.rawX.toInt()
                downY = e.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val moveY = e.rawY.toInt()
                if (Math.abs(moveY - downY) > mTouchSlop) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(e)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        if (null != llTop) {
            if (mTopHeight > t || oldt < 0) {
                llTop!!.alpha = 1f
                llTop!!.visibility = View.VISIBLE
            } else {
                if (t > mTopHeight) {
                    var alpha = 1 - (t - mTopHeight) / mTopHeight.toFloat()
                    if (alpha <= 0) {
                        alpha = 0f
                        llTop!!.visibility = View.GONE
                    } else {
                        llTop!!.visibility = View.VISIBLE
                    }
                    llTop!!.alpha = alpha
                }
            }
        }
        super.onScrollChanged(l, t, oldl, oldt)
    }

    fun setTopView(llTop: LinearLayout) {
        this.llTop = llTop
        this.mTopHeight = context.resources.getDimension(R.dimen.size_100).toInt()
    }

}

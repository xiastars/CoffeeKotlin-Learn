package com.summer.demo.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ProgressBar

/**
 * Created by xiastars on 2017/11/8.
 */

class VerticalProgressBar : ProgressBar {

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context) : super(context) {}

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(h, w, oldh, oldw)
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        canvas.rotate(-90f)
        canvas.translate((-height).toFloat(), 0f)
        super.onDraw(canvas)
    }

}
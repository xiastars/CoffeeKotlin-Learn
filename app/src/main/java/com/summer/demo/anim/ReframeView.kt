package com.summer.demo.anim

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * 拥有四边框的ImageView
 * @author @xiastars@vip.qq.com
 */
class ReframeView : View {
    internal var left: Int = 0
    internal var right: Int = 0
    internal var top: Int = 0
    internal var bottom: Int = 0

    internal var linePaint: Paint? = null


    constructor(context: Context) : super(context) {}

    constructor(context: Context, atri: AttributeSet) : super(context, atri) {}

    override fun onDraw(canvas: Canvas) {
        dragLine(canvas)
        super.onDraw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.i("xia", "$left,,$top,,$right")
    }

    private fun dragLine(canvas: Canvas) {
        if (linePaint == null) {
            linePaint = Paint()
            linePaint!!.style = Paint.Style.STROKE
            linePaint!!.strokeWidth = 5f
            linePaint!!.maskFilter = BlurMaskFilter(100f, BlurMaskFilter.Blur.SOLID)
            linePaint!!.color = Color.parseColor("#2b6a9b")
        }
        Log.i("xia", getLeft().toString() + ",,")
        val path = Path()
        path.moveTo((left - 10).toFloat(), (top - 10).toFloat())
        path.lineTo((right + 10).toFloat(), (top - 10).toFloat())
        //虚线效果
        //        PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
        //        linePaint.setPathEffect(effects);
        canvas.drawPath(path, linePaint!!)
        path.moveTo((right + 10).toFloat(), (top - 10).toFloat())
        path.lineTo((right + 10).toFloat(), (bottom - 10).toFloat())
        canvas.drawPath(path, linePaint!!)
        path.moveTo((left - 10).toFloat(), (bottom + 10).toFloat())
        path.lineTo((right + 10).toFloat(), (bottom + 10).toFloat())
        canvas.drawPath(path, linePaint!!)
        path.moveTo((left - 10).toFloat(), (top - 10).toFloat())
        path.lineTo((left - 10).toFloat(), (bottom + 10).toFloat())
        canvas.drawPath(path, linePaint!!)
    }

    fun setData(left2: Int, right2: Int, top2: Int, bottom2: Int) {
        this.left = left2
        this.right = right2
        this.top = top2
        this.bottom = bottom2
        invalidate()
        requestLayout()
    }

}

package com.summer.demo.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/6/13 15:50
 */
class CanvasAnimView : View {

    internal lateinit var showBitmap: Bitmap
    internal lateinit var showCanvas: Canvas

    internal var radius: Int = 0

    internal var index: Int = 0

    //圆心坐标
    private var centerX: Int = 0
    private var centerY: Int = 0

    internal var circlePaint: Paint? = null
    internal var showPaint: Paint? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = measuredWidth
        val height = measuredHeight

        radius = Math.min(width, height) / 2
        centerX = width / 2
        centerY = height / 2
        //秒针
        showBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        showCanvas = Canvas(showBitmap)


    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (null == circlePaint) {
            circlePaint = Paint()
            circlePaint!!.isAntiAlias = true
            circlePaint!!.style = Paint.Style.FILL
            circlePaint!!.color = Color.BLUE
        }
        if (null == showPaint) {
            showPaint = Paint()
            showPaint!!.isAntiAlias = true
            showPaint!!.color = Color.RED
            showPaint!!.style = Paint.Style.FILL
            showPaint!!.strokeCap = Paint.Cap.ROUND
            showPaint!!.strokeWidth = 5f
        }
        showCanvas.save()
        //绘制圆
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), radius.toFloat(), circlePaint!!)

        showCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        showCanvas.rotate((index * 30).toFloat(), centerX.toFloat(), centerY.toFloat())
        showCanvas.drawLine(centerX.toFloat(), centerY.toFloat(),
                centerX.toFloat(), (centerY - 100).toFloat(), showPaint!!)
        showCanvas.restore()

        canvas.drawBitmap(showBitmap, 0f, 0f, null)

    }

    fun setIndex() {
        index++
        if (index >= 60) {
            index = 0
        }
        invalidate()
    }
}

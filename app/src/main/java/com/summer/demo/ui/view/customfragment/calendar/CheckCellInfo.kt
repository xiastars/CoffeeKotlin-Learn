package com.summer.demo.ui.view.customfragment.calendar

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build

import com.summer.demo.R
import com.summer.helper.utils.SUtils

/**
 * Created by xiastars on 2017/9/8.
 */

class CheckCellInfo(internal var context: Context) {
    var content: String  = ""//显示内容
    var isValided: Boolean = false//是否可以点击
    var isToday: Boolean = false//是否是今天
    var isWeekend: Boolean = false//是否星期头部
    var originDay: String = ""
    var isChecked: Boolean = false//是否选中

    internal lateinit var cellRecct: Rect
    internal lateinit var canvas: Canvas
    internal lateinit var paint: Paint

    internal var paddingLeft: Int = 0
    internal var paddingTop: Int = 0
    internal var ovalWidth: Int = 0

    init {
        ovalWidth = SUtils.getDip(context, 24)
    }

    fun getCellRecct(): Rect {
        return cellRecct
    }

    fun setCellRecct(cellRecct: Rect) {
        this.cellRecct = cellRecct
        paddingLeft = (cellRecct.right - cellRecct.left - ovalWidth) / 2
        paddingTop = (cellRecct.bottom - cellRecct.top - ovalWidth) / 2
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun onDraw(canvas: Canvas, paint: Paint) {
        this.canvas = canvas
        this.paint = paint
        val fontMetrics = paint.fontMetricsInt
        val baseline = (cellRecct.bottom + cellRecct.top - fontMetrics.bottom - fontMetrics.top) / 2
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()

        if (isChecked) {
            try {
                paint.color = context.resources.getColor(R.color.red_d4)
                canvas.drawOval((cellRecct.left + paddingLeft).toFloat(), (cellRecct.top + paddingTop).toFloat(), (cellRecct.right - paddingLeft).toFloat(), (cellRecct.bottom - paddingTop).toFloat(), paint)
            } catch (e: NoSuchMethodError) {
                e.printStackTrace()
            }

            paint.color = Color.WHITE
        } else {

            if (isValided) {
                paint.color = Color.BLACK
            } else {
                paint.color = context.resources.getColor(R.color.grey_99)
            }
        }
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(content, cellRecct.centerX().toFloat(), baseline.toFloat(), paint)
        if (isToday) {
            paint.color = context.resources.getColor(R.color.red_d4)
            val left = cellRecct.left + paddingLeft + ovalWidth / 2 - SUtils.getDip(context, 1.5f)
            val top = (cellRecct.top + paddingTop + ovalWidth).toFloat()
            canvas.drawOval(left, top, left + SUtils.getDip(context, 3f), top + SUtils.getDip(context, 3), paint)
        }
        canvas.save()
    }

}

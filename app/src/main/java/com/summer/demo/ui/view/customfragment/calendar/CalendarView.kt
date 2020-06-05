package com.summer.demo.ui.view.customfragment.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.annotation.AttrRes
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.summer.demo.R
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SUtils
import java.text.ParseException
import java.util.*

/**
 * Created by xiastars on 2017/9/8.
 */

class CalendarView : FrameLayout, ViewGroup.OnHierarchyChangeListener {
    internal lateinit var activity: Context
    internal lateinit var mPaint: Paint
    internal var cells: MutableMap<String, CheckCellInfo>? = HashMap()

    internal var ismOnDraw: Boolean = false

    internal lateinit var downPoint: Point
    internal var cellWith: Int = 0
    internal var cellHeight: Int = 0
    internal var mMotionDownX: Int = 0
    internal var mMotionDownY: Int = 0

    internal var curMonth: Int = 0//当前月

    @SuppressLint("NewApi")
    constructor(context: Context) : super(context) {
        this.activity = context
        isMotionEventSplittingEnabled = false
        isChildrenDrawingOrderEnabled = true
        setOnHierarchyChangeListener(this)
        init()
    }

    fun setActivity(activity: Context) {
        this.activity = activity
    }

    @SuppressLint("NewApi")
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        isMotionEventSplittingEnabled = false
        isChildrenDrawingOrderEnabled = true
        setOnHierarchyChangeListener(this)
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.textSize = context.resources.getDimension(R.dimen.text_24)
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.BLACK
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (cells != null && !ismOnDraw) {
            ismOnDraw = true
            drawCells(canvas)
        }
    }

    private fun drawCells(canvas: Canvas) {

        for ((_, cellInfo) in cells!!) {
            cellInfo.onDraw(canvas, mPaint)
        }
        ismOnDraw = false
    }

    /**
     * 创建所有格子
     */
    fun creatCells(yearAndMonth: IntArray, curMonth: Int) {
        this.curMonth = curMonth
        val year = yearAndMonth[0]
        val month = yearAndMonth[1]
        var calendar: Calendar? = null
        try {
            calendar = Calendar.getInstance()
            calendar!!.time = Date(SUtils.getTime(year.toString() + "" + month))

        } catch (e: ParseException) {
            e.printStackTrace()
        }

        Logs.i("--------")
        val lastDay = calendar!!.getMaximum(Calendar.DAY_OF_MONTH)
        val today = calendar.get(Calendar.DAY_OF_MONTH)
        cellWith = SUtils.screenWidth / 7
        cellHeight = (cellWith * 0.7f).toInt()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val weekend = calendar.get(Calendar.DAY_OF_WEEK)
        val shouldAdd = weekend - 1//需要添加几个空白页面
        val days = arrayOf("日", "一", "二", "三", "四", "五", "六")
        var index = 0
        for (y in 0..6) {
            for (x in 0..6) {
                val info = CheckCellInfo(context)
                if (y == 0) {
                    info.isWeekend = true
                    info.content = days[x] + ""
                    info.isValided = false
                } else {
                    index++
                    if (index > shouldAdd) {
                        info.content = (index - shouldAdd).toString() + ""
                    } else {
                        info.content = ""
                    }
                    if (index - shouldAdd == today && curMonth == month) {//设置为今天
                        info.isToday = true
                    }
                    if (curMonth == month && index - shouldAdd > today || TextUtils.isEmpty(info.content)) {
                        info.isValided = false
                    } else {
                        info.isValided = true
                    }
                }
                if (index > lastDay + shouldAdd) {
                    break
                }
                val rect = Rect()
                rect.left = cellWith * x
                rect.right = rect.left + cellWith
                rect.top = cellHeight * y
                rect.bottom = rect.top + cellHeight
                info.setCellRecct(rect)
                cells!!["$x-$y"] = info
            }
        }
        invalidate()
        requestLayout()
    }


    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var x = event.x.toInt()
        var y = event.y.toInt()
        Logs.i("xia", event.action.toString() + ",,," + x + ",,," + y)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> downPoint = Point(x, y)
            MotionEvent.ACTION_MOVE -> if (0 == mMotionDownX && 0 == mMotionDownY) {
                mMotionDownX = x
                mMotionDownY = y
            }
            MotionEvent.ACTION_UP -> {
                x = event.x.toInt()
                y = event.y.toInt()
                handlerClick(x, y)
            }
            MotionEvent.ACTION_CANCEL -> {
            }
        }
        return super.onTouchEvent(event)
    }

    private fun handlerClick(x: Int, y: Int) {

        for ((_, info) in cells!!) {
            Logs.i("info:$info")
            if (info != null) {
                if (!info.isValided) {
                    continue
                }
                Logs.i("x,," + x + ",," + y + ",," + info.getCellRecct())
                if (info.getCellRecct().contains(x, y)) {
                    info.isChecked = !info.isChecked
                    invalidate()
                    requestLayout()
                }

            }
        }

    }

    fun getCellWithId(id: String): CheckCellInfo? {
        return cells!![id]
    }

    override fun onChildViewAdded(parent: View, child: View) {

    }

    override fun onChildViewRemoved(parent: View, child: View) {

    }
}

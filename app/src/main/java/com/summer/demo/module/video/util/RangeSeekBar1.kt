package com.summer.demo.module.video.util

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.summer.demo.R
import com.summer.helper.utils.Logs
import java.text.DecimalFormat


/**
 * ================================================
 * 作    者：顾修忠-guxiuzhong@youku.com/gfj19900401@163.com
 * 版    本：
 * 创建日期：2017/4/4-下午1:22
 * 描    述：
 * 修订历史：
 * ================================================
 */

class RangeSeekBar : View {
    var absoluteMinValuePrim: Double = 0.0
    var absoluteMaxValuePrim: Double = 0.0
    private var normalizedMinValue = 0.0//点坐标占总长度的比例值，范围从0-1
    private var normalizedMaxValue = 1.0//点坐标占总长度的比例值，范围从0-1
    private var min_cut_time: Long = 3000
    private var normalizedMinValueTime = 0.0
    private var normalizedMaxValueTime = 1.0// normalized：规格化的--点坐标占总长度的比例值，范围从0-1
    private var mScaledTouchSlop: Int = 0
    private var thumbImageLeft: Bitmap? = null
    private var thumbImageRight: Bitmap? = null
    private var thumbPressedImage: Bitmap? = null
    private var mBitmapBlack: Bitmap? = null
    private var mBitmapPro: Bitmap? = null
    private var paint: Paint? = null
    private var rectPaint: Paint? = null
    private var thumbWidth: Int = 0
    private var thumbHalfWidth: Float = 0.toFloat()
    private val padding = 0f

    private val thumbPaddingTop = 0f
    private val thumbPressPaddingTop = 0f
    private var isTouchDown: Boolean = false
    private var mActivePointerId = INVALID_POINTER_ID
    private var mDownMotionX: Float = 0.toFloat()
    private var mIsDragging: Boolean = false
    private var pressedThumb: Thumb? = null
    private var isMin: Boolean = false
    private var min_width = 1.0//最小裁剪距离
    /**
     * 供外部activity调用，控制是都在拖动的时候打印log信息，默认是false不打印
     */
    var isNotifyWhileDragging = false

    private val valueLength: Int
        get() = width - 2 * thumbWidth


    var selectedMinValue: Long
        get() = normalizedToValue(normalizedMinValueTime)
        set(value) = if (0.0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
            setNormalizedMinValue(0.0)
        } else {
            setNormalizedMinValue(valueToNormalized(value))
        }

    var selectedMaxValue: Long
        get() = normalizedToValue(normalizedMaxValueTime)
        set(value) = if (0.0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
            setNormalizedMaxValue(1.0)
        } else {
            setNormalizedMaxValue(valueToNormalized(value))
        }

    private var listener: OnRangeSeekBarChangeListener? = null

    enum class Thumb {
        MIN, MAX
    }

    constructor(context: Context, absoluteMinValuePrim: Double) : super(context) {
        this.absoluteMinValuePrim = absoluteMinValuePrim
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    constructor(context: Context, absoluteMinValuePrim: Long, absoluteMaxValuePrim: Long) : super(context) {
        this.absoluteMinValuePrim = absoluteMinValuePrim.toDouble()
        this.absoluteMaxValuePrim = absoluteMaxValuePrim.toDouble()
        isFocusable = true
        isFocusableInTouchMode = true
        init()
    }

    private fun init() {
        mScaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        //等比例缩放图片
        thumbImageLeft = BitmapFactory.decodeResource(resources, R.drawable.handle_left)
        val width = thumbImageLeft!!.width
        val height = thumbImageLeft!!.height
        val newWidth = dip2px(11)
        val newHeight = dip2px(55)
        val scaleWidth = newWidth * 1.0f / width
        val scaleHeight = newHeight * 1.0f / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        thumbImageLeft = Bitmap.createBitmap(thumbImageLeft!!, 0, 0, width, height, matrix, true)
        thumbImageRight = thumbImageLeft
        thumbPressedImage = thumbImageLeft
        thumbWidth = newWidth
        thumbHalfWidth = (thumbWidth / 2).toFloat()


        mBitmapBlack = BitmapFactory.decodeResource(resources, R.drawable.upload_overlay_black)
        mBitmapPro = BitmapFactory.decodeResource(resources, R.drawable.upload_overlay_trans)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        rectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        rectPaint!!.style = Paint.Style.FILL
        rectPaint!!.color = Color.parseColor("#ffffff")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = 300
        if (View.MeasureSpec.UNSPECIFIED != View.MeasureSpec.getMode(widthMeasureSpec)) {
            width = View.MeasureSpec.getSize(widthMeasureSpec)
        }
        var height = 120
        if (View.MeasureSpec.UNSPECIFIED != View.MeasureSpec.getMode(heightMeasureSpec)) {
            height = View.MeasureSpec.getSize(heightMeasureSpec)
        }
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bg_middle_left = 0f
        val bg_middle_right = (width - paddingRight).toFloat()
        val scale = (bg_middle_right - bg_middle_left) / mBitmapPro!!.width

        val rangeL = normalizedToScreen(normalizedMinValue)
        val rangeR = normalizedToScreen(normalizedMaxValue)
        val scale_pro = (rangeR - rangeL) / mBitmapPro!!.width
        if (scale_pro > 0) {
            try {
                val pro_mx = Matrix()
                pro_mx.postScale(scale_pro, 1f)
                val m_bitmap_pro_new = Bitmap.createBitmap(mBitmapPro!!, 0, 0, mBitmapPro!!.width,
                        mBitmapPro!!.height, pro_mx, true)

                //画中间的透明遮罩
                canvas.drawBitmap(m_bitmap_pro_new, rangeL, thumbPaddingTop, paint)

                val mx = Matrix()
                mx.postScale(scale, 1f)
                val m_bitmap_black_new = Bitmap.createBitmap(mBitmapBlack!!, 0, 0, mBitmapBlack!!.width, mBitmapBlack!!.height, mx, true)

                //画左边的半透明遮罩
                val m_bg_new1 = Bitmap.createBitmap(m_bitmap_black_new, 0, 0, (rangeL - bg_middle_left).toInt() + thumbWidth / 2, mBitmapBlack!!.height)
                canvas.drawBitmap(m_bg_new1, bg_middle_left, thumbPaddingTop, paint)

                //画右边的半透明遮罩
                val m_bg_new2 = Bitmap.createBitmap(m_bitmap_black_new, (rangeR - thumbWidth / 2).toInt(), 0, (width - rangeR).toInt() + thumbWidth / 2, mBitmapBlack!!.height)
                canvas.drawBitmap(m_bg_new2, (rangeR - thumbWidth / 2).toInt().toFloat(), thumbPaddingTop, paint)

                //画上下的矩形
                canvas.drawRect(rangeL, thumbPaddingTop, rangeR, thumbPaddingTop + dip2px(2), rectPaint!!)
                canvas.drawRect(rangeL, (height - dip2px(2)).toFloat(), rangeR, height.toFloat(), rectPaint!!)
                //画左右thumb
                drawThumb(normalizedToScreen(normalizedMinValue), false, canvas, true)
                drawThumb(normalizedToScreen(normalizedMaxValue), false, canvas, false)
            } catch (e: Exception) {
                // 当pro_scale非常小，例如width=12，Height=48，pro_scale=0.01979065时，
                // 宽高按比例计算后值为0.237、0.949，系统强转为int型后宽就变成0了。就出现非法参数异常
                Log.e(TAG,
                        "IllegalArgumentException--width=" + mBitmapPro!!.width + "Height=" + mBitmapPro!!.height
                                + "scale_pro=" + scale_pro, e)
            }

        }
    }


    private fun drawThumb(screenCoord: Float, pressed: Boolean, canvas: Canvas, isLeft: Boolean) {
        canvas.drawBitmap(if (pressed) thumbPressedImage else if (isLeft) thumbImageLeft else thumbImageRight, screenCoord - if (isLeft) 0 else thumbWidth, if (pressed) thumbPressPaddingTop else thumbPaddingTop, paint)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isTouchDown) {
            return super.onTouchEvent(event)
        }
        if (event.pointerCount > 1) {
            return super.onTouchEvent(event)
        }

        if (!isEnabled)
            return false
        if (absoluteMaxValuePrim <= min_cut_time) {
            return super.onTouchEvent(event)
        }
        val pointerIndex: Int// 记录点击点的index
        val action = event.action
        Logs.i("xia", "---------------------------")
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                //记住最后一个手指点击屏幕的点的坐标x，mDownMotionX
                mActivePointerId = event.getPointerId(event.pointerCount - 1)
                pointerIndex = event.findPointerIndex(mActivePointerId)
                mDownMotionX = event.getX(pointerIndex)
                // 判断touch到的是最大值thumb还是最小值thumb
                pressedThumb = evalPressedThumb(mDownMotionX)
                if (pressedThumb == null)
                    return super.onTouchEvent(event)
                isPressed = true// 设置该控件被按下了
                onStartTrackingTouch()// 置mIsDragging为true，开始追踪touch事件
                trackTouchEvent(event)
                attemptClaimDrag()
                if (listener != null) {
                    listener!!.onRangeSeekBarValuesChanged(this, selectedMinValue, selectedMaxValue, MotionEvent.ACTION_DOWN, isMin, pressedThumb)
                }
            }
            MotionEvent.ACTION_MOVE -> if (pressedThumb != null) {
                if (mIsDragging) {
                    trackTouchEvent(event)
                } else {
                    // Scroll to follow the motion event
                    pointerIndex = event.findPointerIndex(mActivePointerId)
                    val x = event.getX(pointerIndex)// 手指在控件上点的X坐标
                    // 手指没有点在最大最小值上，并且在控件上有滑动事件
                    if (Math.abs(x - mDownMotionX) > mScaledTouchSlop) {
                        isPressed = true
                        Log.e(TAG, "没有拖住最大最小值")// 一直不会执行？
                        invalidate()
                        onStartTrackingTouch()
                        trackTouchEvent(event)
                        attemptClaimDrag()
                    }
                }
                if (isNotifyWhileDragging && listener != null) {
                    listener!!.onRangeSeekBarValuesChanged(this, selectedMinValue, selectedMaxValue, MotionEvent.ACTION_MOVE, isMin, pressedThumb)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (mIsDragging) {
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                    isPressed = false
                } else {
                    onStartTrackingTouch()
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                }

                invalidate()
                if (listener != null) {
                    listener!!.onRangeSeekBarValuesChanged(this, selectedMinValue, selectedMaxValue, MotionEvent.ACTION_UP, isMin, pressedThumb)
                }
                pressedThumb = null// 手指抬起，则置被touch到的thumb为空
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                val index = event.pointerCount - 1
                // final int index = ev.getActionIndex();
                mDownMotionX = event.getX(index)
                mActivePointerId = event.getPointerId(index)
                invalidate()
            }
            MotionEvent.ACTION_POINTER_UP -> {
                onSecondaryPointerUp(event)
                invalidate()
            }
            MotionEvent.ACTION_CANCEL -> {
                if (mIsDragging) {
                    onStopTrackingTouch()
                    isPressed = false
                }
                invalidate() // see above explanation
            }
            else -> {
            }
        }
        return true
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.action and ACTION_POINTER_INDEX_MASK shr ACTION_POINTER_INDEX_SHIFT

        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == mActivePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mDownMotionX = ev.getX(newPointerIndex)
            mActivePointerId = ev.getPointerId(newPointerIndex)
        }
    }

    private fun trackTouchEvent(event: MotionEvent) {
        if (event.pointerCount > 1) return
        Log.e(TAG, "trackTouchEvent: " + event.action + " x: " + event.x)
        val pointerIndex = event.findPointerIndex(mActivePointerId)// 得到按下点的index
        var x = 0f
        try {
            x = event.getX(pointerIndex)
        } catch (e: Exception) {
            return
        }

        if (Thumb.MIN == pressedThumb) {
            // screenToNormalized(x)-->得到规格化的0-1的值
            setNormalizedMinValue(screenToNormalized(x, 0))
        } else if (Thumb.MAX == pressedThumb) {
            setNormalizedMaxValue(screenToNormalized(x, 1))
        }
    }

    private fun screenToNormalized(screenCoord: Float, position: Int): Double {
        val width = width
        if (width <= 2 * padding) {
            // prevent division by zero, simply return 0.
            return 0.0
        } else {
            isMin = false
            var current_width = screenCoord.toDouble()
            val rangeL = normalizedToScreen(normalizedMinValue)
            val rangeR = normalizedToScreen(normalizedMaxValue)
            val min = min_cut_time / (absoluteMaxValuePrim - absoluteMinValuePrim) * (width - thumbWidth * 2)

            if (absoluteMaxValuePrim > 5 * 60 * 1000) {//大于5分钟的精确小数四位
                val df = DecimalFormat("0.0000")
                min_width = java.lang.Double.parseDouble(df.format(min))
            } else {
                min_width = Math.round(min + 0.5).toDouble()
            }
            if (position == 0) {
                if (isInThumbRangeLeft(screenCoord, normalizedMinValue, 0.5)) {
                    return normalizedMinValue
                }

                val rightPosition: Float = if (getWidth() - rangeR >= 0) getWidth() - rangeR else 0f
                val left_length = valueLength - rightPosition - min_width


                if (current_width > rangeL) {
                    current_width = rangeL + (current_width - rangeL)
                } else if (current_width <= rangeL) {
                    current_width = rangeL - (rangeL - current_width)
                }

                if (current_width > left_length) {
                    isMin = true
                    current_width = left_length
                }

                if (current_width < thumbWidth * 2 / 3) {
                    current_width = 0.0
                }

                val resultTime = (current_width - padding) / (width - 2 * thumbWidth)
                normalizedMinValueTime = Math.min(1.0, Math.max(0.0, resultTime))
                val result = (current_width - padding) / (width - 2 * padding)
                return Math.min(1.0, Math.max(0.0, result))// 保证该该值为0-1之间，但是什么时候这个判断有用呢？
            } else {
                if (isInThumbRange(screenCoord, normalizedMaxValue, 0.5)) {
                    return normalizedMaxValue
                }

                val right_length = valueLength - (rangeL + min_width)
                if (current_width > rangeR) {
                    current_width = rangeR + (current_width - rangeR)
                } else if (current_width <= rangeR) {
                    current_width = rangeR - (rangeR - current_width)
                }

                var paddingRight = getWidth() - current_width

                if (paddingRight > right_length) {
                    isMin = true
                    current_width = getWidth() - right_length
                    paddingRight = right_length
                }

                if (paddingRight < thumbWidth * 2 / 3) {
                    current_width = getWidth().toDouble()
                    paddingRight = 0.0
                }

                var resultTime = (paddingRight - padding) / (width - 2 * thumbWidth)
                resultTime = 1 - resultTime
                normalizedMaxValueTime = Math.min(1.0, Math.max(0.0, resultTime))
                val result = (current_width - padding) / (width - 2 * padding)
                return Math.min(1.0, Math.max(0.0, result))// 保证该该值为0-1之间，但是什么时候这个判断有用呢？
            }

        }
    }

    /**
     * 计算位于哪个Thumb内
     *
     * @param touchX touchX
     * @return 被touch的是空还是最大值或最小值
     */
    private fun evalPressedThumb(touchX: Float): Thumb? {
        var result: Thumb? = null
        val minThumbPressed = isInThumbRange(touchX, normalizedMinValue, 2.0)// 触摸点是否在最小值图片范围内
        val maxThumbPressed = isInThumbRange(touchX, normalizedMaxValue, 2.0)
        if (minThumbPressed && maxThumbPressed) {
            // 如果两个thumbs重叠在一起，无法判断拖动哪个，做以下处理
            // 触摸点在屏幕右侧，则判断为touch到了最小值thumb，反之判断为touch到了最大值thumb
            result = if (touchX / width > 0.5f) Thumb.MIN else Thumb.MAX
        } else if (minThumbPressed) {
            result = Thumb.MIN
        } else if (maxThumbPressed) {
            result = Thumb.MAX
        }
        return result
    }

    private fun isInThumbRange(touchX: Float, normalizedThumbValue: Double, scale: Double): Boolean {
        // 当前触摸点X坐标-最小值图片中心点在屏幕的X坐标之差<=最小点图片的宽度的一般
        // 即判断触摸点是否在以最小值图片中心为原点，宽度一半为半径的圆内。
        return Math.abs(touchX - normalizedToScreen(normalizedThumbValue)) <= thumbHalfWidth * scale
    }

    private fun isInThumbRangeLeft(touchX: Float, normalizedThumbValue: Double, scale: Double): Boolean {
        // 当前触摸点X坐标-最小值图片中心点在屏幕的X坐标之差<=最小点图片的宽度的一般
        // 即判断触摸点是否在以最小值图片中心为原点，宽度一半为半径的圆内。
        return Math.abs(touchX - normalizedToScreen(normalizedThumbValue) - thumbWidth.toFloat()) <= thumbHalfWidth * scale
    }

    /**
     * 试图告诉父view不要拦截子控件的drag
     */
    private fun attemptClaimDrag() {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
    }

    internal fun onStartTrackingTouch() {
        mIsDragging = true
    }


    internal fun onStopTrackingTouch() {
        mIsDragging = false
    }

    fun setMin_cut_time(min_cut_time: Long) {
        this.min_cut_time = min_cut_time
    }


    private fun normalizedToScreen(normalizedCoord: Double): Float {
        return (paddingLeft + normalizedCoord * (width - paddingLeft - paddingRight)).toFloat()
    }

    private fun valueToNormalized(value: Long): Double {
        return if (0.0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
            0.0
        } else (value - absoluteMinValuePrim) / (absoluteMaxValuePrim - absoluteMinValuePrim)
    }

    fun setNormalizedMinValue(value: Double) {
        normalizedMinValue = Math.max(0.0, Math.min(1.0, Math.min(value, normalizedMaxValue)))
        invalidate()// 重新绘制此view
    }


    fun setNormalizedMaxValue(value: Double) {
        normalizedMaxValue = Math.max(0.0, Math.min(1.0, Math.max(value, normalizedMinValue)))
        invalidate()// 重新绘制此view
    }

    private fun normalizedToValue(normalized: Double): Long {
        return (absoluteMinValuePrim + normalized * (absoluteMaxValuePrim - absoluteMinValuePrim)).toLong()
    }

    fun dip2px(dip: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dip.toFloat() * scale + 0.5f).toInt()
    }

    fun setTouchDown(touchDown: Boolean) {
        isTouchDown = touchDown
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("SUPER", super.onSaveInstanceState())
        bundle.putDouble("MIN", normalizedMinValue)
        bundle.putDouble("MAX", normalizedMaxValue)
        bundle.putDouble("MIN_TIME", normalizedMinValueTime)
        bundle.putDouble("MAX_TIME", normalizedMaxValueTime)
        return bundle
    }

    override fun onRestoreInstanceState(parcel: Parcelable) {
        val bundle = parcel as Bundle
        super.onRestoreInstanceState(bundle.getParcelable("SUPER"))
        normalizedMinValue = bundle.getDouble("MIN")
        normalizedMaxValue = bundle.getDouble("MAX")
        normalizedMinValueTime = bundle.getDouble("MIN_TIME")
        normalizedMaxValueTime = bundle.getDouble("MAX_TIME")
    }

    interface OnRangeSeekBarChangeListener {
        fun onRangeSeekBarValuesChanged(bar: RangeSeekBar, minValue: Long, maxValue: Long, action: Int, isMin: Boolean, pressedThumb: Thumb?)
    }

    fun setOnRangeSeekBarChangeListener(listener: OnRangeSeekBarChangeListener) {
        this.listener = listener
    }

    companion object {
        private val TAG = RangeSeekBar::class.java.simpleName
        val INVALID_POINTER_ID = 255
        val ACTION_POINTER_INDEX_MASK = 0x0000ff00
        val ACTION_POINTER_INDEX_SHIFT = 8
    }
}

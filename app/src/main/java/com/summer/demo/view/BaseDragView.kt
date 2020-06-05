package com.summer.demo.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.RelativeLayout

open class BaseDragView(context: Context) : RelativeLayout(context) {
    /* 宽度 */
    internal var mWidth = 0
    /* 高度 */
    internal var mHeight = 0
    /* 角色所处位置 */
    internal var mCoor: Rect? = null
    /* 是否在触摸状态 */
    internal var mIsPressed = false

    internal var mLongPressHelper: CheckLongPressHelper
    protected var mParams: FrameLayout.LayoutParams? = null

    /* 行动名称 */
    internal var mActionName: String? = null

    val viewLeft: Int
        get() = mParams!!.leftMargin

    val viewTop: Int
        get() = mParams!!.topMargin

    open val coor: Rect?
        get() = null

    fun getmActionName(): String? {
        return mActionName
    }

    fun setmActionName(mActionName: String) {
        this.mActionName = mActionName
    }

    init {
        mLongPressHelper = CheckLongPressHelper(this)
    }

    @SuppressLint("ClickableViewAccessibility", "NewApi")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                performClick()
                if (!mIsPressed) {
                    mIsPressed = true
                    val mScaleAnimation = ValueAnimator.ofFloat(0.85f, scaleX)
                    mScaleAnimation.addUpdateListener { animation ->
                        val value = (animation.animatedValue as Float).toFloat()
                        scaleX = value
                        scaleY = value
                    }
                    mScaleAnimation.interpolator = OvershootInterpolator(1.2f)
                    mScaleAnimation.duration = 300
                    mScaleAnimation.start()
                }

                mLongPressHelper.postCheckForLongPress()
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                mIsPressed = false
                mLongPressHelper.cancelLongPress()
            }
        }
        return false
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mWidth = width
        mHeight = height
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun getmWidth(): Int {
        return mWidth
    }

    fun setmWidth(mWidth: Int) {
        this.mWidth = mWidth
    }

    fun getmHeight(): Int {
        return mHeight
    }

    open fun setLayoutPosition(left: Int, top: Int) {

    }

    fun setmHeight(mHeight: Int) {
        this.mHeight = mHeight
    }

    open fun resizeFrame(mWidth: Int, mHeight: Int) {}
}

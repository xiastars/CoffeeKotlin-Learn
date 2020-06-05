package com.summer.demo.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.widget.RelativeLayout
import com.summer.demo.R
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SUtils

class DragView(context: Context) : BaseDragView(context) {

    /* 带有图片可以缩放的 */
    private var rlPet: RelativeLayout? = null
    private var ivImage: ImageView? = null
    lateinit var onSingleClickListener: SingleClickListener
    lateinit var onDoubleClickListener: DoubleClickListener
    lateinit var onLongClickListener: LongClickListener
    /* 默认背景 */
    internal lateinit var mDefaultIcon: String
    /* 音频正在播放中 */
    internal var mAudioPlaying: Boolean = false
    /* 要进行的动作类型与URL */
    internal var mActionUrl: String? = null
    /* 结束时X轴 */
    internal var endX: Int = 0
    /* 默认情况下，不能拖动，只有选中了才能拖动 */
    var isDragAble: Boolean = false
        internal set
    /* 结束时Y轴 */
    internal var endY: Int = 0
    /* 音频正在录制中 */
    internal var mAudioRecording: Boolean = false
    /* 准备走动阶段 */
    internal var prepareMoving: Boolean = false
    /* 第一次走动提醒 */
    internal var FRIST_MOVE_TIPS = "FRIST_MOVE_TIPS"

    private var resId: Int = 0

    override val coor: Rect?
        get() {
            mCoor = Rect()
            mCoor!!.left = mParams!!.leftMargin
            mCoor!!.right = mParams!!.leftMargin + mWidth
            mCoor!!.top = mParams!!.topMargin
            mCoor!!.bottom = mParams!!.topMargin + mHeight
            return mCoor
        }

    init {
        mLongPressHelper = CheckLongPressHelper(this)
        mParams = this.layoutParams as FrameLayout.LayoutParams?
        if (null == mParams) {
            mParams = FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            setLayoutParams(mParams)
        }
        addChildView()
    }

    private fun addChildView() {
        val view = LayoutInflater.from(context).inflate(R.layout.view_drag, null) as RelativeLayout
        rlPet = view.findViewById<View>(R.id.rl_pet) as RelativeLayout
        ivImage = view.findViewById<View>(R.id.iv_pet) as ImageView
        ivImage!!.scaleType = ScaleType.FIT_XY
        this.addView(view)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    @SuppressLint("NewApi")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                scaleX = 1f
                scaleY = 1f
                Logs.i("DragReletiveLayout -- > ACTION_CANCEL")
                // mDragLayer.checkOnScaleButton((int)event.getX(),
                // (int)event.getY());
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
                    mScaleAnimation.duration = 100
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

    override fun cancelLongPress() {
        mIsPressed = false
        mLongPressHelper.cancelLongPress()
    }

    fun setLayoutPosition(x: Int, y: Int, width: Int, height: Int) {
        if (null != mParams) {
            val p = ivImage!!.layoutParams

            p.width = width
            p.height = height
            mParams!!.leftMargin = x
            mParams!!.topMargin = y
            requestLayout()
            invalidate()
        }
    }

    fun setRuleBottomRight() {

    }

    override fun setLayoutPosition(x: Int, y: Int) {
        if (null != mParams) {
            mParams!!.leftMargin = x
            mParams!!.topMargin = y
            requestLayout()
            invalidate()
        }
    }

    /**
     * 设置默认资源图片
     */
    fun setDefalultIcon() {
        if (!TextUtils.isEmpty(mDefaultIcon)) {
            SUtils.setPic(ivImage, mDefaultIcon, false)
        }
    }

    fun setDefalultIcon(resId: Int) {
        this.resId = resId
        ivImage!!.setBackgroundResource(resId)
    }

    fun setDefalultIcon(path: String) {
        setDefaultIconOnly(path)
        setDefalultIcon()
    }

    fun setDefaultIconOnly(path: String) {
        this.mDefaultIcon = path
    }

    override fun getLayoutParams(): android.view.ViewGroup.LayoutParams? {
        return mParams
    }

    override fun resizeFrame(mWidth: Int, mHeight: Int) {
        // mParams.width = mWidth;
        // mParams.height = mHeight;
        // RelativeLayout.LayoutParams pa = (LayoutParams)
        // ivImage.getLayoutParams();
        // pa.width = mWidth;
        // pa.height = mHeight;
    }

    private interface OnGetBitmapListener {
        fun onSucceed()

        fun onFailure()
    }

    interface SingleClickListener {
        fun onSClick()
    }

    interface DoubleClickListener {
        fun onDClick()
    }

    interface LongClickListener {
        fun longClick()
    }

    companion object {
        /**
         * 文字类型
         */
        val ACTION_NAME_TEXT = "文字类型__"
    }

}

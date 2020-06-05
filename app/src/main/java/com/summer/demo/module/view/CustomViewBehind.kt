package com.summer.demo.module.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

import com.summer.demo.R

class CustomViewBehind @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewGroup(context, attrs) {
    private var mTouchMode = SlidingMenu.TOUCHMODE_MARGIN

    private var mViewAbove: CustomViewAbove? = null

    //		if (mContent != null)
    //			removeView(mContent);
    //		addView(mContent);
    var content: View? = null
    /**
     * Sets the secondary (right) menu for use when setMode is called with SlidingMenu.LEFT_RIGHT.
     * @param v the right menu
     */
    var secondaryContent: View? = null
        set(v) {
            if (secondaryContent != null)
                removeView(secondaryContent)
            field = v
            addView(secondaryContent)
        }
    var marginThreshold: Int = 0
    private var mWidthOffset: Int = 0
    private var mTransformer: SlidingMenu.CanvasTransformer? = null
    private var mChildrenEnabled: Boolean = false

    val behindWidth: Int
        get() = content!!.width

    var mode: Int = 0
        set(mode) {
            if (mode == SlidingMenu.LEFT || mode == SlidingMenu.RIGHT) {
                if (content != null)
                    content!!.visibility = View.VISIBLE
                if (secondaryContent != null)
                    secondaryContent!!.visibility = View.INVISIBLE
            }
            field = mode
        }
    private var mFadeEnabled: Boolean = false
    private val mFadePaint = Paint()
    var scrollScale: Float = 0.toFloat()
    private var mShadowDrawable: Drawable? = null
    private var mSecondaryShadowDrawable: Drawable? = null
    private var mShadowWidth: Int = 0
    private var mFadeDegree: Float = 0.toFloat()

    private var mSelectorEnabled = true
    private var mSelectorDrawable: Bitmap? = null
    private var mSelectedView: View? = null

    private val selectorTop: Int
        get() {
            var y = mSelectedView!!.top
            y += (mSelectedView!!.height - mSelectorDrawable!!.height) / 2
            return y
        }

    init {
        marginThreshold = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                MARGIN_THRESHOLD.toFloat(), resources.displayMetrics).toInt()
    }

    fun setCustomViewAbove(customViewAbove: CustomViewAbove) {
        mViewAbove = customViewAbove
    }

    fun setCanvasTransformer(t: SlidingMenu.CanvasTransformer) {
        mTransformer = t
    }

    fun setWidthOffset(i: Int) {
        mWidthOffset = i
        requestLayout()
    }

    fun setChildrenEnabled(enabled: Boolean) {
        mChildrenEnabled = enabled
    }

    override fun scrollTo(x: Int, y: Int) {
        super.scrollTo(x, y)
        if (mTransformer != null)
            invalidate()
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        return !mChildrenEnabled
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return !mChildrenEnabled
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (mTransformer != null) {
            canvas.save()
            mTransformer!!.transformCanvas(canvas, mViewAbove!!.percentOpen)
            super.dispatchDraw(canvas)
            canvas.restore()
        } else
            super.dispatchDraw(canvas)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = r - l
        val height = b - t
        content!!.layout(0, 0, width - mWidthOffset, height)
        if (secondaryContent != null)
            secondaryContent!!.layout(0, 0, width - mWidthOffset, height)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.getDefaultSize(0, widthMeasureSpec)
        val height = View.getDefaultSize(0, heightMeasureSpec)
        setMeasuredDimension(width, height)
        val contentWidth = ViewGroup.getChildMeasureSpec(widthMeasureSpec, 0, width - mWidthOffset)
        val contentHeight = ViewGroup.getChildMeasureSpec(heightMeasureSpec, 0, height)
        content!!.measure(contentWidth, contentHeight)
        if (secondaryContent != null)
            secondaryContent!!.measure(contentWidth, contentHeight)
    }

    fun setShadowDrawable(shadow: Drawable) {
        mShadowDrawable = shadow
        invalidate()
    }

    fun setSecondaryShadowDrawable(shadow: Drawable) {
        mSecondaryShadowDrawable = shadow
        invalidate()
    }

    fun setShadowWidth(width: Int) {
        mShadowWidth = width
        invalidate()
    }

    fun setFadeEnabled(b: Boolean) {
        mFadeEnabled = b
    }

    fun setFadeDegree(degree: Float) {
        check(!(degree > 1.0f || degree < 0.0f)) { "The BehindFadeDegree must be between 0.0f and 1.0f" }
        mFadeDegree = degree
    }

    fun getMenuPage(page: Int): Int {
        var page = page
        page = if (page > 1) 2 else if (page < 1) 0 else page
        return if (mode == SlidingMenu.LEFT && page > 1) {
            0
        } else if (mode == SlidingMenu.RIGHT && page < 1) {
            2
        } else {
            page
        }
    }

    fun scrollBehindTo(content: View, x: Int, y: Int) {
        var vis = View.VISIBLE
        if (mode == SlidingMenu.LEFT) {
            if (x >= content.left) vis = View.INVISIBLE
            scrollTo(((x + behindWidth) * scrollScale).toInt(), y)
        } else if (mode == SlidingMenu.RIGHT) {
            if (x <= content.left) vis = View.INVISIBLE
            scrollTo((behindWidth - width + (x - behindWidth) * scrollScale).toInt(), y)
        } else if (mode == SlidingMenu.LEFT_RIGHT) {
            this.content!!.visibility = if (x >= content.left) View.INVISIBLE else View.VISIBLE
            secondaryContent!!.visibility = if (x <= content.left) View.INVISIBLE else View.VISIBLE
            vis = if (x == 0) View.INVISIBLE else View.VISIBLE
            if (x <= content.left) {
                scrollTo(((x + behindWidth) * scrollScale).toInt(), y)
            } else {
                scrollTo((behindWidth - width + (x - behindWidth) * scrollScale).toInt(), y)
            }
        }
        if (vis == View.INVISIBLE)
            Log.v(TAG, "behind INVISIBLE")
        visibility = vis
    }

    fun getMenuLeft(content: View, page: Int): Int {
        if (mode == SlidingMenu.LEFT) {
            when (page) {
                0 -> return content.left - behindWidth
                2 -> return content.left
            }
        } else if (mode == SlidingMenu.RIGHT) {
            when (page) {
                0 -> return content.left
                2 -> return content.left + behindWidth
            }
        } else if (mode == SlidingMenu.LEFT_RIGHT) {
            when (page) {
                0 -> return content.left - behindWidth
                2 -> return content.left + behindWidth
            }
        }
        return content.left
    }

    fun getAbsLeftBound(content: View): Int {
        if (mode == SlidingMenu.LEFT || mode == SlidingMenu.LEFT_RIGHT) {
            return content.left - behindWidth
        } else if (mode == SlidingMenu.RIGHT) {
            return content.left
        }
        return 0
    }

    fun getAbsRightBound(content: View): Int {
        if (mode == SlidingMenu.LEFT) {
            return content.left
        } else if (mode == SlidingMenu.RIGHT || mode == SlidingMenu.LEFT_RIGHT) {
            return content.left + behindWidth
        }
        return 0
    }

    fun marginTouchAllowed(content: View, x: Int): Boolean {
        val left = content.left
        val right = content.right
        if (mode == SlidingMenu.LEFT) {
            return x >= left && x <= marginThreshold + left
        } else if (mode == SlidingMenu.RIGHT) {
            return x <= right && x >= right - marginThreshold
        } else if (mode == SlidingMenu.LEFT_RIGHT) {
            return x >= left && x <= marginThreshold + left || x <= right && x >= right - marginThreshold
        }
        return false
    }

    fun setTouchMode(i: Int) {
        mTouchMode = i
    }

    fun menuOpenTouchAllowed(content: View, currPage: Int, x: Float): Boolean {
        when (mTouchMode) {
            SlidingMenu.TOUCHMODE_FULLSCREEN -> return true
            SlidingMenu.TOUCHMODE_MARGIN -> return menuTouchInQuickReturn(content, currPage, x)
        }
        return false
    }

    fun menuTouchInQuickReturn(content: View, currPage: Int, x: Float): Boolean {
        if (mode == SlidingMenu.LEFT || mode == SlidingMenu.LEFT_RIGHT && currPage == 0) {
            return x >= content.left
        } else if (mode == SlidingMenu.RIGHT || mode == SlidingMenu.LEFT_RIGHT && currPage == 2) {
            return x <= content.right
        }
        return false
    }

    fun menuClosedSlideAllowed(dx: Float): Boolean {
        if (mode == SlidingMenu.LEFT) {
            return dx > 0
        } else if (mode == SlidingMenu.RIGHT) {
            return dx < 0
        } else if (mode == SlidingMenu.LEFT_RIGHT) {
            return true
        }
        return false
    }

    fun menuOpenSlideAllowed(dx: Float): Boolean {
        if (mode == SlidingMenu.LEFT) {
            return dx < 0
        } else if (mode == SlidingMenu.RIGHT) {
            return dx > 0
        } else if (mode == SlidingMenu.LEFT_RIGHT) {
            return true
        }
        return false
    }

    fun drawShadow(content: View, canvas: Canvas) {
        if (mShadowDrawable == null || mShadowWidth <= 0) return
        var left = 0
        if (mode == SlidingMenu.LEFT) {
            left = content.left - mShadowWidth
        } else if (mode == SlidingMenu.RIGHT) {
            left = content.right
        } else if (mode == SlidingMenu.LEFT_RIGHT) {
            if (mSecondaryShadowDrawable != null) {
                left = content.right
                mSecondaryShadowDrawable!!.setBounds(left, 0, left + mShadowWidth, height)
                mSecondaryShadowDrawable!!.draw(canvas)
            }
            left = content.left - mShadowWidth
        }
        mShadowDrawable!!.setBounds(left, 0, left + mShadowWidth, height)
        mShadowDrawable!!.draw(canvas)
    }

    fun drawFade(content: View, canvas: Canvas, openPercent: Float) {
        if (!mFadeEnabled) return
        val alpha = (mFadeDegree * 255f * Math.abs(1 - openPercent)).toInt()
        mFadePaint.color = Color.argb(alpha, 0, 0, 0)
        var left = 0
        var right = 0
        if (mode == SlidingMenu.LEFT) {
            left = content.left - behindWidth
            right = content.left
        } else if (mode == SlidingMenu.RIGHT) {
            left = content.right
            right = content.right + behindWidth
        } else if (mode == SlidingMenu.LEFT_RIGHT) {
            left = content.left - behindWidth
            right = content.left
            canvas.drawRect(left.toFloat(), 0f, right.toFloat(), height.toFloat(), mFadePaint)
            left = content.right
            right = content.right + behindWidth
        }
        canvas.drawRect(left.toFloat(), 0f, right.toFloat(), height.toFloat(), mFadePaint)
    }

    fun drawSelector(content: View, canvas: Canvas, openPercent: Float) {
        if (!mSelectorEnabled) return
        if (mSelectorDrawable != null && mSelectedView != null) {
            val tag = mSelectedView!!.getTag(R.id.selected_view) as String
            if (tag == TAG + "SelectedView") {
                canvas.save()
                val left: Int
                val right: Int
                val offset: Int
                offset = (mSelectorDrawable!!.width * openPercent).toInt()
                if (mode == SlidingMenu.LEFT) {
                    right = content.left
                    left = right - offset
                    canvas.clipRect(left, 0, right, height)
                    canvas.drawBitmap(mSelectorDrawable!!, left.toFloat(), selectorTop.toFloat(), null)
                } else if (mode == SlidingMenu.RIGHT) {
                    left = content.right
                    right = left + offset
                    canvas.clipRect(left, 0, right, height)
                    canvas.drawBitmap(mSelectorDrawable!!, (right - mSelectorDrawable!!.width).toFloat(), selectorTop.toFloat(), null)
                }
                canvas.restore()
            }
        }
    }

    fun setSelectorEnabled(b: Boolean) {
        mSelectorEnabled = b
    }

    fun setSelectedView(v: View?) {
        if (mSelectedView != null) {
            mSelectedView!!.setTag(R.id.selected_view, null)
            mSelectedView = null
        }
        if (v != null && v.parent != null) {
            mSelectedView = v
            mSelectedView!!.setTag(R.id.selected_view, TAG + "SelectedView")
            invalidate()
        }
    }

    fun setSelectorBitmap(b: Bitmap) {
        mSelectorDrawable = b
        refreshDrawableState()
    }

    companion object {

        private val TAG = "CustomViewBehind"

        private val MARGIN_THRESHOLD = 48 // dips
    }

}

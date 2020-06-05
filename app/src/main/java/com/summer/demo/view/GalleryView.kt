package com.summer.demo.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

class GalleryView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null) : RecyclerView(mContext, attrs) {
    private var mScrollPositionChangeListener: OnScrollPositionChangeListener? = null
    private var mScrollStopListener: OnScrollStopListener? = null
    private var mCurrentOffset: Int = 0
    private var mOnePageWidth: Int = 0 // 滑动一页的距离
    private var mCurrentItemPos: Int = 0
    private var mLastHeighlightPosition = -1
    private var mHighlightScale = 1.28f
    private var mReferenceX: Int = 0

    /**
     * 获取当前突出变大的选项位置
     */
    val highlightPosition: Int
        get() = Math.max(0, mCurrentItemPos - OFFEST_SPAN_COUNT)

    init {
        init()
    }

    private fun init() {
        layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        LinearSnapHelper().attachToRecyclerView(this)
    }

    private fun handleScroll() {
        if (childCount < 3)
            return
        if (mOnePageWidth <= 0) {
            val view = getChildAt(1)
            val p = getChildAdapterPosition(view)
            val params = view.layoutParams as RecyclerView.LayoutParams
            mOnePageWidth = view.width + params.leftMargin + params.rightMargin
            mReferenceX = (width - mOnePageWidth) / 2
        }
        if (mOnePageWidth <= 0)
            return
        var pageChanged = false
        // 滑动超过一页说明已翻页
        if (Math.abs(mCurrentOffset - highlightPosition * mOnePageWidth) >= mOnePageWidth) {
            pageChanged = true
        }
        mCurrentItemPos = Math.max(mCurrentItemPos, OFFEST_SPAN_COUNT)
        if (pageChanged) {
            mCurrentItemPos = mCurrentOffset / mOnePageWidth + OFFEST_SPAN_COUNT
            if (mScrollPositionChangeListener != null)
                mScrollPositionChangeListener!!.onScrollChange(highlightPosition)
        }

        val childCount = childCount
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            val scale = Math.max(1f, mHighlightScale - Math.abs(view.x - mReferenceX) / mOnePageWidth * (mHighlightScale - 1))
            view.scaleX = scale
            view.scaleY = scale
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        handleScroll()
    }

    override fun scrollToPosition(position: Int) {
        scrollBy((position - highlightPosition) * mOnePageWidth, 0)
    }

    override fun smoothScrollToPosition(position: Int) {
        //        super.smoothScrollToPosition(position + OFFEST_SPAN_COUNT);
        smoothScrollBy((position - highlightPosition) * mOnePageWidth, 0)
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        mCurrentOffset += dx
        handleScroll()
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            if (mScrollStopListener != null) {
                val cur = highlightPosition
                if (cur != mLastHeighlightPosition) {
                    mLastHeighlightPosition = cur
                    mScrollStopListener!!.onScrollStop(mLastHeighlightPosition)
                }
            }
        }
    }

    fun setmHighlightScale(scale: Float) {
        mHighlightScale = scale
        requestLayout()
    }

    fun setOnScrollStopListener(listener: OnScrollStopListener) {
        mScrollStopListener = listener
    }

    fun setOnScrollPositionChangeListener(listener: OnScrollPositionChangeListener) {
        mScrollPositionChangeListener = listener
    }

    interface OnScrollStopListener {
        fun onScrollStop(position: Int)
    }

    interface OnScrollPositionChangeListener {
        fun onScrollChange(position: Int)
    }

    companion object {

        private val OFFEST_SPAN_COUNT = 1
    }
}

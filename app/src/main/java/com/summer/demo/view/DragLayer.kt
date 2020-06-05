package com.summer.demo.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ImageView.ScaleType

import com.summer.helper.utils.Logs
import com.summer.helper.utils.SUtils

/**
 * 画布，所有的人物都在这个画布中
 *
 * @author xiaqiliang
 */
class DragLayer : FrameLayout, ViewGroup.OnHierarchyChangeListener {
    /* 当前处理的拖动项 */
    internal var mDragItem: DragView? = null
    /**
     * 当前DragItem的LayoutParams
     */
    internal var mDragItemParams: FrameLayout.LayoutParams? = null
    internal var downPoint: Point? = null
    /**
     * 走动前的位置
     */
    internal var mMovePre = Point()
    /**
     * 走动后的位置
     */
    internal var mMoveAfter = Point()
    /**
     * 背景图片
     */
    internal var mBackgroundView: ImageView? = null
    /**
     * 装饰框
     */
    internal var mFrameView: ImageView? = null

    /**
     * 是否在于宠物编辑动作模式
     */
    internal var mViewOnEditMode = false
    /**
     * 是否正按在缩放键上,0为左，1为上，2为右，3为下
     */
    internal var mViewOnScaleMode = -1
    /**
     * 是否处在走动模式
     */
    internal var onMoveMode = false
    /* 按下时间，判断长按 */
    internal var mDownTime: Long = 0
    /**
     * 播放或预览模式
     */
    internal var mPreviewMode = false
    /**
     * 有人物正在走动中
     */
    internal var mMoving: Boolean = false
    /* 判断双击 */
    internal var mClickIndex = 0
    /* 全屏模式 */
    internal var mFullScreenMode = false
    /* 左边还是右边 */
    internal var leftOrRight = 0
    /* 正在拖动中 */
    internal var mOnDrag = false
    /* 背景图片 */
    internal var mBackgroundImg: String? = null
    /* 当前触摸按下位置X */
    internal var mMotionDownX = 0
    /* 当前触摸按下位置Y */
    internal var mMotionDownY = 0
    internal var frameNotifyed = false
    /**
     * 编辑状态下与播放状态下的比例
     */
    internal var mPlayScale = 0f
    internal var mPlayScaleY = 0f

    internal var mDragItemOffsetX: Float = 0.toFloat()
    internal var mDragItemOffsetY: Float = 0.toFloat()

    private val isRelativeLayout: DragView?
        get() = if (null != mDragItem && mDragItem is DragView) {
            mDragItem
        } else null

    @SuppressLint("NewApi")
    constructor(context: Context) : super(context) {
        isMotionEventSplittingEnabled = false
        isChildrenDrawingOrderEnabled = true
        setOnHierarchyChangeListener(this)
        addBackgroundView()
        mPlayScale = 1240 / 1031f
        mPlayScaleY = 697.5f / 580f
    }

    @SuppressLint("NewApi")
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        isMotionEventSplittingEnabled = false
        isChildrenDrawingOrderEnabled = true
        setOnHierarchyChangeListener(this)
        addBackgroundView()
        mPlayScale = 1240 / 1031f
        mPlayScaleY = 697.5f / 580f
    }

    /**
     * 添加背景
     */
    fun addBackgroundView() {
        mBackgroundView = ImageView(context)
        addView(mBackgroundView)
        mBackgroundView!!.isClickable = false
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        mBackgroundView!!.layoutParams = params
        mBackgroundView!!.scaleType = ScaleType.CENTER_CROP
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (mOnDrag) {
            false
        } else super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        performClick()
        Logs.i("xia", "onTouch:" + ismMoving())
        /* 移动时不让点击 */
        if (ismMoving()) {
            return true
        }
        val x = event.x.toInt()
        val y = event.y.toInt()

        // if(null != mDetector){
        // mDetector.onTouchEvent(event);
        // }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downPoint = Point(x, y)
                mOnDrag = false
                mDragItem = checkOnChild(x, y)
                /* 检查是否按在物品四个角落点 */
                // if(checkOnScaleButton(x,y)){
                // return true;
                // }
                mDownTime = System.currentTimeMillis()

                mClickIndex++
                if (null != mDragItem) {
                    mDragItemOffsetY = (y - mDragItem!!.viewTop).toFloat()
                    mDragItemOffsetX = (x - mDragItem!!.viewLeft).toFloat()
                    Logs.i("xia", "左偏移:$mDragItemOffsetX,,,上偏移:$mDragItemOffsetY")
                    mMotionDownX = x
                    mMotionDownY = y
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (0 == mMotionDownX && 0 == mMotionDownY) {
                    mMotionDownX = x
                    mMotionDownY = y
                }
                Logs.i("xia", "未处理的Y:$y")
                /* 走动模式,绘制拖动的线 */
                if (onMoveMode && null != mDragItem) {
                }
                handleMove(x, y)
            }
            MotionEvent.ACTION_UP -> {
                if (mDragItem == null) {
                    setmDragItem(null)
                } else {
                }
                mOnDrag = true
                val curTime = System.currentTimeMillis()
                Handler().postDelayed({ mClickIndex = 0 }, 200)
                if (curTime - mDownTime > 500) {
                    hanleActionUP(x, y)
                    Logs.i("come to here:" + mDragItem!!)
                    if (mDragItem != null) {
                        if (mDragItem!!.onLongClickListener != null) {
                            mDragItem!!.onLongClickListener.longClick()
                        }
                    }
                } else {
                    if (Math.abs(x - mMotionDownX) < 5 && Math.abs(y - mMotionDownY) < 5) {

                        setOnEditMode(true)
                        Logs.i("come to here:" + mClickIndex + ",,," + mDragItem!!.onDoubleClickListener)
                        if (mDragItem != null) {
                            if (mClickIndex > 1) {
                                mClickIndex = 0
                                if (mDragItem!!.onDoubleClickListener != null) {
                                    mDragItem!!.onDoubleClickListener.onDClick()
                                }
                            } else {
                                if (mDragItem!!.onSingleClickListener != null) {
                                    mDragItem!!.onSingleClickListener.onSClick()
                                }
                            }

                        }
                    } else {
                        hanleActionUP(x, y)
                    }
                }

                mDownTime = 0
                Logs.i("DragLayer -- > ACTION_CANCEL")
                mOnDrag = true
                if (onMoveMode) {
                    return true
                }
                mDownTime = 0
            }
            MotionEvent.ACTION_CANCEL -> {
                Logs.i("DragLayer -- > ACTION_CANCEL")
                mOnDrag = true
                if (onMoveMode) {
                    return true
                }
                mDownTime = 0
            }
        }
        return if (mDragItem != null) {
            true
        } else false
    }

    /**
     * 抬手后设置宠物所在位置，并设置疆界束缚
     *
     * @param x
     * @param y
     */
    @SuppressLint("NewApi")
    private fun hanleActionUP(x: Int, y: Int) {
        if (null == mDragItem)
            return
        mDragItem!!.translationX = 0f
        mDragItem!!.translationY = 0f
        val left = x - mMotionDownX + mDragItem!!.viewLeft
        val top = y - mMotionDownY + mDragItem!!.viewTop

        if (mDragItem is DragView) {
            val layout = mDragItem

            layout!!.setLayoutPosition(left, top)
            layout.cancelLongPress()
        } else {
            mDragItem!!.translationX = 0f
            mDragItem!!.translationY = 0f
            mDragItem!!.setLayoutPosition(left, top)
        }
        mDragItem!!.requestLayout()
        mDragItem!!.invalidate()
        deleteDragItem()
    }


    /**
     * 移动时改变拖动项的位置
     *
     * @param x
     * @param y
     */
    @SuppressLint("NewApi")
    fun handleMove(x: Int, y: Int) {
        if (null == mDragItem) {
            mMotionDownX = 0
            mMotionDownY = 0
            return
        }
        if (mMotionDownX == 0 && mMotionDownY == 0) {
            return
        }
        val tx = x - mMotionDownX
        val ty = y - mMotionDownY
        mDragItem!!.translationX = tx.toFloat()
        mDragItem!!.translationY = ty.toFloat()
    }


    /**
     * 检查触摸点是否在物品对象上
     *
     * @param x
     * @param y
     * @return
     */
    fun checkOnChild(x: Int, y: Int): DragView? {
        val count = this.childCount
        for (i in 0 until count) {
            val child = this.getChildAt(count - i - 1)
            if (null != child) {
                var outRect: Rect? = Rect()
                if (child is BaseDragView) {
                    outRect = child.coor
                    var layout: DragView? = null
                    if (child is DragView) {
                        layout = child
                    }
                    if (outRect != null && outRect.contains(x, y)) {
                        if (layout != null) {
                            mPreviewMode = false
                            setmDragItem(layout)
                            requestLayout()
                            invalidate()
                            return layout
                        } else {
                        }
                        return null
                    }
                }
            }
        }
        return null
    }


    fun deleteDragItem() {
        mDragItem = null
        mMotionDownX = 0
        mMotionDownY = 0
    }

    override fun onChildViewAdded(parent: View, child: View) {}

    override fun onChildViewRemoved(parent: View, child: View) {}

    fun setmDragItem(item: DragView?) {
        mDragItem = null
        if (null == item) {
            mDragItemParams = null
            return
        }
        this.mDragItem = item
        // mDetector = new ScaleGestureDetector(getContext(), listener);
        mDragItemParams = mDragItem!!.layoutParams as FrameLayout.LayoutParams
    }

    /**
     * 设置为编辑模式
     *
     * @param b
     */
    fun setOnEditMode(b: Boolean) {
        this.mViewOnEditMode = b
    }

    override fun setBackgroundResource(resid: Int) {
        if (mBackgroundView != null) {
            mBackgroundView!!.setBackgroundResource(resid)
        }
    }

    /**
     * 设置网络图片
     * @param img
     */
    fun setmBackgroundImg(img: String) {

        if (mBackgroundView != null) {
            SUtils.setPic(mBackgroundView, img)
        }
    }

    fun ismPreviewMode(): Boolean {
        return mPreviewMode
    }

    fun setmPreviewMode(mPreviewMode: Boolean) {
        this.mPreviewMode = mPreviewMode
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    fun clearAll() {
        mMoving = false
        onMoveMode = false
        mMotionDownX = 0
        mMotionDownY = 0
        mPreviewMode = false
        mDragItem = null
    }

    fun ismMoving(): Boolean {
        return mMoving
    }

    fun setmMoving(mMoving: Boolean) {
        this.mMoving = mMoving
    }

}

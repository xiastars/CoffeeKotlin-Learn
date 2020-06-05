package com.summer.demo.module.view

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.support.v4.view.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.Interpolator
import android.widget.Scroller
import com.summer.demo.R
import com.summer.demo.ui.main.MainFragment
import com.summer.helper.view.CustomerViewPager
import java.util.*

class CustomViewAbove @JvmOverloads constructor( context: Context, attrs: AttributeSet? = null) : ViewGroup(context, attrs) {

    //		addView(mContent);
    var content: View? = null

    private var mCurItem: Int = 0
    private var mScroller: Scroller? = null

    private var mScrollingCacheEnabled: Boolean = false

    private var mScrolling: Boolean = false

    private var mIsBeingDragged: Boolean = false
    private var mIsUnableToDrag: Boolean = false
    private var mTouchSlop: Int = 0
    private var mInitialMotionX: Float = 0.toFloat()
    /**
     * Position of the last motion event.
     */
    private var mLastMotionX: Float = 0.toFloat()
    private var mLastMotionY: Float = 0.toFloat()
    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    protected var mActivePointerId = INVALID_POINTER

    /**
     * Determines speed during touch scrolling
     */
    protected var mVelocityTracker: VelocityTracker? = null
    private var mMinimumVelocity: Int = 0
    protected var mMaximumVelocity: Int = 0
    private var mFlingDistance: Int = 0

    private var mViewBehind: CustomViewBehind? = null
    //	private int mMode;
    var isSlidingEnabled = true

    private var mOnPageChangeListener: OnPageChangeListener? = null
    private var mInternalPageChangeListener: OnPageChangeListener? = null

    //	private OnCloseListener mCloseListener;
    //	private OnOpenListener mOpenListener;
    private var mClosedListener: SlidingMenu.OnClosedListener? = null
    private var mOpenedListener: SlidingMenu.OnOpenedListener? = null
    private var mMenuDragListener: SlidingMenu.OnMenuDragListener? = null

    private val mIgnoredViews = ArrayList<View>()

    /**
     * Set the currently selected page. If the CustomViewPager has already been through its first
     * layout there will be a smooth animated transition between the current item and the
     * specified item.
     *
     * @param item Item index to select
     */
    var currentItem: Int
        get() = mCurItem
        set(item) = setCurrentItemInternal(item, true, false)

    private val leftBound: Int
        get() = mViewBehind!!.getAbsLeftBound(content!!)

    private val rightBound: Int
        get() = mViewBehind!!.getAbsRightBound(content!!)

    val contentLeft: Int
        get() = content!!.left + content!!.paddingLeft

    val isMenuOpen: Boolean
        get() = mCurItem == 0 || mCurItem == 2

    val behindWidth: Int
        get() = if (mViewBehind == null) {
            0
        } else {
            mViewBehind!!.behindWidth
        }

    var touchMode = SlidingMenu.TOUCHMODE_MARGIN

    private var mQuickReturn = false

    val percentOpen: Float
        get() = Math.abs(mScrollX - content!!.left) / behindWidth

    // variables for drawing
    private var mScrollX = 0.0f


    /**
     * Callback interface for responding to changing state of the selected page.
     */
    interface OnPageChangeListener {


        fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)


        fun onPageSelected(position: Int)

    }

    /**
     * Simple implementation of the [OnPageChangeListener] interface with stub
     * implementations of each method. Extend this if you do not intend to override
     * every method of [OnPageChangeListener].
     */
    open class SimpleOnPageChangeListener : OnPageChangeListener {

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            // This space for rent
        }

        override fun onPageSelected(position: Int) {
            // This space for rent
        }

        fun onPageScrollStateChanged(state: Int) {
            // This space for rent
        }

    }

    init {
        initCustomViewAbove()
    }

    internal fun initCustomViewAbove() {
        setWillNotDraw(false)
        descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
        isFocusable = true
        val context = getContext()
        mScroller = Scroller(context, sInterpolator)
        val configuration = ViewConfiguration.get(context)
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration)
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity
        mMaximumVelocity = configuration.scaledMaximumFlingVelocity
        setInternalPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                if (mViewBehind != null) {
                    when (position) {
                        0, 2 -> mViewBehind!!.setChildrenEnabled(true)
                        1 -> mViewBehind!!.setChildrenEnabled(false)
                    }
                }
            }
        })

        val density = context.resources.displayMetrics.density
        mFlingDistance = (MIN_DISTANCE_FOR_FLING * density).toInt()
    }

    /**
     * Set the currently selected page.
     *
     * @param item         Item index to select
     * @param smoothScroll True to smoothly scroll to the new item, false to transition immediately
     */
    fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        setCurrentItemInternal(item, smoothScroll, false)
    }

    @JvmOverloads
    internal fun setCurrentItemInternal(item: Int, smoothScroll: Boolean, always: Boolean, velocity: Int = 0) {
        var item = item
        if (!always && mCurItem == item) {
            setScrollingCacheEnabled(false)
            return
        }

        item = mViewBehind!!.getMenuPage(item)

        val dispatchSelected = mCurItem != item
        mCurItem = item
        var destX = getDestScrollX(mCurItem)
        if (dispatchSelected && mOnPageChangeListener != null) {
            mOnPageChangeListener!!.onPageSelected(item)
        }
        if (dispatchSelected && mInternalPageChangeListener != null) {
            mInternalPageChangeListener!!.onPageSelected(item)
        }
        val metric = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(metric)
        val screenWidth = metric.widthPixels
        if (destX < 0)
            destX = -(screenWidth - context!!.resources.getDimensionPixelSize(R.dimen.slidingmenu_offset))
        else if (destX > 0) destX = screenWidth
        if (mMenuDragListener != null) mMenuDragListener!!.onDrag(destX, scrollY)
        if (smoothScroll) {
            smoothScrollTo(destX, 0, velocity)
        } else {
            completeScroll()
            scrollTo(destX, 0)
        }
    }

    /**
     * Set a listener that will be invoked whenever the page changes or is incrementally
     * scrolled. See [OnPageChangeListener].
     *
     * @param listener Listener to set
     */
    fun setOnPageChangeListener(listener: OnPageChangeListener) {
        mOnPageChangeListener = listener
    }

    fun setOnOpenedListener(l: SlidingMenu.OnOpenedListener) {
        mOpenedListener = l
    }

    fun setOnClosedListener(l: SlidingMenu.OnClosedListener) {
        mClosedListener = l
    }

    fun setOnMenuDragListener(l: SlidingMenu.OnMenuDragListener) {
        mMenuDragListener = l
    }

    /**
     * Set a separate OnPageChangeListener for internal use by the support library.
     *
     * @param listener Listener to set
     * @return The old listener that was set, if any.
     */
    internal fun setInternalPageChangeListener(listener: OnPageChangeListener): OnPageChangeListener? {
        val oldListener = mInternalPageChangeListener
        mInternalPageChangeListener = listener
        return oldListener
    }

    fun addIgnoredView(v: View) {
        if (!mIgnoredViews.contains(v)) {
            mIgnoredViews.add(v)
        }
    }

    fun removeIgnoredView(v: View) {
        mIgnoredViews.remove(v)
    }

    fun clearIgnoredViews() {
        mIgnoredViews.clear()
    }

    // We want the duration of the page snap animation to be influenced by the distance that
    // the screen has to travel, however, we don't want this duration to be effected in a
    // purely linear fashion. Instead, we use this method to moderate the effect that the distance
    // of travel has on the overall snap duration.
    internal fun distanceInfluenceForSnapDuration(f: Float): Float {
        var f = f
        f -= 0.5f // center the values about 0.
        f *= (0.3f * Math.PI / 2.0f).toFloat()
        return Math.sin(f.toDouble()).toFloat()
    }

    fun getDestScrollX(page: Int): Int {
        when (page) {
            0, 2 -> return mViewBehind!!.getMenuLeft(content!!, page)
            1 -> if (content != null) {
                return content!!.left
            }
        }
        return 0
    }

    private fun isInIgnoredView(ev: MotionEvent): Boolean {
        val rect = Rect()
        for (v in mIgnoredViews) {
            v.getHitRect(rect)
            if (rect.contains(ev.x.toInt(), ev.y.toInt())) return true
        }
        return false
    }

    fun getChildWidth(i: Int): Int {
        when (i) {
            0 -> return behindWidth
            1 -> return content!!.width
            else -> return 0
        }
    }

    /**
     * Like [View.scrollBy], but scroll smoothly instead of immediately.
     *
     * @param x        the number of pixels to scroll by on the X axis
     * @param y        the number of pixels to scroll by on the Y axis
     * @param velocity the velocity associated with a fling, if applicable. (0 otherwise)
     */
    @JvmOverloads
    internal fun smoothScrollTo(x: Int, y: Int, velocity: Int = 0) {
        var velocity = velocity
        if (childCount == 0) {
            // Nothing to do.
            setScrollingCacheEnabled(false)
            return
        }
        val sx = scrollX
        val sy = scrollY
        val dx = x - sx
        val dy = y - sy
        if (dx == 0 && dy == 0) {
            completeScroll()
            if (isMenuOpen) {
                if (mOpenedListener != null)
                    mOpenedListener!!.onOpened(if (dx < 0) true else false)
            } else {
                if (mClosedListener != null)
                    mClosedListener!!.onClosed()
            }
            return
        }
        setScrollingCacheEnabled(true)
        mScrolling = true

        val width = behindWidth
        val halfWidth = width / 2
        val distanceRatio = Math.min(1f, 1.0f * Math.abs(dx) / width)
        val distance = halfWidth + halfWidth * distanceInfluenceForSnapDuration(distanceRatio)

        var duration = 0
        velocity = Math.abs(velocity)
        if (velocity > 0) {
            duration = 4 * Math.round(1000 * Math.abs(distance / velocity))
        } else {
            val pageDelta = Math.abs(dx).toFloat() / width
            duration = ((pageDelta + 1) * 100).toInt()
            duration = MAX_SETTLE_DURATION
        }
        duration = Math.min(duration, MAX_SETTLE_DURATION)
        mScroller!!.startScroll(sx, sy, dx, dy, duration)
        invalidate()
    }

    fun setCustomViewBehind(cvb: CustomViewBehind) {
        mViewBehind = cvb
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val width = View.getDefaultSize(0, widthMeasureSpec)
        val height = View.getDefaultSize(0, heightMeasureSpec)
        setMeasuredDimension(width, height)

        val contentWidth = ViewGroup.getChildMeasureSpec(widthMeasureSpec, 0, width)
        val contentHeight = ViewGroup.getChildMeasureSpec(heightMeasureSpec, 0, height)
        content!!.measure(contentWidth, contentHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Make sure scroll position is set correctly.
        if (w != oldw) {
            // [ChrisJ] - This fixes the onConfiguration change for orientation issue..
            // maybe worth having a look why the recomputeScroll pos is screwing
            // up?
            completeScroll()
            scrollTo(getDestScrollX(mCurItem), scrollY)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = r - l
        val height = b - t
        content!!.layout(0, 0, width, height)
    }

    fun setAboveOffset(i: Int) {
        //		RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams)mContent.getLayoutParams());
        //		params.setMargins(i, params.topMargin, params.rightMargin, params.bottomMargin);
        content!!.setPadding(i, content!!.paddingTop,
                content!!.paddingRight, content!!.paddingBottom)
    }


    override fun computeScroll() {
        if (!mScroller!!.isFinished) {
            if (mScroller!!.computeScrollOffset()) {
                val oldX = scrollX
                val oldY = scrollY
                val x = mScroller!!.currX
                val y = mScroller!!.currY

                if (oldX != x || oldY != y) {
                    scrollTo(x, y)
                    pageScrolled(x)
                }
                // Keep on drawing until the animation has finished.
                invalidate()
                return
            }
        }

        // Done with scroll, clean up state.
        completeScroll()
    }

    private fun pageScrolled(xpos: Int) {
        val widthWithMargin = width
        val position = xpos / widthWithMargin
        val offsetPixels = xpos % widthWithMargin
        val offset = offsetPixels.toFloat() / widthWithMargin

        onPageScrolled(position, offset, offsetPixels)
    }

    protected fun onPageScrolled(position: Int, offset: Float, offsetPixels: Int) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener!!.onPageScrolled(position, offset, offsetPixels)
        }
        if (mInternalPageChangeListener != null) {
            mInternalPageChangeListener!!.onPageScrolled(position, offset, offsetPixels)
        }
    }

    private fun completeScroll() {
        val needPopulate = mScrolling
        if (needPopulate) {
            // Done with scroll, no longer want to cache view drawing.
            setScrollingCacheEnabled(false)
            mScroller!!.abortAnimation()
            val oldX = scrollX
            val oldY = scrollY
            val x = mScroller!!.currX
            val y = mScroller!!.currY
            if (oldX != x || oldY != y) {
                scrollTo(x, y)
            }
            if (isMenuOpen) {
                if (mOpenedListener != null)
                    mOpenedListener!!.onOpened(if (x < 0) true else false)
            } else {
                if (mClosedListener != null)
                    mClosedListener!!.onClosed()
            }
        }
        mScrolling = false
    }

    private fun thisTouchAllowed(ev: MotionEvent): Boolean {
        val x = (ev.x + mScrollX).toInt()
        if (isMenuOpen) {
            return mViewBehind!!.menuOpenTouchAllowed(content!!, mCurItem, x.toFloat())
        } else {
            when (touchMode) {
                SlidingMenu.TOUCHMODE_FULLSCREEN -> return !isInIgnoredView(ev)
                SlidingMenu.TOUCHMODE_NONE -> return false
                SlidingMenu.TOUCHMODE_MARGIN -> return mViewBehind!!.marginTouchAllowed(content!!, x)
            }
        }
        return false
    }

    private fun thisSlideAllowed(dx: Float): Boolean {
        var allowed = false
        if (isMenuOpen) {
            allowed = mViewBehind!!.menuOpenSlideAllowed(dx)
        } else {
            allowed = mViewBehind!!.menuClosedSlideAllowed(dx)
        }
        if (DEBUG)
            Log.v(TAG, "this slide allowed $allowed dx: $dx")
        return allowed
    }

    private fun getPointerIndex(ev: MotionEvent, id: Int): Int {
        val activePointerIndex = MotionEventCompat.findPointerIndex(ev, id)
        if (activePointerIndex == -1)
            mActivePointerId = INVALID_POINTER
        return activePointerIndex
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {

        if (!isSlidingEnabled)
            return false

        val action = ev.action and MotionEventCompat.ACTION_MASK

        if (DEBUG)
            if (action == MotionEvent.ACTION_DOWN)
                Log.v(TAG, "Received ACTION_DOWN")

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP
                || action != MotionEvent.ACTION_DOWN && mIsUnableToDrag) {
            endDrag()
            return false
        }

        when (action) {
            MotionEvent.ACTION_MOVE -> {
                //动态里有Viewpager,临时写法
                val viewPager = checkHasViewPager(this)
                if (viewPager != null) {
                    if (viewPager is CustomerViewPager) {
                        if (viewPager.currentItem != 0 && MainFragment.curFragmentIndex == 2) {
                            return false
                        }
                    }
                    /*   else if (viewPager instanceof CBLoopViewPager) {//首页的轮滑图片
                        return false;
                    }*/
                }
                determineDrag(ev)
            }
            MotionEvent.ACTION_DOWN -> {
                val index = MotionEventCompat.getActionIndex(ev)
                mActivePointerId = MotionEventCompat.getPointerId(ev, index)
                if (mActivePointerId == INVALID_POINTER)
                    return false
                mInitialMotionX = MotionEventCompat.getX(ev, index)
                mLastMotionX = mInitialMotionX
                mLastMotionY = MotionEventCompat.getY(ev, index)
                if (thisTouchAllowed(ev)) {
                    mIsBeingDragged = false
                    mIsUnableToDrag = false
                    if (isMenuOpen && mViewBehind!!.menuTouchInQuickReturn(content!!, mCurItem, ev.x + mScrollX)) {
                        mQuickReturn = true
                    }
                } else {
                    mIsUnableToDrag = true
                }
            }
            MotionEventCompat.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)
        }

        if (!mIsBeingDragged) {
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain()
            }
            mVelocityTracker!!.addMovement(ev)
        }
        return mIsBeingDragged || mQuickReturn
    }

    private fun checkHasViewPager(parent: ViewGroup): ViewPager? {
        val count = parent.childCount
        for (i in 0 until count) {
            val view = parent.getChildAt(i)
            if (view is ViewGroup) {
                if (view is ViewPager) {
                    if (view is CustomerViewPager) {
                        return view
                    }
                    /*    if (view instanceof CBLoopViewPager) {
                        CBLoopViewPager pager = (CBLoopViewPager) view;
                        if (pager.isOnTouch()) {
                            return pager;
                        }
                    }*/
                }
                val has = checkHasViewPager(view)
                if (has != null) {
                    return has
                }
            }
        }
        return null
    }


    override fun onTouchEvent(ev: MotionEvent): Boolean {

        if (!isSlidingEnabled)
            return false

        if (!mIsBeingDragged && !thisTouchAllowed(ev))
            return false

        //		if (!mIsBeingDragged && !mQuickReturn)
        //			return false;

        val action = ev.action

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(ev)

        when (action and MotionEventCompat.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                /*
                 * If being flinged and user touches, stop the fling. isFinished
                 * will be false if being flinged.
                 */
                completeScroll()

                // Remember where the motion event started
                val index = MotionEventCompat.getActionIndex(ev)
                mActivePointerId = MotionEventCompat.getPointerId(ev, index)
                mInitialMotionX = ev.x
                mLastMotionX = mInitialMotionX
            }
            MotionEvent.ACTION_MOVE -> {
                if (!mIsBeingDragged) {
                    determineDrag(ev)
                    if (mIsUnableToDrag)
                        return false
                }
                if (mIsBeingDragged) {
                    // Scroll to follow the motion event
                    val activePointerIndex = getPointerIndex(ev, mActivePointerId)
                    if (mActivePointerId == INVALID_POINTER)
                        return false
                    val x = MotionEventCompat.getX(ev, activePointerIndex)
                    val deltaX = mLastMotionX - x
                    mLastMotionX = x
                    val oldScrollX = scrollX.toFloat()
                    var scrollX = oldScrollX + deltaX
                    val leftBound = leftBound.toFloat()
                    val rightBound = rightBound.toFloat()
                    if (scrollX < leftBound) {
                        scrollX = leftBound
                    } else if (scrollX > rightBound) {
                        scrollX = rightBound
                    }
                    // Don't lose the rounded component
                    mLastMotionX += scrollX - scrollX.toInt()
                    scrollTo(scrollX.toInt(), scrollY)
                    pageScrolled(scrollX.toInt())
                    if (mMenuDragListener != null)
                        mMenuDragListener!!.onDrag(scrollX.toInt(), scrollY)
                }
            }
            MotionEvent.ACTION_UP -> if (mIsBeingDragged) {
                val velocityTracker = mVelocityTracker
                velocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                val initialVelocity = VelocityTrackerCompat.getXVelocity(
                        velocityTracker, mActivePointerId).toInt()
                val scrollX = scrollX
                //				final int widthWithMargin = getWidth();
                //				final float pageOffset = (float) (scrollX % widthWithMargin) / widthWithMargin;
                // TODO test this. should get better flinging behavior
                val pageOffset = (scrollX - getDestScrollX(mCurItem)).toFloat() / behindWidth
                val activePointerIndex = getPointerIndex(ev, mActivePointerId)
                if (mActivePointerId != INVALID_POINTER) {
                    val x = MotionEventCompat.getX(ev, activePointerIndex)
                    val totalDelta = (x - mInitialMotionX).toInt()
                    val nextPage = determineTargetPage(pageOffset, initialVelocity, totalDelta)
                    setCurrentItemInternal(nextPage, true, true, initialVelocity)
                } else {
                    setCurrentItemInternal(mCurItem, true, true, initialVelocity)
                }
                mActivePointerId = INVALID_POINTER
                endDrag()
            } else if (mQuickReturn && mViewBehind!!.menuTouchInQuickReturn(content!!, mCurItem, ev.x + mScrollX)) {
                // close the menu
                currentItem = 1
                endDrag()
            }
            MotionEvent.ACTION_CANCEL -> if (mIsBeingDragged) {
                setCurrentItemInternal(mCurItem, true, true)
                mActivePointerId = INVALID_POINTER
                endDrag()
            }
            MotionEventCompat.ACTION_POINTER_DOWN -> {
                val indexx = MotionEventCompat.getActionIndex(ev)
                mLastMotionX = MotionEventCompat.getX(ev, indexx)
                mActivePointerId = MotionEventCompat.getPointerId(ev, indexx)
            }
            MotionEventCompat.ACTION_POINTER_UP -> {
                onSecondaryPointerUp(ev)
                val pointerIndex = getPointerIndex(ev, mActivePointerId)
                if (mActivePointerId == INVALID_POINTER)
                    return false
                mLastMotionX = MotionEventCompat.getX(ev, pointerIndex)
            }
        }
        return true
    }

    private fun determineDrag(ev: MotionEvent) {
        val activePointerId = mActivePointerId
        val pointerIndex = getPointerIndex(ev, activePointerId)
        if (activePointerId == INVALID_POINTER)
            return
        val x = MotionEventCompat.getX(ev, pointerIndex)
        val dx = x - mLastMotionX
        val xDiff = Math.abs(dx)
        val y = MotionEventCompat.getY(ev, pointerIndex)
        val dy = y - mLastMotionY
        val yDiff = Math.abs(dy)
        if (xDiff > (if (isMenuOpen) mTouchSlop / 2 else mTouchSlop) && xDiff > yDiff && thisSlideAllowed(dx)) {
            startDrag()
            mLastMotionX = x
            mLastMotionY = y
            setScrollingCacheEnabled(true)
            // TODO add back in touch slop check
        } else if (xDiff > mTouchSlop) {
            mIsUnableToDrag = true
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        super.scrollTo(x, y)
        mScrollX = x.toFloat()
        mViewBehind!!.scrollBehindTo(content!!, x, y)
        //((SlidingMenu)getParent()).manageLayers(getPercentOpen());
    }

    private fun determineTargetPage(pageOffset: Float, velocity: Int, deltaX: Int): Int {
        var targetPage = mCurItem
        if (Math.abs(deltaX) > mFlingDistance && Math.abs(velocity) > mMinimumVelocity) {
            if (velocity > 0 && deltaX > 0) {
                targetPage -= 1
            } else if (velocity < 0 && deltaX < 0) {
                targetPage += 1
            }
        } else {
            targetPage = Math.round(mCurItem + pageOffset)
        }
        return targetPage
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        // Draw the margin drawable if needed.
        mViewBehind!!.drawShadow(content!!, canvas)
        mViewBehind!!.drawFade(content!!, canvas, percentOpen)
        mViewBehind!!.drawSelector(content!!, canvas, percentOpen)
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        if (DEBUG) Log.v(TAG, "onSecondaryPointerUp called")
        val pointerIndex = MotionEventCompat.getActionIndex(ev)
        val pointerId = MotionEventCompat.getPointerId(ev, pointerIndex)
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex)
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex)
            if (mVelocityTracker != null) {
                mVelocityTracker!!.clear()
            }
        }
    }

    private fun startDrag() {
        mIsBeingDragged = true
        mQuickReturn = false
    }

    private fun endDrag() {
        mQuickReturn = false
        mIsBeingDragged = false
        mIsUnableToDrag = false
        mActivePointerId = INVALID_POINTER

        if (mVelocityTracker != null) {
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    private fun setScrollingCacheEnabled(enabled: Boolean) {
        if (mScrollingCacheEnabled != enabled) {
            mScrollingCacheEnabled = enabled
            if (USE_CACHE) {
                val size = childCount
                for (i in 0 until size) {
                    val child = getChildAt(i)
                    if (child.visibility != View.GONE) {
                        child.isDrawingCacheEnabled = enabled
                    }
                }
            }
        }
    }

    /**
     * Tests scrollability within child views of v given a delta of dx.
     *
     * @param v      View to test for horizontal scrollability
     * @param checkV Whether the view v passed should itself be checked for scrollability (true),
     * or just its children (false).
     * @param dx     Delta scrolled in pixels
     * @param x      X coordinate of the active touch point
     * @param y      Y coordinate of the active touch point
     * @return true if child views of v can be scrolled by delta of dx.
     */
    protected fun canScroll(v: View, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        if (v is ViewGroup) {
            val scrollX = v.getScrollX()
            val scrollY = v.getScrollY()
            val count = v.childCount
            // Count backwards - let topmost views consume scroll distance first.
            for (i in count - 1 downTo 0) {
                val child = v.getChildAt(i)
                if (x + scrollX >= child.left && x + scrollX < child.right &&
                        y + scrollY >= child.top && y + scrollY < child.bottom &&
                        canScroll(child, true, dx, x + scrollX - child.left,
                                y + scrollY - child.top)) {
                    return true
                }
            }
        }

        return checkV && ViewCompat.canScrollHorizontally(v, -dx)
    }


    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // Let the focused view and/or our descendants get the key first
        return super.dispatchKeyEvent(event) || executeKeyEvent(event)
    }

    /**
     * You can call this function yourself to have the scroll view perform
     * scrolling from a key event, just as if the event had been dispatched to
     * it by the view hierarchy.
     *
     * @param event The key event to execute.
     * @return Return true if the event was handled, else false.
     */
    fun executeKeyEvent(event: KeyEvent): Boolean {
        var handled = false
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> handled = arrowScroll(View.FOCUS_LEFT)
                KeyEvent.KEYCODE_DPAD_RIGHT -> handled = arrowScroll(View.FOCUS_RIGHT)
                KeyEvent.KEYCODE_TAB -> if (Build.VERSION.SDK_INT >= 11) {
                    // The focus finder had a bug handling FOCUS_FORWARD and FOCUS_BACKWARD
                    // before Android 3.0. Ignore the tab key on those devices.
                    /*    if (KeyEventCompat.hasNoModifiers(event)) {
                            handled = arrowScroll(FOCUS_FORWARD);
                        } else if (KeyEventCompat.hasModifiers(event, KeyEvent.META_SHIFT_ON)) {
                            handled = arrowScroll(FOCUS_BACKWARD);
                        }*/
                }
            }
        }
        return handled
    }

    fun arrowScroll(direction: Int): Boolean {
        var currentFocused: View? = findFocus()
        if (currentFocused === this) currentFocused = null

        var handled = false

        val nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused,
                direction)
        if (nextFocused != null && nextFocused !== currentFocused) {
            if (direction == View.FOCUS_LEFT) {
                handled = nextFocused.requestFocus()
            } else if (direction == View.FOCUS_RIGHT) {
                // If there is nothing to the right, or this is causing us to
                // jump to the left, then what we really want to do is page right.
                if (currentFocused != null && nextFocused.left <= currentFocused.left) {
                    handled = pageRight()
                } else {
                    handled = nextFocused.requestFocus()
                }
            }
        } else if (direction == View.FOCUS_LEFT || direction == View.FOCUS_BACKWARD) {
            // Trying to move left and nothing there; try to page.
            handled = pageLeft()
        } else if (direction == View.FOCUS_RIGHT || direction == View.FOCUS_FORWARD) {
            // Trying to move right and nothing there; try to page.
            handled = pageRight()
        }
        if (handled) {
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction))
        }
        return handled
    }

    internal fun pageLeft(): Boolean {
        if (mCurItem > 0) {
            setCurrentItem(mCurItem - 1, true)
            return true
        }
        return false
    }

    internal fun pageRight(): Boolean {
        if (mCurItem < 1) {
            setCurrentItem(mCurItem + 1, true)
            return true
        }
        return false
    }

    companion object {

        private val TAG = "CustomViewAbove"
        private val DEBUG = false

        private val USE_CACHE = false

        private val MAX_SETTLE_DURATION = 600 // ms
        private val MIN_DISTANCE_FOR_FLING = 25 // dips

        private val sInterpolator = Interpolator { t ->
            var t = t
            t -= 1.0f
            t * t * t * t * t + 1.0f
        }
        /**
         * Sentinel value for no current active pointer.
         * Used by [.mActivePointerId].
         */
        private val INVALID_POINTER = -1
    }

}
/**
 * Like [View.scrollBy], but scroll smoothly instead of immediately.
 *
 * @param x the number of pixels to scroll by on the X axis
 * @param y the number of pixels to scroll by on the Y axis
 */

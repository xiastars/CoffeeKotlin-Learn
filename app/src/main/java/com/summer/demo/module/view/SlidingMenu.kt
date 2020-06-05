package com.summer.demo.module.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import com.summer.helper.utils.Logs


class SlidingMenu
/**
 * Instantiates a new SlidingMenu.
 *
 * @param context the associated Context
 * @param attrs the attrs
 * @param defStyle the def style
 */
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)//
/*	TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenu);
		// set the above and behind views if defined in xml
		int viewAbove = ta.getResourceId(R.styleable.SlidingMenu_viewAbove, -1);
		if (viewAbove != -1) {
			setContent(viewAbove);
		} else {
			//setContent(new FrameLayout(context));
		}
		int viewBehind = ta.getResourceId(R.styleable.SlidingMenu_viewBehind, -1);
		if (viewBehind != -1) {
			setMenu(viewBehind);
		} else {
			//setMenu(new FrameLayout(context));
		}
		ta.recycle();
	*/ : RelativeLayout(context, attrs, defStyle) {
    private var mActionbarOverlay = false

    var customerViewAbove: CustomViewAbove? = null
        private set

    var customerViewBehind: CustomViewBehind? = null
        private set

    private var mOpenListener: OnOpenListener? = null

    private var mSecondaryOpenListner: OnOpenListener? = null

    private var mCloseListener: OnCloseListener? = null

    /**
     * Retrieves the current content.
     * @return the current content
     */
    val content: View?
        get() = customerViewAbove!!.content

    /**
     * Retrieves the main menu.
     * @return the main menu
     */
    val menu: View?
        get() = customerViewBehind!!.content

    /**
     * Retrieves the current secondary menu (right).
     * @return the current menu
     */
    val secondaryMenu: View?
        get() = customerViewBehind!!.secondaryContent

    /**
     * Checks if is sliding enabled.
     *
     * @return true, if is sliding enabled
     */
    /**
     * Sets the sliding enabled.
     *
     * @param b true to enable sliding, false to disable it.
     */
    var isSlidingEnabled: Boolean
        get() = customerViewAbove!!.isSlidingEnabled
        set(b) {
            customerViewAbove!!.isSlidingEnabled = b
        }

    /**
     * Returns the current side that the SlidingMenu is on.
     * @return the current mode, either SlidingMenu.LEFT or SlidingMenu.RIGHT
     */
    /**
     * Sets which side the SlidingMenu should appear on.
     * @param mode must be either SlidingMenu.LEFT or SlidingMenu.RIGHT
     */
    var mode: Int
        get() = customerViewBehind!!.mode
        set(mode) {
            check(!(mode != LEFT && mode != RIGHT && mode != LEFT_RIGHT)) { "SlidingMenu mode must be LEFT, RIGHT, or LEFT_RIGHT" }
            customerViewBehind!!.mode = mode
        }

    /**
     * Checks if is the behind view showing.
     *
     * @return Whether or not the behind view is showing
     */
    val isMenuShowing: Boolean
        get() = customerViewAbove!!.currentItem == 0 || customerViewAbove!!.currentItem == 2

    /**
     * Checks if is the behind view showing.
     *
     * @return Whether or not the behind view is showing
     */
    val isSecondaryMenuShowing: Boolean
        get() = customerViewAbove!!.currentItem == 2

    /**
     * Gets the behind offset.
     *
     * @return The margin on the right of the screen that the behind view scrolls to
     */
    /**
     * Sets the behind offset.
     *
     * @param i The margin, in pixels, on the right of the screen that the behind view scrolls to.
     */
    //		RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams)mViewBehind.getLayoutParams());
    //		int bottom = params.bottomMargin;
    //		int top = params.topMargin;
    //		int left = params.leftMargin;
    //		params.setMargins(left, top, i, bottom);
    var behindOffset: Int
        get() = (customerViewBehind!!.layoutParams as RelativeLayout.LayoutParams).rightMargin
        set(i) = customerViewBehind!!.setWidthOffset(i)

    /**
     * Gets the behind scroll scale.
     *
     * @return The scale of the parallax scroll
     */
    /**
     * Sets the behind scroll scale.
     *
     * @param f The scale of the parallax scroll (i.e. 1.0f scrolls 1 pixel for every
     * 1 pixel that the above view scrolls and 0.0f scrolls 0 pixels)
     */
    var behindScrollScale: Float
        get() = customerViewBehind!!.scrollScale
        set(f) {
            check(!(f < 0 && f > 1)) { "ScrollScale must be between 0 and 1" }
            customerViewBehind!!.scrollScale = f
        }

    /**
     * Gets the touch mode margin threshold
     * @return the touch mode margin threshold
     */
    /**
     * Set the touch mode margin threshold
     * @param touchmodeMarginThreshold
     */
    var touchmodeMarginThreshold: Int
        get() = customerViewBehind!!.marginThreshold
        set(touchmodeMarginThreshold) {
            customerViewBehind!!.marginThreshold = touchmodeMarginThreshold
        }

    /**
     * Gets the touch mode above.
     *
     * @return the touch mode above
     */
    /**
     * Controls whether the SlidingMenu can be opened with a swipe gesture.
     * Options are [TOUCHMODE_MARGIN][.TOUCHMODE_MARGIN], [TOUCHMODE_FULLSCREEN][.TOUCHMODE_FULLSCREEN],
     * or [TOUCHMODE_NONE][.TOUCHMODE_NONE]
     *
     * @param i the new touch mode
     */
    var touchModeAbove: Int
        get() = customerViewAbove!!.touchMode
        set(i) {
            check(!(i != TOUCHMODE_FULLSCREEN && i != TOUCHMODE_MARGIN
                    && i != TOUCHMODE_NONE)) { "TouchMode must be set to either" + "TOUCHMODE_FULLSCREEN or TOUCHMODE_MARGIN or TOUCHMODE_NONE." }
            customerViewAbove!!.touchMode = i
        }

    private val mHandler = Handler()


    interface OnOpenListener {

        fun onOpen(isMenuOpen: Boolean)
    }


    interface OnOpenedListener {

        /**
         * On opened.
         */
        fun onOpened(isMenuOpen: Boolean)
    }


    interface OnCloseListener {

        /**
         * On close.
         */
        fun onClose()

    }

    interface OnClosedListener {

        /**
         * On closed.
         */
        fun onClosed()
    }

    /**
     * The Interface CanvasTransformer.
     */
    interface CanvasTransformer {

        /**
         * Transform canvas.
         *
         * @param canvas the canvas
         * @param percentOpen the percent open
         */
        fun transformCanvas(canvas: Canvas, percentOpen: Float)
    }

    /**
     * The Interface OnMenuDragListener.
     */
    interface OnMenuDragListener {

        /**
         * On onDrag.
         */
        fun onDrag(x: Int, y: Int)
    }

    /**
     * Instantiates a new SlidingMenu and attach to Activity.
     *
     * @param activity the activity to attach slidingmenu
     * @param slideStyle the slidingmenu style
     */
    constructor(activity: Activity, slideStyle: Int) : this(activity, null) {
        this.attachToActivity(activity, slideStyle)
    }

    /**
     * Attaches the SlidingMenu to an entire Activity
     *
     * @param activity the Activity
     * @param slideStyle either SLIDING_CONTENT or SLIDING_WINDOW
     * @param actionbarOverlay whether or not the ActionBar is overlaid
     */
    @JvmOverloads
    fun attachToActivity(activity: Activity, slideStyle: Int, actionbarOverlay: Boolean = false) {
        require(!(slideStyle != SLIDING_WINDOW && slideStyle != SLIDING_CONTENT)) { "slideStyle must be either SLIDING_WINDOW or SLIDING_CONTENT" }

        check(parent == null) { "This SlidingMenu appears to already be attached" }

        // get the window background
        val a = activity.theme.obtainStyledAttributes(intArrayOf(android.R.attr.windowBackground))
        val background = a.getResourceId(0, 0)
        a.recycle()

        when (slideStyle) {
            SLIDING_WINDOW -> {
                mActionbarOverlay = false
                val decor = activity.window.decorView as ViewGroup
                val decorChild = decor.getChildAt(0) as ViewGroup
                // save ActionBar themes that have transparent assets
                decorChild.setBackgroundResource(background)
                decor.removeView(decorChild)
                decor.addView(this)
                setContent(decorChild)
            }
            SLIDING_CONTENT -> {
                mActionbarOverlay = actionbarOverlay
                // take the above view out of
                val contentParent = activity.findViewById<View>(android.R.id.content) as ViewGroup
                val content = contentParent.getChildAt(0)
                contentParent.removeView(content)
                contentParent.addView(this)
                setContent(content)
                // save people from having transparent backgrounds
                if (content.background == null)
                    content.setBackgroundResource(background)
            }
        }
    }

    /**
     * Set the above view content from a layout resource. The resource will be inflated, adding all top-level views
     * to the above view.
     *
     * @param res the new content
     */
    fun setContent(res: Int) {
        setContent(LayoutInflater.from(context).inflate(res, null))
    }

    /**
     * Set the above view content to the given View.
     *
     * @param view The desired content to display.
     */
    fun setContent(view: View) {
        customerViewAbove = view as CustomViewAbove
        customerViewAbove!!.content = customerViewAbove!!.getChildAt(0)
        customerViewAbove!!.setCustomViewBehind(customerViewBehind!!)
        customerViewBehind!!.setCustomViewAbove(customerViewAbove!!)
        customerViewAbove!!.setOnPageChangeListener(object : CustomViewAbove.OnPageChangeListener {
            val POSITION_OPEN = 0
            val POSITION_CLOSE = 1
            val POSITION_SECONDARY_OPEN = 2

            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == POSITION_OPEN && mOpenListener != null) {
                    mOpenListener!!.onOpen(true)
                } else if (position == POSITION_CLOSE && mCloseListener != null) {
                    mCloseListener!!.onClose()
                } else if (position == POSITION_SECONDARY_OPEN && mSecondaryOpenListner != null) {
                    mSecondaryOpenListner!!.onOpen(false)
                }
            }
        })
        showContent()
    }

    /**
     * Set the behind view (menu) content from a layout resource. The resource will be inflated, adding all top-level views
     * to the behind view.
     *
     * @param res the new content
     */
    fun setMenu(res: Int) {
        setMenu(LayoutInflater.from(context).inflate(res, null))
    }

    /**
     * Set the behind view (menu) content to the given View.
     *
     * @param v The desired content to display.
     */
    fun setMenu(v: View) {
        customerViewBehind = v as CustomViewBehind
        customerViewBehind!!.content = customerViewBehind!!.getChildAt(0)

    }

    /**
     * Set the secondary behind view (right menu) content from a layout resource. The resource will be inflated, adding all top-level views
     * to the behind view.
     *
     * @param res the new content
     */
    fun setSecondaryMenu(res: Int) {
        setSecondaryMenu(LayoutInflater.from(context).inflate(res, null))
    }

    /**
     * Set the secondary behind view (right menu) content to the given View.
     *
     * @param v The desired content to display.
     */
    fun setSecondaryMenu(v: View) {
        customerViewBehind!!.secondaryContent = v
        //		mViewBehind.invalidate();
    }

    /**
     * Sets whether or not the SlidingMenu is in static mode (i.e. nothing is moving and everything is showing)
     *
     * @param b true to set static mode, false to disable static mode.
     */
    fun setStatic(b: Boolean) {
        if (b) {
            isSlidingEnabled = false
            customerViewAbove!!.setCustomViewBehind(null!!)
            customerViewAbove!!.currentItem = 1
            //			mViewBehind.setCurrentItem(0);
        } else {
            customerViewAbove!!.currentItem = 1
            //			mViewBehind.setCurrentItem(1);
            customerViewAbove!!.setCustomViewBehind(customerViewBehind!!)
            isSlidingEnabled = true
        }
    }

    /**
     * Opens the menu and shows the menu view.
     *
     * @param animate true to animate the transition, false to ignore animation
     */
    @JvmOverloads
    fun showMenu(animate: Boolean = true) {
        Logs.i(".."+customerViewAbove);
        customerViewAbove!!.setCurrentItem(0, animate)
    }

    /**
     * Opens the menu and shows the secondary (right) menu view. Will default to the regular menu
     * if there is only one.
     *
     * @param animate true to animate the transition, false to ignore animation
     */
    @JvmOverloads
    fun showSecondaryMenu(animate: Boolean = true) {
        customerViewAbove!!.setCurrentItem(2, animate)
    }

    /**
     * Closes the menu and shows the above view.
     *
     * @param animate true to animate the transition, false to ignore animation
     */
    @JvmOverloads
    fun showContent(animate: Boolean = true) {
        customerViewAbove!!.setCurrentItem(1, animate)
    }

    /**
     * Toggle the SlidingMenu. If it is open, it will be closed, and vice versa.
     *
     * @param animate true to animate the transition, false to ignore animation
     */
    @JvmOverloads
    fun toggle(animate: Boolean = true) {
        if (isMenuShowing) {
            showContent(animate)
        } else {
            showMenu(animate)
        }
    }

    /**
     * Sets the behind offset.
     *
     * @param resID The dimension resource id to be set as the behind offset.
     * The menu, when open, will leave this width margin on the right of the screen.
     */
    fun setBehindOffsetRes(resID: Int) {
        val i = context.resources.getDimension(resID).toInt()
        behindOffset = i
    }

    /**
     * Sets the above offset.
     *
     * @param i the new above offset, in pixels
     */
    fun setAboveOffset(i: Int) {
        customerViewAbove!!.setAboveOffset(i)
    }

    /**
     * Sets the above offset.
     *
     * @param resID The dimension resource id to be set as the above offset.
     */
    fun setAboveOffsetRes(resID: Int) {
        val i = context.resources.getDimension(resID).toInt()
        setAboveOffset(i)
    }

    /**
     * Sets the behind width.
     *
     * @param i The width the Sliding Menu will open to, in pixels
     */
    fun setBehindWidth(i: Int) {
        var width: Int
        val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay
        try {
            val cls = Display::class.java
            val parameterTypes = arrayOf<Class<*>>(Point::class.java)
            val parameter = Point()
            val method = cls.getMethod("getSize", *parameterTypes)
            method.invoke(display, parameter)
            width = parameter.x
        } catch (e: Exception) {
            width = display.width
        }

        behindOffset = width - i
    }

    /**
     * Sets the behind width.
     *
     * @param res The dimension resource id to be set as the behind width offset.
     * The menu, when open, will open this wide.
     */
    fun setBehindWidthRes(res: Int) {
        val i = context.resources.getDimension(res).toInt()
        setBehindWidth(i)
    }

    /**
     * Sets the behind canvas transformer.
     *
     * @param t the new behind canvas transformer
     */
    fun setBehindCanvasTransformer(t: CanvasTransformer) {
        customerViewBehind!!.setCanvasTransformer(t)
    }

    /**
     * Controls whether the SlidingMenu can be opened with a swipe gesture.
     * Options are [TOUCHMODE_MARGIN][.TOUCHMODE_MARGIN], [TOUCHMODE_FULLSCREEN][.TOUCHMODE_FULLSCREEN],
     * or [TOUCHMODE_NONE][.TOUCHMODE_NONE]
     *
     * @param i the new touch mode
     */
    fun setTouchModeBehind(i: Int) {
        check(!(i != TOUCHMODE_FULLSCREEN && i != TOUCHMODE_MARGIN
                && i != TOUCHMODE_NONE)) { "TouchMode must be set to either" + "TOUCHMODE_FULLSCREEN or TOUCHMODE_MARGIN or TOUCHMODE_NONE." }
        customerViewBehind!!.setTouchMode(i)
    }

    /**
     * Sets the shadow drawable.
     *
     * @param resId the resource ID of the new shadow drawable
     */
    fun setShadowDrawable(resId: Int) {
        setShadowDrawable(context.resources.getDrawable(resId))
    }

    /**
     * Sets the shadow drawable.
     *
     * @param d the new shadow drawable
     */
    fun setShadowDrawable(d: Drawable) {
        customerViewBehind!!.setShadowDrawable(d)
    }

    /**
     * Sets the secondary (right) shadow drawable.
     *
     * @param resId the resource ID of the new shadow drawable
     */
    fun setSecondaryShadowDrawable(resId: Int) {
        setSecondaryShadowDrawable(context.resources.getDrawable(resId))
    }

    /**
     * Sets the secondary (right) shadow drawable.
     *
     * @param d the new shadow drawable
     */
    fun setSecondaryShadowDrawable(d: Drawable) {
        customerViewBehind!!.setSecondaryShadowDrawable(d)
    }

    /**
     * Sets the shadow width.
     *
     * @param resId The dimension resource id to be set as the shadow width.
     */
    fun setShadowWidthRes(resId: Int) {
        setShadowWidth(resources.getDimension(resId).toInt())
    }

    /**
     * Sets the shadow width.
     *
     * @param pixels the new shadow width, in pixels
     */
    fun setShadowWidth(pixels: Int) {
        customerViewBehind!!.setShadowWidth(pixels)
    }

    /**
     * Enables or disables the SlidingMenu's fade in and out
     *
     * @param b true to enable fade, false to disable it
     */
    fun setFadeEnabled(b: Boolean) {
        customerViewBehind!!.setFadeEnabled(b)
    }

    /**
     * Sets how much the SlidingMenu fades in and out. Fade must be enabled, see
     * [setFadeEnabled(boolean)][.setFadeEnabled]
     *
     * @param f the new fade degree, between 0.0f and 1.0f
     */
    fun setFadeDegree(f: Float) {
        customerViewBehind!!.setFadeDegree(f)
    }

    /**
     * Enables or disables whether the selector is drawn
     *
     * @param b true to draw the selector, false to not draw the selector
     */
    fun setSelectorEnabled(b: Boolean) {
        customerViewBehind!!.setSelectorEnabled(true)
    }

    /**
     * Sets the selected view. The selector will be drawn here
     *
     * @param v the new selected view
     */
    fun setSelectedView(v: View) {
        customerViewBehind!!.setSelectedView(v)
    }

    /**
     * Sets the selector drawable.
     *
     * @param res a resource ID for the selector drawable
     */
    fun setSelectorDrawable(res: Int) {
        customerViewBehind!!.setSelectorBitmap(BitmapFactory.decodeResource(resources, res))
    }

    /**
     * Sets the selector drawable.
     *
     * @param b the new selector bitmap
     */
    fun setSelectorBitmap(b: Bitmap) {
        customerViewBehind!!.setSelectorBitmap(b)
    }

    /**
     * Add a View ignored by the Touch Down event when mode is Fullscreen
     *
     * @param v a view to be ignored
     */
    fun addIgnoredView(v: View) {
        customerViewAbove!!.addIgnoredView(v)
    }

    /**
     * Remove a View ignored by the Touch Down event when mode is Fullscreen
     *
     * @param v a view not wanted to be ignored anymore
     */
    fun removeIgnoredView(v: View) {
        customerViewAbove!!.removeIgnoredView(v)
    }

    /**
     * Clear the list of Views ignored by the Touch Down event when mode is Fullscreen
     */
    fun clearIgnoredViews() {
        customerViewAbove!!.clearIgnoredViews()
    }

    fun setOnOpenListener(listener: OnOpenListener) {
        //mViewAbove.setOnOpenListener(listener);
        mOpenListener = listener
    }

    fun setSecondaryOnOpenListner(listener: OnOpenListener) {
        mSecondaryOpenListner = listener
    }

    /**
     * Sets the OnCloseListener. [OnCloseListener.onClose()][OnCloseListener.onClose] will be called when any one of the SlidingMenu is closed
     *
     * @param listener the new setOnCloseListener
     */
    fun setOnCloseListener(listener: OnCloseListener) {
        //mViewAbove.setOnCloseListener(listener);
        mCloseListener = listener
    }

    fun setOnOpenedListener(listener: OnOpenedListener) {
        customerViewAbove!!.setOnOpenedListener(listener)
    }

    fun setOnClosedListener(listener: OnClosedListener) {
        customerViewAbove!!.setOnClosedListener(listener)
    }

    fun setOnMenuDragListener(listener: OnMenuDragListener) {
        customerViewAbove!!.setOnMenuDragListener(listener)
    }

    class SavedState : View.BaseSavedState {

        val item: Int

        constructor(superState: Parcelable, item: Int) : super(superState) {
            this.item = item
        }

        private constructor(`in`: Parcel) : super(`in`) {
            item = `in`.readInt()
        }

        /* (non-Javadoc)
		 * @see android.view.AbsSavedState#writeToParcel(android.os.Parcel, int)
		 */
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(item)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }

    }

    /* (non-Javadoc)
	 * @see android.view.View#onSaveInstanceState()
	 */
    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        return SavedState(superState, customerViewAbove!!.currentItem)
    }

    /* (non-Javadoc)
	 * @see android.view.View#onRestoreInstanceState(android.os.Parcelable)
	 */
    override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        customerViewAbove!!.currentItem = ss.item
    }

    /* (non-Javadoc)
	 * @see android.view.ViewGroup#fitSystemWindows(android.graphics.Rect)
	 */
    override fun fitSystemWindows(insets: Rect): Boolean {
        val leftPadding = insets.left
        val rightPadding = insets.right
        val topPadding = insets.top
        val bottomPadding = insets.bottom
        if (!mActionbarOverlay) {
            Log.v(TAG, "setting padding!")
            setPadding(leftPadding, topPadding, rightPadding, bottomPadding)
        }
        return true
    }

    @SuppressLint("NewApi")
    fun manageLayers(percentOpen: Float) {
        if (Build.VERSION.SDK_INT < 11) return

        val layer = percentOpen > 0.0f && percentOpen < 1.0f
        val layerType = if (layer) View.LAYER_TYPE_HARDWARE else View.LAYER_TYPE_NONE

        if (layerType != content!!.layerType) {
            mHandler.post {
                Log.v(TAG, "changing layerType. hardware? " + (layerType == View.LAYER_TYPE_HARDWARE))
                content!!.setLayerType(layerType, null)
                menu!!.setLayerType(layerType, null)
                if (secondaryMenu != null) {
                    secondaryMenu!!.setLayerType(layerType, null)
                }
            }
        }
    }

    companion object {

        private val TAG = "SlidingMenu"

        val SLIDING_WINDOW = 0
        val SLIDING_CONTENT = 1

        val TOUCHMODE_MARGIN = 0

        val TOUCHMODE_FULLSCREEN = 1

        val TOUCHMODE_NONE = 2

        val LEFT = 0

        val RIGHT = 1

        val LEFT_RIGHT = 2
    }

}
/**
 * Instantiates a new SlidingMenu.
 *
 * @param context the associated Context
 */
/**
 * Instantiates a new SlidingMenu.
 *
 * @param context the associated Context
 * @param attrs the attrs
 */
/**
 * Attaches the SlidingMenu to an entire Activity
 *
 * @param activity the Activity
 * @param slideStyle either SLIDING_CONTENT or SLIDING_WINDOW
 */
/**
 * Opens the menu and shows the menu view.
 */
/**
 * Opens the menu and shows the secondary menu view. Will default to the regular menu
 * if there is only one.
 */
/**
 * Closes the menu and shows the above view.
 */
/**
 * Toggle the SlidingMenu. If it is open, it will be closed, and vice versa.
 */

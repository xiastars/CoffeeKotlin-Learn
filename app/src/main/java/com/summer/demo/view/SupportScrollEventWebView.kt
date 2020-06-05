package com.summer.demo.view

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.webkit.WebView
import android.widget.ZoomButtonsController
import com.summer.helper.listener.OnSimpleClickListener

class SupportScrollEventWebView : WebView {
    private var zoomController: ZoomButtonsController? = null
    private lateinit var onSingleTabListener: OnSimpleClickListener
    private var doubleTapDetecture: GestureDetector? = null

    constructor(context: Context) : super(context) {
        disableZoomController(context)
    }

    constructor(context: Context, attrs: AttributeSet,
                defStyle: Int) : super(context, attrs, defStyle) {
        disableZoomController(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        disableZoomController(context)
    }

    private fun disableZoomController(context: Context) {
        doubleTapDetecture = GestureDetector(context, GestureListener())
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            this.settings.builtInZoomControls = true
            this.settings.displayZoomControls = false
        } else {
            getControlls()
        }
    }

    private fun getControlls() {
        try {
            val webview = Class.forName("android.webkit.WebView")
            val method = webview.getMethod("getZoomButtonsController")
            zoomController = method.invoke(this, "") as ZoomButtonsController
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        super.onTouchEvent(ev)
        doubleTapDetecture!!.onTouchEvent(ev)
        if (zoomController != null) {
            // Hide the controlls AFTER they where made visible by the default
            // implementation.
            zoomController!!.isVisible = false
        }
        return true
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDoubleTap(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (onSingleTabListener != null) onSingleTabListener!!.onClick(0)
            return super.onSingleTapConfirmed(e)
        }
    }

    fun setOnSingleTabListener(onSingleTabListener:OnSimpleClickListener) {
        this.onSingleTabListener = onSingleTabListener
    }

}
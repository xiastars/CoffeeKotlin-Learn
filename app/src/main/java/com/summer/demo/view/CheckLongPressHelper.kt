package com.summer.demo.view

import android.view.View

class CheckLongPressHelper(private val mView: View) {
    private var mHasPerformedLongPress: Boolean = false
    private var mPendingCheckForLongPress: CheckForLongPress? = null

    internal inner class CheckForLongPress : Runnable {
        override fun run() {
            if (mView.parent != null && mView.hasWindowFocus()
                    && !mHasPerformedLongPress) {
                if (mView.performLongClick()) {
                    mView.isPressed = false
                    mHasPerformedLongPress = true
                }
            }
        }
    }

    fun postCheckForLongPress() {
        mHasPerformedLongPress = false

        if (mPendingCheckForLongPress == null) {
            mPendingCheckForLongPress = CheckForLongPress()
        }
        mView.postDelayed(mPendingCheckForLongPress, 300)
    }

    fun cancelLongPress() {
        mHasPerformedLongPress = false
        if (mPendingCheckForLongPress != null) {
            mView.removeCallbacks(mPendingCheckForLongPress)
            mPendingCheckForLongPress = null
        }
    }

    fun hasPerformedLongPress(): Boolean {
        return mHasPerformedLongPress
    }
}

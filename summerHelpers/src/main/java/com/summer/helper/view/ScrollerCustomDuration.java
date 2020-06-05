package com.summer.helper.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class ScrollerCustomDuration extends Scroller{
	private int mDuration = 500;

    public ScrollerCustomDuration(Context context) {
        super(context);
    }

    public ScrollerCustomDuration(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    @SuppressLint("NewApi")
    public ScrollerCustomDuration(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    /**
     * Set the duration will change
     */
    public void setScrollDuration(int mDuration) {
    	this.mDuration = mDuration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}

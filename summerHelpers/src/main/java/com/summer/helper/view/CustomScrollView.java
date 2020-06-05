package com.summer.helper.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

import com.summer.helper.listener.OnScrollListener;


public class CustomScrollView extends ScrollView {
    private View llTop;
    private int mTopHeight;
    private int downX;
    private int downY;
    private int mTouchSlop;

    OnScrollListener listener;


    public CustomScrollView(Context context) {
        super(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setOnScrollListner(OnScrollListener listener) {
        this.listener = listener;
    }

    public CustomScrollView(Context context, AttributeSet attr) {
        super(context, attr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (listener != null) {
            listener.onScroll(scrollX, scrollY, clampedX, clampedY);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int action = e.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) e.getRawX();
                downY = (int) e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) e.getRawY();
                if (Math.abs(moveY - downY) > mTouchSlop) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (listener != null) {
                    listener.onScroll(0, 0, true, true);
                }
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (null != llTop) {
            if (mTopHeight > t || oldt < 0) {
                llTop.setAlpha(1);
            } else {
                if (t > mTopHeight) {
                    float alpha = 1 - (t - mTopHeight) / (float) mTopHeight;
                    if (alpha <= 0) {
                        alpha = 0;

                    } else {

                    }
                    llTop.setAlpha(alpha);
                }
            }
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setTopView(View llTop) {
        this.llTop = llTop;
        this.mTopHeight = llTop.getLayoutParams().height;
    }

}

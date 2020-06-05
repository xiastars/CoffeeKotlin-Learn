package com.summer.helper.view;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;

public class CustomerViewPager extends ViewPager {

	private Rect mRect = new Rect();// 用来记录初始位置
	private int currentItem = 0;
	private boolean handleDefault = true;
	private float preX = 0f;
	private static final float RATIO = 0.5f;// 摩擦系数
	private static final float SCROLL_WIDTH = 30f;
	private float curX = 0f;  
	private float downX = 0f;
	private boolean left = false;
	private boolean right = false;

	public CustomerViewPager(Context context) {
		super(context);
		init();
		postInitViewPager();
	}

	public CustomerViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		postInitViewPager();
	}
	
	/**
	 * init method .
	 */
	private void init() {
//		setOnPageChangeListener(listener);
	}
	
	private ScrollerCustomDuration mScroller = null;

	/**
	 * Override the Scroller instance with our own class so we can change the
	 * duration
	 */
	private void postInitViewPager() {
		try {
			Class<?> viewpager = ViewPager.class;
			Field scroller = viewpager.getDeclaredField("mScroller");
			scroller.setAccessible(true);
			Field interpolator = viewpager.getDeclaredField("sInterpolator");
			interpolator.setAccessible(true);

			mScroller = new ScrollerCustomDuration(getContext(),
					(Interpolator) interpolator.get(null));
			scroller.set(this, mScroller);
		} catch (Exception e) {
		}
	}
	
	/**
	 * Set the duration will change
	 */
	public void setScrollDuration(int scrollFactor) {
		mScroller.setScrollDuration(scrollFactor);
	}
	
	//这是当前是第几页，请在onPageSelect方法中调用它。  
    public void setCurrentIndex(int currentItem) {  
        this.currentItem = currentItem;  
    } 

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			preX = ev.getX();// 记录起点
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		currentItem = getCurrentItem();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = curX;
			break;
		case MotionEvent.ACTION_UP:
			onTouchActionUp();
			break;
		case MotionEvent.ACTION_MOVE:
			// 当时滑到第一项或者是最后一项的时候。
			if ((currentItem == 0 || currentItem == getAdapter().getCount() - 1)) {
				float nowX = ev.getX();
				float offset = nowX - preX;
				preX = nowX;
				if (currentItem == 0) {
					if (offset > SCROLL_WIDTH) {// 手指滑动的距离大于设定值
						whetherConditionIsRight(offset);
					} else if (!handleDefault) {// 这种情况是已经出现缓冲区域了，手指慢慢恢复的情况
						if (getLeft() + (int) (offset * RATIO) >= mRect.left) {
							layout(getLeft() + (int) (offset * RATIO),getTop(), getRight()
											+ (int) (offset * RATIO),
									getBottom());
						}
					}
				} else {
					if (offset < -SCROLL_WIDTH) {
						whetherConditionIsRight(offset);
					} else if (!handleDefault) {
						if (getRight() + (int) (offset * RATIO) <= mRect.right) {
							layout(getLeft() + (int) (offset * RATIO),
									getTop(), getRight()
											+ (int) (offset * RATIO),
									getBottom());
						}
					}
				}
			} else {
				handleDefault = true;
			}

			if (!handleDefault) {
				return true;
			}
			break;

		default:
			break;
		}
		int curIndex = getCurrentItem();
		if (curIndex == 0) {
			if (downX <= curX) {
				getParent().requestDisallowInterceptTouchEvent(false);
			} else {
				getParent().requestDisallowInterceptTouchEvent(true);
			}
		} else if (curIndex == getAdapter().getCount() - 1) {
			if (downX >= curX) {
				getParent().requestDisallowInterceptTouchEvent(false);
			} else {
				getParent().requestDisallowInterceptTouchEvent(true);
			}
		} else {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
		return super.onTouchEvent(ev);
	}
	
	/**
	 * 得到是否向右侧滑动
	 * 
	 * @return true 为右滑动
	 */
	public boolean getMoveRight() {
		return right;
	}

	/**
	 * 得到是否向左侧滑动
	 * 
	 * @return true 为左做滑动
	 */
	public boolean getMoveLeft() {
		return left;
	}

	private void whetherConditionIsRight(float offset) {
		if (mRect.isEmpty()) {
			mRect.set(getLeft(), getTop(), getRight(), getBottom());
		}
		handleDefault = false;
		layout(getLeft() + (int) (offset * RATIO), getTop(), getRight()
				+ (int) (offset * RATIO), getBottom());
	}

	private void onTouchActionUp() {
		if (!mRect.isEmpty()) {
			recoveryPosition();
		}
	}

	private void recoveryPosition() {
		TranslateAnimation ta = new TranslateAnimation(getLeft(), mRect.left,
				0, 0);
		ta.setDuration(300);
		startAnimation(ta);
		layout(mRect.left, mRect.top, mRect.right, mRect.bottom);
		mRect.setEmpty();
		handleDefault = true;
	}
}

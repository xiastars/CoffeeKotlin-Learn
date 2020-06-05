package com.summer.helper.recycle;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.malata.summer.helper.R;
import com.summer.helper.adapter.SRecycleMoreAdapter;
import com.summer.helper.utils.Logs;
import com.summer.helper.utils.SUtils;
import com.summer.helper.view.CustomerViewPager;
import com.summer.helper.view.ScrollableLayout;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * @author https://github.com/android-cjj/Android-MaterialRefreshLayout
 */
public class MaterialRefreshLayout extends FrameLayout {

	public static final String Tag = MaterialRefreshLayout.class.getSimpleName();
	private final static int DEFAULT_WAVE_HEIGHT = 140;
	private final static int HIGHER_WAVE_HEIGHT = 180;
	private static int DEFAULT_HEAD_HEIGHT = 70;
	private final static int HIGHER_HEAD_HEIGHT = 100;
	/* 默认圈圈大小 */
	private static int DEFAULT_PROGRESS_SIZE = 40;
	private final static int BIG_PROGRESS_SIZE = 15;
	private final static int PROGRESS_STOKE_WIDTH = 3;

	private MaterialHeaderView mMaterialHeaderView;
	private MaterialFooterView mMaterialFooterView;
	private View rlNomoreDataView;
	private SunLayout mSunLayout;
	private boolean isOverlay;
	private int waveType;
	private int waveColor;
	protected float mWaveHeight;
	protected float mHeadHeight;
	protected View mChildView;
	private float mTouchY;
	private float mCurrentY;
	private float mTouchX;
	private float mCurrentX;
	private DecelerateInterpolator decelerateInterpolator;
	private float headHeight;
	private float waveHeight;
	private int[] colorSchemeColors;
	private int colorsId;
	private int progressTextColor;
	private int progressValue, progressMax;
	private boolean showArrow = false;
	private int textType;
	private MaterialRefreshListener refreshListener;
	private boolean showProgressBg;
	private int progressBg;
	private boolean isShowWave;
	private int progressSizeType;
	private int progressSize = 0;
	private boolean isSunStyle = false;
	/* 当前是否为横向 */
	private boolean isHor = false;
	//下拉刷新
	private boolean mPullDownRefreshable = true;
	private boolean mPullDownRefreshing;
	//上拉刷新
	private boolean mPullUpRefreshable = true;
	private boolean mPullUpRefreshing;

	/**
	 * 当有ViewPager时，优化ViewPager的滑动
	 */
	private CustomerViewPager mViewPager;

	View emptyView;
	private TextView tvHintContent;
	private String mHintContent;
	//是否显示空页面
	boolean showEmptyView = true;
	//有header的情况有时候不需要空页面，有时候需要
	boolean showEmptyViewDespiteHeader = false;

	public MaterialRefreshLayout(Context context) {
		this(context, null, 0);
	}

	public MaterialRefreshLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MaterialRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	private void init(Context context, AttributeSet attrs, int defstyleAttr) {
		if (isInEditMode()) {
			return;
		}

		if (getChildCount() > 1) {
			throw new RuntimeException("can only have one child widget");
		}

		decelerateInterpolator = new DecelerateInterpolator(10);

		final float density = getContext().getResources().getDisplayMetrics().density;
		TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.MaterialRefreshLayout, defstyleAttr, 0);
		isOverlay = t.getBoolean(R.styleable.MaterialRefreshLayout_overlay, false);
		/** attrs for materialWaveView */
		waveType = t.getInt(R.styleable.MaterialRefreshLayout_wave_height_type, 0);
		DEFAULT_HEAD_HEIGHT = SUtils.getDip(context, 45);
		if (waveType == 0) {
			headHeight = DEFAULT_HEAD_HEIGHT;
			waveHeight = DEFAULT_WAVE_HEIGHT;
			MaterialWaveView.DefaulHeadHeight = DEFAULT_HEAD_HEIGHT;
			MaterialWaveView.DefaulWaveHeight = DEFAULT_WAVE_HEIGHT;
		} else {
			headHeight = HIGHER_HEAD_HEIGHT;
			waveHeight = HIGHER_WAVE_HEIGHT;
			MaterialWaveView.DefaulHeadHeight = HIGHER_HEAD_HEIGHT;
			MaterialWaveView.DefaulWaveHeight = HIGHER_WAVE_HEIGHT;
		}
		waveColor = t.getColor(R.styleable.MaterialRefreshLayout_wave_color, Color.WHITE);
		isShowWave = t.getBoolean(R.styleable.MaterialRefreshLayout_wave_show, true);

		/** attrs for circleprogressbar */
		colorsId = t.getResourceId(R.styleable.MaterialRefreshLayout_progress_colors, R.array.material_colors);
		colorSchemeColors = context.getResources().getIntArray(colorsId);
		showArrow = t.getBoolean(R.styleable.MaterialRefreshLayout_progress_show_arrow, true);
		textType = t.getInt(R.styleable.MaterialRefreshLayout_progress_text_visibility, 1);
		progressTextColor = t.getColor(R.styleable.MaterialRefreshLayout_progress_text_color, Color.BLACK);
		progressValue = t.getInteger(R.styleable.MaterialRefreshLayout_progress_value, 0);
		progressMax = t.getInteger(R.styleable.MaterialRefreshLayout_progress_max_value, 100);
		showProgressBg = t.getBoolean(R.styleable.MaterialRefreshLayout_progress_show_circle_backgroud, true);
		progressBg = t.getColor(R.styleable.MaterialRefreshLayout_progress_backgroud_color,
				CircleProgressBar.DEFAULT_CIRCLE_BG_LIGHT);
		progressSizeType = t.getInt(R.styleable.MaterialRefreshLayout_progress_size_type, 0);
		SUtils.initScreenDisplayMetrics((Activity) context);
		progressSize = SUtils.getDip((Activity) context, BIG_PROGRESS_SIZE);
		mPullUpRefreshable = t.getBoolean(R.styleable.MaterialRefreshLayout_pullUpRefreshable, false);
		t.recycle();

		setRefreshView(new RecyclerView(context));
	}

	public void setIsHor() {
		isHor = true;
	}

	public void setRefreshView(View view) {
		removeAllViews();
		mChildView = view;
		if (mChildView.getParent() == null) {
			addView(mChildView);
		}
	}

	public void replaceRefreshView(View view) {
		if (mChildView != null) {
			removeView(mChildView);
		}
		mChildView = view;
		if (mChildView.getParent() == null) {
			addView(mChildView);
		}
	}

	public View getRefreshView() {
		return mChildView;
	}

	//多余的方法，为了兼容老代码
	public RecyclerView getRefreshViewForTypeRecycleView() {
		if (mChildView instanceof RecyclerView) {
			return (RecyclerView) mChildView;
		} else {
			return null;
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (mChildView == null) {
			new IllegalArgumentException("Refresh target view is null!");
		}

		Context context = getContext();

		setWaveHeight(Util.dip2px(context, waveHeight));
		setHeaderHeight(Util.dip2px(context, headHeight));

		if (isSunStyle) {
			mSunLayout = new SunLayout(context);
			LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					Util.dip2px(context, HIGHER_HEAD_HEIGHT));
			layoutParams.gravity = Gravity.TOP;
			mSunLayout.setVisibility(View.GONE);
			setHeaderView(mSunLayout);
		} else {
			mMaterialHeaderView = new MaterialHeaderView(context);
			LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					Util.dip2px(context, HIGHER_HEAD_HEIGHT));
			if (!isHor) {
				layoutParams.gravity = Gravity.TOP;
			} else {
				layoutParams.width = Util.dip2px(context, HIGHER_HEAD_HEIGHT);
				layoutParams.gravity = Gravity.CENTER_VERTICAL;
			}
			mMaterialHeaderView.setLayoutParams(layoutParams);
			mMaterialHeaderView.showProgressArrow(showArrow);
			mMaterialHeaderView.setProgressSize(progressSize);
			mMaterialHeaderView.setProgressColors(colorSchemeColors);
			mMaterialHeaderView.setProgressStokeWidth(PROGRESS_STOKE_WIDTH);
			mMaterialHeaderView.setTextType(textType);
			mMaterialHeaderView.setProgressValue(progressValue);
			mMaterialHeaderView.setProgressValueMax(progressMax);
			mMaterialHeaderView.setIsProgressBg(showProgressBg);
			mMaterialHeaderView.setProgressBg(progressBg);
			mMaterialHeaderView.setVisibility(View.GONE);
			setHeaderView(mMaterialHeaderView);
		}

		mMaterialFooterView = new MaterialFooterView(context);
		LayoutParams layoutParams2 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				Util.dip2px(context, HIGHER_HEAD_HEIGHT));
		if (!isHor) {
			layoutParams2.gravity = Gravity.BOTTOM;
		} else {
			layoutParams2.width = Util.dip2px(context, HIGHER_HEAD_HEIGHT);
			layoutParams2.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
		}
		mMaterialFooterView.setLayoutParams(layoutParams2);
		mMaterialFooterView.showProgressArrow(showArrow);
		mMaterialFooterView.setProgressSize(progressSize);
		mMaterialFooterView.setProgressColors(colorSchemeColors);
		mMaterialFooterView.setProgressStokeWidth(PROGRESS_STOKE_WIDTH);
		mMaterialFooterView.setTextType(textType);
		mMaterialFooterView.setProgressValue(progressValue);
		mMaterialFooterView.setProgressValueMax(progressMax);
		mMaterialFooterView.setIsProgressBg(showProgressBg);
		mMaterialFooterView.setProgressBg(progressBg);
		mMaterialFooterView.setVisibility(View.GONE);
		setFooderView(mMaterialFooterView);

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (isRefreshing() || !isRefreshable()) {
			return super.onInterceptTouchEvent(ev);
		}
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mTouchY = ev.getY();
				mCurrentY = mTouchY;
				mTouchX = ev.getX();
				mCurrentX = mTouchX;
				break;
			case MotionEvent.ACTION_MOVE:
				float currentY = ev.getY();
				float dy = currentY - mTouchY;
				float currentX = ev.getX();
				float dx = currentX - mTouchX;
				// 如果为竖向
				if (!isHor) {
					if (dx > 30) {//防止斜滑
						return false;
					}
					if (mPullDownRefreshable && !mPullDownRefreshing && dy > 5 && !canChildScrollUp() && !childCanScrollInParentWithUp(true)) {
						if (mMaterialHeaderView != null) {
							mMaterialHeaderView.setVisibility(View.VISIBLE);
							mMaterialHeaderView.onBegin(this);
						} else if (mSunLayout != null) {
							mSunLayout.setVisibility(View.VISIBLE);
							mSunLayout.onBegin(this);
						}
						return true;
					} else if (mPullUpRefreshable && !mPullUpRefreshing && dy < 0 && !canChildScrollDown() && !childCanScrollInParentWithUp(false)) {
						if (mMaterialFooterView != null) {
							soveLoadMoreLogic();
						}
						return super.onInterceptTouchEvent(ev);
					}
				} else {
					if (mPullDownRefreshable && !mPullDownRefreshing && dx > 0 && !canChildScrollUp() && !childCanScrollInParentWithUp(true)) {
						if (mMaterialHeaderView != null) {
							mMaterialHeaderView.setVisibility(View.VISIBLE);
							mMaterialHeaderView.onBegin(this);
						} else if (mSunLayout != null) {
							mSunLayout.setVisibility(View.VISIBLE);
							mSunLayout.onBegin(this);
						}
						return true;
					} else if (mPullUpRefreshable && !mPullUpRefreshing && dx < 10 && !canChildScrollDown() && !childCanScrollInParentWithUp(false)) {
						if (mMaterialFooterView != null) {
							soveLoadMoreLogic();
						}
						return super.onInterceptTouchEvent(ev);
					}
				}

				break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	private void soveLoadMoreLogic() {
		mPullUpRefreshing = true;
		mMaterialFooterView.setVisibility(View.VISIBLE);
		mMaterialFooterView.onBegin(this);
		mMaterialFooterView.onRefreshing(this);
		if (refreshListener != null) {
			refreshListener.onRefreshLoadMore(MaterialRefreshLayout.this);
		}
	}

	public void setAutoLoadmore() {
		if (refreshListener != null && mPullUpRefreshable && !mPullUpRefreshing) {
			mPullUpRefreshing = true;
			refreshListener.onRefreshLoadMore(MaterialRefreshLayout.this);
		}
	}

	public void setViewPager(CustomerViewPager viewPager) {
		this.mViewPager = viewPager;
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if (isRefreshing() || !isRefreshable()) {
			return super.onTouchEvent(e);
		}
		float dy = 0, dx = 0;
		switch (e.getAction()) {
			case MotionEvent.ACTION_MOVE:
				mCurrentY = e.getY();
				mCurrentX = e.getX();
				float axis;
				dy = mCurrentY - mTouchY;
				dx = mCurrentX - mTouchX;
				if (isHor) {
					axis = dx;
				} else {
					axis = dy;
				}
				axis = Math.min(mWaveHeight * 2, axis);
				axis = Math.max(0, axis);
				if (mChildView != null) {
					float offsetY = decelerateInterpolator.getInterpolation(axis / mWaveHeight / 2) * axis / 2;
					float fraction = offsetY / mHeadHeight;
					if (mMaterialHeaderView != null) {
						if (!isHor) {
							mMaterialHeaderView.getLayoutParams().height = (int) offsetY;
						} else {
							mMaterialHeaderView.getLayoutParams().width = (int) offsetY;
						}

						mMaterialHeaderView.requestLayout();
						mMaterialHeaderView.onPull(this, fraction);
					} else if (mSunLayout != null) {
						mSunLayout.getLayoutParams().height = (int) offsetY;
						mSunLayout.requestLayout();
						mSunLayout.onPull(this, fraction);
					}
					if (!isOverlay) {
						if (isHor) {
							ViewCompat.setTranslationX(mChildView, offsetY);
						} else {
							// ViewCompat.setTranslationY(mChildView, offsetY);
						}
					}

				}
				return true;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				if (mChildView != null) {
					if (mMaterialHeaderView != null) {
						if (isHor) {
							if (isOverlay) {
								if (mMaterialHeaderView.getLayoutParams().width > mHeadHeight) {
									updateListener();
									mMaterialHeaderView.getLayoutParams().width = (int) mHeadHeight;
									mMaterialHeaderView.requestLayout();

								} else {
									mMaterialHeaderView.getLayoutParams().width = 0;
									mMaterialHeaderView.requestLayout();
								}

							} else {
								/*
								 * if (ViewCompat.getTranslationY(mChildView) >=
								 * mHeadHeight) {
								 * createAnimatorTranslationY(mChildView,
								 * mHeadHeight, mMaterialHeaderView);
								 * updateListener(); } else {
								 * createAnimatorTranslationY(mChildView, 0,
								 * mMaterialHeaderView); }
								 */
							}
						} else {
							if (isOverlay) {
								if (mMaterialHeaderView.getLayoutParams().height > mHeadHeight) {

									updateListener();

									mMaterialHeaderView.getLayoutParams().height = (int) mHeadHeight;
									mMaterialHeaderView.requestLayout();

								} else {
									mMaterialHeaderView.getLayoutParams().height = 0;
									mMaterialHeaderView.requestLayout();
								}
							} else {
								if (mMaterialHeaderView.getLayoutParams().height >= mHeadHeight) {
									updateListener();
								} else {
									createAnimatorTranslationY(mChildView, 0, mMaterialHeaderView);
								}
							}
						}

					} else if (mSunLayout != null) {
						if (isOverlay) {
							if (mSunLayout.getLayoutParams().height > mHeadHeight) {

								updateListener();

								mSunLayout.getLayoutParams().height = (int) mHeadHeight;
								mSunLayout.requestLayout();

							} else {
								mSunLayout.getLayoutParams().height = 0;
								mSunLayout.requestLayout();
							}

						} else {
							if (ViewCompat.getTranslationY(mChildView) >= mHeadHeight) {
								createAnimatorTranslationY(mChildView, mHeadHeight, mSunLayout);
								updateListener();
							} else {
								createAnimatorTranslationY(mChildView, 0, mSunLayout);
							}
						}
					}

				}
				return true;
		}

		return super.onTouchEvent(e);
	}

	public void resetLocation() {
		if (mChildView != null) {
			mChildView.clearAnimation();
			mChildView.setTranslationX(0);
			mChildView.setTranslationY(0);
		}
	}

	public void setSunStyle(boolean isSunStyle) {
		this.isSunStyle = isSunStyle;
	}

	public void autoRefresh() {
		this.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!isRefreshing()) {
					if (mMaterialHeaderView != null) {
						mMaterialHeaderView.setVisibility(View.VISIBLE);

						if (isOverlay) {
							mMaterialHeaderView.getLayoutParams().height = (int) mHeadHeight;
							mMaterialHeaderView.requestLayout();
						} else {
							createAnimatorTranslationY(mChildView, mHeadHeight, mMaterialHeaderView);
						}
					} else if (mSunLayout != null) {
						mSunLayout.setVisibility(View.VISIBLE);
						if (isOverlay) {
							mSunLayout.getLayoutParams().height = (int) mHeadHeight;
							mSunLayout.requestLayout();
						} else {
							createAnimatorTranslationY(mChildView, mHeadHeight, mSunLayout);
						}
					}

					updateListener();

				}
			}
		}, 50);

	}

	public void autoRefreshLoadMore() {
		this.post(new Runnable() {
			@Override
			public void run() {
				if (mPullUpRefreshable) {
					soveLoadMoreLogic();
				} else {
					// throw new RuntimeException("you must setLoadMore ture");
				}
			}
		});
	}

	public void updateListener() {
		mPullDownRefreshing = true;

		if (mMaterialHeaderView != null) {
			mMaterialHeaderView.onRefreshing(MaterialRefreshLayout.this);
		} else if (mSunLayout != null) {
			mSunLayout.onRefreshing(MaterialRefreshLayout.this);
		}

		if (refreshListener != null) {
			refreshListener.onRefresh(MaterialRefreshLayout.this);
		}

	}

	public void setRefreshable(boolean able) {
		mPullDownRefreshable = able;
		mPullUpRefreshable = able;
	}

	public boolean isRefreshable() {
		return mPullDownRefreshable || mPullUpRefreshable;
	}

	public boolean isRefreshing() {
		return mPullDownRefreshing || mPullUpRefreshing;
	}

	public void setPullDownRefreshable(boolean able) {
		this.mPullDownRefreshable = able;
	}

	public boolean isPullDownRefreshable() {
		return mPullDownRefreshable;
	}

	public void setPullUpRefreshable(boolean able) {
		this.mPullUpRefreshable = able;
	}

	public boolean isPullUpRefreshable() {
		return mPullUpRefreshable;
	}

	public void setProgressColors(int[] colors) {
		this.colorSchemeColors = colors;
	}

	public void setShowArrow(boolean showArrow) {
		this.showArrow = showArrow;
	}

	public void setShowProgressBg(boolean showProgressBg) {
		this.showProgressBg = showProgressBg;
	}

	protected void setWaveColor(int waveColor) {
		this.waveColor = waveColor;
	}

	protected void setWaveShow(boolean isShowWave) {
		this.isShowWave = isShowWave;
	}

	public void setIsOverLay(boolean isOverLay) {
		this.isOverlay = isOverLay;
	}

//    public void setProgressValue(int progressValue) {
//        this.progressValue = progressValue;
//        mMaterialHeaderView.setProgressValue(progressValue);
//    }

	public void createAnimatorTranslationY(final View v, final float h, final FrameLayout fl) {
		ViewPropertyAnimatorCompat viewPropertyAnimatorCompat = ViewCompat.animate(v);
		viewPropertyAnimatorCompat.setDuration(250);
		viewPropertyAnimatorCompat.setInterpolator(new DecelerateInterpolator());
		viewPropertyAnimatorCompat.translationY(h);
		viewPropertyAnimatorCompat.start();
		viewPropertyAnimatorCompat.setUpdateListener(new ViewPropertyAnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(View view) {
				float height = ViewCompat.getTranslationY(v);
				fl.getLayoutParams().height = (int) height;
				fl.requestLayout();
			}
		});
	}

	/**
	 * @return Whether it is possible for the child view of this layout to
	 * scroll up. Override this if the child view is a custom view.
	 */
	public boolean canChildScrollUp() {
		if (mChildView == null) {
			return false;
		}
		if (Build.VERSION.SDK_INT < 14) {
			if (mChildView instanceof AbsListView) {
				final AbsListView absListView = (AbsListView) mChildView;
				return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0
						|| absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
			} else {
				if (isHor) {
					return ViewCompat.canScrollHorizontally(mChildView, -1) || mChildView.getScrollX() > 0;
				}
				return ViewCompat.canScrollVertically(mChildView, -1) || mChildView.getScrollY() > 0;
			}
		} else {
			if (isHor) {
				return ViewCompat.canScrollHorizontally(mChildView, -1);
			}
			if (mChildView instanceof PtrClassicFrameLayout) {
				View ptrChild = ((PtrClassicFrameLayout) mChildView).getChildAt(0);
				Logs.i("---" + ViewCompat.canScrollVertically(ptrChild, -1) + ",,," + (ptrChild.getScrollY() > 0));
				return ViewCompat.canScrollVertically(ptrChild, -1) || ptrChild.getScrollY() > 0;
			}

			return ViewCompat.canScrollVertically(mChildView, -1) || mChildView.getScrollY() > 0;
		}
	}

	public boolean canChildScrollDown() {
		if (mChildView == null) {
			return false;
		}
		if (Build.VERSION.SDK_INT < 14) {
			if (mChildView instanceof AbsListView) {
				final AbsListView absListView = (AbsListView) mChildView;
				if (absListView.getChildCount() > 0) {
					int lastChildBottom = absListView.getChildAt(absListView.getChildCount() - 1).getBottom();
					return absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1
							&& lastChildBottom <= absListView.getMeasuredHeight();
				} else {
					return false;
				}

			} else {
				if (isHor) {
					return ViewCompat.canScrollHorizontally(mChildView, 1) || mChildView.getScrollX() > 0;
				}
				return ViewCompat.canScrollVertically(mChildView, 1) || mChildView.getScrollY() > 0;
			}
		} else {
			if (isHor) {
				return ViewCompat.canScrollHorizontally(mChildView, 1);
			}
			if (mChildView instanceof PtrClassicFrameLayout) {
				ViewGroup ptrChild = (ViewGroup) ((PtrClassicFrameLayout) mChildView).getChildAt(0);
				Logs.i("mChildView.getScrollY()" + ((PtrClassicFrameLayout) mChildView).getChildAt(0).getScrollY() + ",,,," + mChildView.getTranslationY());
				Logs.i("" + ViewCompat.canScrollVertically(mChildView, 1) + ",,," + ViewCompat.canScrollVertically(ptrChild.getChildAt(0), 1));
				return ViewCompat.canScrollVertically(mChildView, 1);
			}
			return ViewCompat.canScrollVertically(mChildView, 1);
		}
	}

	/**
	 * 可支持刷新的目标view是否可以在某个可滚动的父view里可以滚动
	 *
	 * @param up true为可以往下拉,false反之
	 */
	protected boolean childCanScrollInParentWithUp(boolean up) {
		if (mChildView == null) {
			return false;
		}
		ViewGroup parent = getScrollParentView(mChildView);
		if (parent == null) {
			return false;
		} else {
			int parentScrollY = parent.getScrollY();
			//ViewCompat.canScrollVertically对于自定义的会失效
			if (up) {
				return parentScrollY > 0;
			} else {
				int h = 0;
				int pc = parent.getChildCount();
				for (int i = 0; i < pc; i++)
					h += parent.getChildAt(i).getHeight();
//                int childY = (int) getViewLocationInParentView(mChildView, parent).y;
//                Logs.d("zxc", "ccc "+parentScrollY+" "+h+" "+parent.getHeight()+" "+parent.getTop()+" "+parent.getBottom());
				return parentScrollY < h - parent.getHeight();
			}
		}
	}

	/**
	 * @param view
	 * @return 如果返回为空代表刷新的目标view和当前view之间不包含可滚动的视图
	 */
	private ViewGroup getScrollParentView(View view) {
		ViewParent parent = view.getParent();
		if (parent == this || !(parent instanceof ViewGroup))
			return null;
		ViewGroup viewGroup = (ViewGroup) parent;
		if (viewGroup == null)
			return null;
		//如果是自定义的滚动父view，不是继承于安卓自带的可滚动View，加上（|| instanceof）判断
		if (viewGroup.isScrollContainer() || viewGroup instanceof NestedScrollView || viewGroup instanceof ScrollableLayout) {
			return viewGroup;
		} else {
			return getScrollParentView(viewGroup);
		}
	}

	public void setWaveHigher() {
		headHeight = HIGHER_HEAD_HEIGHT;
		waveHeight = HIGHER_WAVE_HEIGHT;
		MaterialWaveView.DefaulHeadHeight = HIGHER_HEAD_HEIGHT;
		MaterialWaveView.DefaulWaveHeight = HIGHER_WAVE_HEIGHT;
	}

	public void finishRefreshing() {
		if (mChildView != null) {
			ViewPropertyAnimatorCompat viewPropertyAnimatorCompat = ViewCompat.animate(mChildView);
			viewPropertyAnimatorCompat.setDuration(200);
			viewPropertyAnimatorCompat.y(ViewCompat.getTranslationY(mChildView));
			viewPropertyAnimatorCompat.translationY(0);
			viewPropertyAnimatorCompat.setInterpolator(new DecelerateInterpolator());
			viewPropertyAnimatorCompat.start();

			if (mMaterialHeaderView != null) {
				mMaterialHeaderView.onComlete(MaterialRefreshLayout.this);
			} else if (mSunLayout != null) {
				mSunLayout.onComlete(MaterialRefreshLayout.this);
			}

			if (refreshListener != null) {
				refreshListener.onfinish();
			}
		}
		showEmptyView();
		mPullDownRefreshing = false;
		progressValue = 0;
	}

	public void finishPullDownRefresh() {
		this.post(new Runnable() {
			@Override
			public void run() {
				finishRefreshing();
			}
		});
	}

	public void finishPullUpRefresh() {
		this.post(new Runnable() {
			@Override
			public void run() {
				if (mMaterialFooterView != null && mPullUpRefreshing) {
					mPullUpRefreshing = false;
					mMaterialFooterView.onComlete(MaterialRefreshLayout.this);
				}
			}
		});

		showEmptyView();
	}

	public void showEmptyView() {
		if (!showEmptyView) {
			return;
		}
		if (mChildView != null && mChildView instanceof RecyclerView) {
			RecyclerView.Adapter adapter = ((RecyclerView) mChildView).getAdapter();
			if (adapter != null) {
				int count = adapter.getItemCount();
				int addBottom = 0;
				int headCount = 0;
				if (adapter instanceof SRecycleMoreAdapter) {
					SRecycleMoreAdapter sAdapter = (SRecycleMoreAdapter) adapter;
					addBottom = 1;
					headCount = sAdapter.getHeaderCount();
					if (showEmptyViewDespiteHeader) {
						if (headCount > 0)
							addBottom += headCount;
					}

				}
				Logs.i("显示空页面" + count + ",,," + addBottom);
				if (count == addBottom) {
					if (emptyView == null) {
						emptyView = LayoutInflater.from(getContext()).inflate(R.layout.view_empty, null);
						tvHintContent = (TextView) emptyView.findViewById(R.id.tv_hint_content);
						if (mHintContent != null) {
							tvHintContent.setText(mHintContent);
						}
						ImageView ivNav = emptyView.findViewById(R.id.iv_nav);
						SUtils.setPicResource(ivNav, R.drawable.blank_empty_img);
						this.addView(emptyView);
						/* 有头部View的情况下，改写EmptyView的位置 */
						if (headCount > 0 && showEmptyViewDespiteHeader) {
							View child = ((RecyclerView) mChildView).getChildAt(0);
							if(child != null){
								int headHeight = child.getHeight();
								LayoutParams headerParam = (LayoutParams) emptyView.getLayoutParams();
								headerParam.topMargin = headHeight;
							}

						}
						setPullUpRefreshable(false);
					}
				} else {
					if (emptyView != null) {
						this.removeView(emptyView);
						emptyView = null;
					}
				}
			}
		}
	}

	public void showEmptyView(String content, String btnContent, int drawableRes,int marginTop, OnClickListener listener) {
		if (emptyView != null) {
			this.removeView(emptyView);
			emptyView = null;
		}
		emptyView = LayoutInflater.from(getContext()).inflate(R.layout.view_empty, null);
		TextView tvReload = emptyView.findViewById(R.id.tv_reload);
		ImageView ivNav = emptyView.findViewById(R.id.iv_nav);
		SUtils.setPicResource(ivNav, drawableRes);

		tvReload.setOnClickListener(listener);
		if(btnContent != null){
			tvReload.setText(btnContent);
			tvReload.setVisibility(View.VISIBLE);
		}
		tvHintContent = (TextView) emptyView.findViewById(R.id.tv_hint_content);
		tvHintContent.setText(content);
		this.addView(emptyView);
		FrameLayout.LayoutParams params = (LayoutParams) emptyView.getLayoutParams();
		params.width = SUtils.screenWidth;
		params.topMargin = marginTop;
		setPullUpRefreshable(false);
	}


	public void setHintContent(int id) {
		setHintContent(getContext().getString(id));
	}

	public void setHintContent(String str) {
		if (tvHintContent != null)
			tvHintContent.setText(str);
		mHintContent = str;
	}

	/**
	 * 隐藏空状态
	 */
	public void hideEmptyView() {
		if (emptyView != null) {
			this.removeView(emptyView);
			emptyView = null;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private void setHeaderView(final View headerView) {
		addView(headerView);
	}

	public void setHeader(final View headerView) {
		setHeaderView(headerView);
	}

	public void setFooderView(final View fooderView) {
		this.addView(fooderView);
	}


	public void setWaveHeight(float waveHeight) {
		this.mWaveHeight = waveHeight;
	}

	public void setHeaderHeight(float headHeight) {
		this.mHeadHeight = headHeight;
	}

	public void setMaterialRefreshListener(MaterialRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	public void setShowEmptyView(boolean show) {
		showEmptyView = show;
	}

	public boolean isShowEmptyViewDespiteHeader() {
		return showEmptyViewDespiteHeader;
	}

	public void setShowEmptyViewDespiteHeader(boolean showEmptyViewDespiteHeader) {
		this.showEmptyViewDespiteHeader = showEmptyViewDespiteHeader;
	}
}

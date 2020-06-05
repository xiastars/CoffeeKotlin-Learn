package com.summer.helper.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malata.summer.helper.R;
import com.summer.helper.utils.Logs;
import com.summer.helper.utils.SAnimUtils;
import com.summer.helper.utils.SUtils;
import com.summer.helper.utils.SViewUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 自定义RecycleView,继承原生，不支持下拉刷新 
 * @author xiaqiliang
 *
 */
public class NRecycleView extends RecyclerView {
	LayoutManager mManager;

	/**
	 * 方向
	 */
	int orientation = ListItemDecoration.VERTICAL_LIST;
	/**
	 * GridView列数
	 */
	int numGrid;
	/**
	 * 按键操作时的主动滚动
	 */
	boolean mScroll;
	/**
	 * 按键操作时判断是横向
	 */
	boolean isHor;

	int mSelected;
	/**
	 * 切换选中时是否需要更新Adapter
	 */
	boolean shouldNotify;

	MyHandler mHandler;
	Context context;
	View fakeView;

	boolean shouldMeasure;
	View emptyView;
	FrameLayout flTime;
	TextView tvPreYear;//滚动时，先前占位的

	Map<Integer, String> movieTimeMaps;
	int scrollToX;
	int itemSize;
	int curIndex;//在最左的那个
	int curPosition;
	int tempScroll;
	List<TextView> mChilds;
	boolean isLeft;
	int preX;
	private int mMaxHeight = -1;

	public NRecycleView(Context context) {
		super(context);
		this.context = context;
		mHandler = new MyHandler(this);
	}

	public NRecycleView(Context context, AttributeSet attri) {
		super(context, attri);
		this.context = context;
		mHandler = new MyHandler(this);
	}

	public NRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		mHandler = new MyHandler(this);
	}

	public void setList() {
		SUtils.initScreenDisplayMetrics((Activity) getContext());
		this.setLayoutManager(new LinearLayoutManager(getContext()));
	}

	public void setListInScrollView() {
		SUtils.initScreenDisplayMetrics((Activity) getContext());
		this.setNestedScrollingEnabled(false);
		this.setLayoutManager(new FullLinearLayoutManager(getContext()) {
			@Override
			public boolean canScrollVertically() {
				return false;
			}
		});
	}

	public void setGridDivider(int color, int size) {
		addItemDecoration(new GridItemDecoration(getContext(), color, size));
	}

	public void setDivider() {
		this.addItemDecoration(new GridItemDecoration(getContext()));

	}

	public void setCommonDividerGrey(int left, int right) {
		int color = getContext().getResources().getColor(R.color.grey_e1);
		Rect rect = new Rect();
		rect.left = left;
		rect.right = right;
		setDividerColor(color, rect);
	}

	public void setDividerColor(int color, Rect rect) {
		addItemDecoration(new ListItemDecoration(getContext(), LinearLayoutManager.VERTICAL, color, rect));
	}

	public void setInterval(int color, int size) {
		LinearLayoutManager manager = (LinearLayoutManager) getLayoutManager();
		addItemDecoration(new ListItemDecoration(getContext(), manager.getOrientation(), color, size));
	}

	public void setHorizontalList() {
		SUtils.initScreenDisplayMetrics((Activity) getContext());
		isHor = true;
		numGrid = 10000;
		LinearLayoutManager manager = new LinearLayoutManager(getContext());
		manager.setOrientation(LinearLayoutManager.HORIZONTAL);
		this.setLayoutManager(manager);
	}

	public int getGridNum() {
		return numGrid;
	}

	public void setGridView(int num) {
		numGrid = num;
		this.setLayoutManager(new GridLayoutManager(getContext(), num));
	}

	public void setGridViewInScrollView(int num) {
		numGrid = num;
		SUtils.initScreenDisplayMetrics((Activity) getContext());
		this.setNestedScrollingEnabled(false);
		this.setLayoutManager(new GridLayoutManager(getContext(), num) {
			@Override
			public boolean canScrollVertically() {
				return false;
			}
		});
	}

	/**
	 * 如果为true，则每过一行滚动一下，反之到最底下才滚动
	 *
	 * @param scroll
	 */
	public void setScroll(boolean scroll) {
		this.mScroll = scroll;
	}

	public void showEmptyView() {
		showEmptyView(context.getString(R.string.tip_empty_content),null,null);
	}

	public void showEmptyView(String content,String btnContent,OnClickListener onClickListener) {
		RecyclerView.Adapter adapter = this.getAdapter();
		if (adapter != null) {
			if (emptyView == null) {
				emptyView = LayoutInflater.from(getContext()).inflate(R.layout.view_empty, null);
				TextView tvContent = (TextView) emptyView.findViewById(R.id.tv_hint_content);

				TextView tvNext = emptyView.findViewById(R.id.tv_reload);
				tvContent.setText(content);
				if(btnContent != null){
					tvNext.setText(btnContent);
					tvNext.setVisibility(View.VISIBLE);
					tvNext.setOnClickListener(onClickListener);
				}

				SUtils.initScreenDisplayMetrics((Activity) getContext());
				((ViewGroup) this.getParent()).addView(emptyView);
				if(this.getParent() instanceof RelativeLayout){
					RelativeLayout parentView = (RelativeLayout) this.getParent();
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) emptyView.getLayoutParams();
					params.addRule(RelativeLayout.CENTER_IN_PARENT);
				}
			}
		}
	}

	public void notifyScoll(int cur, int all, boolean reverse) {
		if (cur >= all) {
			return;
		}
		if (mScroll) {
			int height = this.getChildAt(0).getHeight();
			if (reverse) {
				height *= -1;
			}
			this.smoothScrollBy(0, height);
		} else {
			LayoutManager manager = getLayoutManager();
			View child = manager.findViewByPosition(cur);
			int height = manager.getChildAt(0).getHeight();
			if (child != null) {
				if (isHor) {
					int width = manager.getChildAt(0).getWidth();
					if (child.getRight() + width > SUtils.screenWidth) {
						smoothScrollBy(width, 0);
					} else if (child.getLeft() - width < 0) {
						smoothScrollBy(-width, 0);
					}
				} else {
					if (child.getBottom() > SUtils.screenHeight) {
						smoothScrollBy(0, height);
					} else if (child.getTop() - height < 0) {
						smoothScrollBy(0, -height);
					}
				}

			} else {
				smoothScrollBy(0, height);
			}
		}
	}

	private int mCount;

	@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		super.onMeasure(widthSpec, heightSpec);
		int height = getMeasuredHeight();
//        int c = getChildCount();
//        Logs.d("zxc", "onMeasure "+height+" "+getLayoutParams().height+" "+mMinHeight+" "+mMaxHeight);

		if (mMaxHeight > -1) {
//            int h = height<mMinHeight ? mMinHeight : (height>mMaxHeight ? mMaxHeight : height);
			int h = height > mMaxHeight ? mMaxHeight : height;
			if (h != height)
				getLayoutParams().height = h;
            /*Logs.d("zxc", "sssss "+h);
            if (height < mMinHeight){
                getLayoutParams().height = mMinHeight;
            }
            if (height==mMinHeight && c<getAdapter().getItemCount()){

            }
            if (height > mMaxHeight){
                getLayoutParams().height = mMaxHeight;
            }*/
		}
	}

	@Override
	public void onViewAdded(View child) {
		super.onViewAdded(child);
		if (shouldMeasure) {
			int height = getViewHeight();
			//getLayoutParams().height = height;
		}
	}

	public void setShouldMeasure() {
		shouldMeasure = true;
		invalidate();
	}

	public int getmSelected() {
		return mSelected;
	}

	public void setmSelected(int mSelected) {
		this.mSelected = mSelected;
		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageDelayed(0, 30);
		if (shouldNotify) {
			this.getAdapter().notifyDataSetChanged();
		}
		/* 当焦点退出时，如果有滚动，则回退 */
		if (mSelected == -1) {
			scrollToPosition(0);
		}
	}

	public static class MyHandler extends Handler {
		private final WeakReference<NRecycleView> mActivity;

		public MyHandler(NRecycleView activity) {
			mActivity = new WeakReference<NRecycleView>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			NRecycleView activity = mActivity.get();
			if (null != activity) {
				switch (msg.what) {
					case 0:
						LayoutManager manager = activity.getLayoutManager();
						int count = manager.getChildCount();
						for (int i = 0; i < count; i++) {
							View v = manager.getChildAt(i);
							if (v != null) {
								v.setScaleX(1.0f);
								v.setScaleY(1.0f);
							}
						}
						View child = manager.findViewByPosition(activity.mSelected);
						if (child != null) {
							SAnimUtils.scale(child, 1.5f, false);
							activity.addFakeView(child);
						}
						break;
				}
			}
		}
	}

	private void addFakeView(View view) {
		if (this.getParent() == null) {
			return;
		}
/*
        ViewGroup viewGroup = (ViewGroup) this.getParent();
		if (fakeView != null) {
			viewGroup.removeView(fakeView);
		}
		fakeView = new View(getContext());
		fakeView.draw(view.getC);
		fakeView = view;
		viewGroup.addView(fakeView);*/
        /*F
        mFake = new JMenu(context);
		mFake.setDefaultIcon(defaultIcon);
		mFake.setLayoutPosition(mParams.leftMargin, mParams.topMargin);
		mFake.measureLayout(childParams.width, childParams.height);*/
	}

	public int getViewHeight() {
		int count = this.getChildCount();
		int height = 0;
		for (int i = 0; i < count; i++) {
			View firstVisibleItem = this.getChildAt(i);
			int itemHeight = firstVisibleItem.getHeight();
			height += itemHeight;
		}
		return height;
	}

	public int getViewWidth() {
		int count = this.getChildCount();
		int height = 0;
		for (int i = 0; i < count; i++) {
			View firstVisibleItem = this.getChildAt(i);
			int itemHeight = firstVisibleItem.getWidth();
			height += itemHeight;
		}
		return height;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public int getViewScrollX() {
		LayoutManager manager = this.getLayoutManager();
		if (manager instanceof LinearLayoutManager) {
			LinearLayoutManager layoutManager = (LinearLayoutManager) this.getLayoutManager();
			int firstItemPosition = layoutManager.findFirstVisibleItemPosition();
			View firstVisibleItem = this.getChildAt(0);
			int itemWidth = firstVisibleItem.getWidth();
			int firstItemRight = layoutManager.getDecoratedRight(firstVisibleItem);
			return (this.getAdapter().getItemCount() * itemWidth - (firstItemPosition + 2) * itemWidth
					+ firstItemRight);
		}
		return 0;
	}

	/**
	 * 头部增加一个可滑动的标题
	 */
	public void setHeaderScrollTitleView(FrameLayout flTime, int itemSize, final Map<Integer, String> movieTimeMaps) {
		this.flTime = flTime;
		this.movieTimeMaps = movieTimeMaps;
		createTimeLine();
		final int layoutWidth = SUtils.getDip(context, 75);
		this.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView nvContainerView, int dx, int dy) {
				super.onScrolled(nvContainerView, dx, dy);

				scrollToX += dx;
				isLeft = scrollToX - preX > 0;
				preX = scrollToX;
				tempScroll += dx;
				if (mChilds != null) {
					int childSize = mChilds.size();
					for (int i = 0; i < childSize; i++) {
						TextView tvChild = mChilds.get(i);
						int tag = (int) tvChild.getTag();
						if (scrollToX == 0) {
							tempScroll = 0;
							curIndex = 0;
							tvPreYear = null;
							tvChild.setTranslationX(0);
							continue;
						}

						if (tag != curIndex) {
							//当靠近前一个标识时，前一个标识开始移动
							if (tvPreYear != null && tvPreYear.equals(tvChild)) {
								tvChild.setTranslationX(-tempScroll + tvChild.getTranslationX());
								//如果向左，看不到时，重新设置位置
								if (isLeft) {
									if (tvChild.getX() < -layoutWidth) {
										tvChild.setTranslationX(-scrollToX);
										tempScroll = 0;
										tvPreYear = null;
									}
								}
							} else {
								tvChild.setTranslationX(-scrollToX);
								if (isLeft) {
									//转移轴心
									if (tvChild.getX() > 0 && tvChild.getX() < SUtils.getDip(context, 55)) {
										curIndex = tag;
										if (tvPreYear == null) {
											tempScroll = 0;
											getPreTextView(i + 1);
										}
									}
								}
							}
						} else {
							if (isLeft) {
								if (isLeft) {
									if (tvChild.getX() < -layoutWidth) {
										tvChild.setTranslationX(-scrollToX);
										tempScroll = 0;
										tvPreYear = null;
									}
								}
								if (tvChild.getX() > SUtils.getDip(context, 5)) {
									tvChild.setTranslationX(-scrollToX);
								}
							} else {
								if (tvChild.getX() > SUtils.getDip(context, 55)) {
									curIndex = tag;
								}
								if (scrollToX < SViewUtils.getViewLeMargin(tvChild)) {
									tvChild.setTranslationX(-scrollToX);
									tempScroll += dx;
								}
							}

						}
					}
				}
			}
		});
	}

	/**
	 * 头部增加一个可滑动的标题
	 */
	public void emphasizeCenterView(FrameLayout flTime, int itemSize, final Map<Integer, String> movieTimeMaps) {
		this.flTime = flTime;
		this.movieTimeMaps = movieTimeMaps;
		createTimeLine();
		final int layoutWidth = SUtils.getDip(context, 90);
		Point centerPoint = new Point();
		centerPoint.x = SUtils.screenWidth / 2 - layoutWidth / 2;
		centerPoint.y = centerPoint.x + layoutWidth;
		final LayoutManager manager = getLayoutManager();
		final int childCount = manager.getChildCount();
		Logs.i("size:" + childCount);
		this.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView nvContainerView, int dx, int dy) {
				super.onScrolled(nvContainerView, dx, dy);

				scrollToX += dx;

				isLeft = scrollToX - preX > 0;


				preX = scrollToX;
				tempScroll += dx;
				if (mChilds != null) {
					int childSize = mChilds.size();
					for (int i = 0; i < childSize; i++) {
						TextView tvChild = mChilds.get(i);
						int tag = (int) tvChild.getTag();
						if (scrollToX == 0) {
							tempScroll = 0;
							curIndex = 0;
							tvPreYear = null;
							tvChild.setTranslationX(0);
							continue;
						}

						if (tag != curIndex) {
							//当靠近前一个标识时，前一个标识开始移动
							if (tvPreYear != null && tvPreYear.equals(tvChild)) {
								tvChild.setTranslationX(-tempScroll + tvChild.getTranslationX());
								//如果向左，看不到时，重新设置位置
								if (isLeft) {
									if (tvChild.getX() < -layoutWidth) {
										tvChild.setTranslationX(-scrollToX);
										tempScroll = 0;
										tvPreYear = null;
									}
								}
							} else {
								tvChild.setTranslationX(-scrollToX);
								if (isLeft) {
									//转移轴心
									if (tvChild.getX() > 0 && tvChild.getX() < SUtils.getDip(context, 55)) {
										curIndex = tag;
										if (tvPreYear == null) {
											tempScroll = 0;
											getPreTextView(i + 1);
										}
									}
								}
							}
						} else {
							if (isLeft) {
								if (isLeft) {
									if (tvChild.getX() < -layoutWidth) {
										tvChild.setTranslationX(-scrollToX);
										tempScroll = 0;
										tvPreYear = null;
									}
								}
								if (tvChild.getX() > SUtils.getDip(context, 5)) {
									tvChild.setTranslationX(-scrollToX);
								}
							} else {
								if (tvChild.getX() > SUtils.getDip(context, 55)) {
									curIndex = tag;
								}
								if (scrollToX < SViewUtils.getViewLeMargin(tvChild)) {
									tvChild.setTranslationX(-scrollToX);
									tempScroll += dx;
								}
							}

						}
					}
				}
			}
		});
	}

	private void getPreTextView(int tag) {
		if (mChilds == null) {
			return;
		}
		if (tag < 0 || tag >= mChilds.size()) {
			return;
		}
		tvPreYear = mChilds.get(tag);
	}

	private void createTimeLine() {
		LinearLayout.LayoutParams timeLayoutParam = (LinearLayout.LayoutParams) flTime.getLayoutParams();
		timeLayoutParam.width = getLayoutManager().getWidth();
		Logs.i("width:" + timeLayoutParam.width);
		if (movieTimeMaps == null) {
			return;
		}
		int dip10 = SUtils.getDip(context, 10);
		Set<Map.Entry<Integer, String>> entrySet = movieTimeMaps.entrySet();
		View lineView = new View(context);
		lineView.setBackgroundColor(context.getResources().getColor(R.color.grey_f1));
		flTime.addView(lineView);
		FrameLayout.LayoutParams lineParams = (FrameLayout.LayoutParams) lineView.getLayoutParams();
		lineParams.width = timeLayoutParam.width;
		lineParams.height = SUtils.getDip(context, 1);
		lineParams.rightMargin = dip10;
		lineParams.topMargin = dip10;
		for (Map.Entry<Integer, String> entry : entrySet) {
			int key = entry.getKey();
			String value = entry.getValue();
			if (mChilds == null) {
				mChilds = new ArrayList<>();
			}

			TextView tvYear = new TextView(context);
			tvYear.setBackgroundColor(context.getResources().getColor(R.color.white));
			tvYear.setTag(key);
			tvYear.setPadding(dip10, 0, dip10, 0);

			flTime.addView(tvYear);
			tvYear.setText(value);
			mChilds.add(tvYear);
			tvYear.setTextColor(context.getResources().getColor(R.color.black));
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			tvYear.setLayoutParams(params);
			params.leftMargin = SUtils.getDip(context, 85) * key + key * SUtils.getDip(context, 10) + dip10 / 2;
			params.width = SUtils.getDip(context, 75);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		return super.onTouchEvent(e);
	}

	public boolean isHor() {
		return isHor;
	}

	public void setHor(boolean isHor) {
		this.isHor = isHor;
	}

	public boolean isShouldNotify() {
		return shouldNotify;
	}

	public void setShouldNotify() {
		this.shouldNotify = true;
	}

	public int getMaxHeight() {
		return mMaxHeight;
	}

	public void setMaximumHeight(int height) {
		this.mMaxHeight = height;
		requestLayout();
	}
}

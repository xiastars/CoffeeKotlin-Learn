package com.summer.helper.recycle;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.malata.summer.helper.R;
import com.summer.helper.listener.OnSimpleClickListener;
import com.summer.helper.utils.Logs;
import com.summer.helper.view.CustomScrollView;
import com.summer.helper.view.NRecycleView;

/**
 * @author https://github.com/android-cjj/Android-MaterialRefreshLayout
 */
public class ScollViewRefreshLayout extends MaterialRefreshLayout {

    private CustomScrollView refreshView;

    View viewContent;
    ImageView ivIcon;
    TextView tvContent;

    int preHeight = 0;
    int maxEggHeight;
    Rect childViewRect;
    //显示底部彩蛋
    boolean isShowEgg;
    float mTouchY;
    boolean isOnShowEgg;//正在显示彩蛋
    OnSimpleClickListener onSimpleClickListener;

    public ScollViewRefreshLayout(Context context) {
        this(context, null, 0);
    }

    public ScollViewRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScollViewRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        refreshView = new CustomScrollView(context);
        setRefreshView(refreshView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (viewContent == null) {
            viewContent = getChildAt(0);
        }
        childViewRect = new Rect(viewContent.getLeft(), viewContent.getTop(), viewContent.getRight(), viewContent.getBottom());
    }

    public void setOnSimpleClickListener(OnSimpleClickListener listener) {
        this.onSimpleClickListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (onSimpleClickListener != null) {
            onSimpleClickListener.onClick(e.getAction());
        }
        return super.onTouchEvent(e);
    }

    /**
     * 设置上拉加载
     */
    public void setLoadMore() {
        this.setPullUpRefreshable(true);
    }

    public void disableLoadMore() {
        this.setPullUpRefreshable(false);
    }

    /**
     * 设置圈圈在View上面
     */
    public void setOverLay() {
        setIsOverLay(true);
    }

    public void addRefreshView(View view) {
        if (view == null) {
            view = getChildAt(0);
        }
        viewContent = view;
        if (view.getParent() == null) {
            refreshView.addView(view);
        }
        view.setBackgroundColor(view.getContext().getResources().getColor(R.color.white));
        view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        view.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        view.invalidate();
        view.requestLayout();
    }

    public void scrollContentViewTo(int x, int y) {
        refreshView.scrollTo(x, y);
    }

    /**
     * 显示最底下的彩蛋
     *
     * @param resString
     * @param resDrawable
     */
    public void setShowEggView(int resString, int resDrawable) {
        isShowEgg = true;
        ivIcon.setBackgroundResource(resDrawable);
        tvContent.setText(resString);
    }

    public CustomScrollView getScrollView() {
        return refreshView;
    }


    /**
     * 设置关联的RecycleView，用来自加载
     *
     * @param recyleView
     */
    NRecycleView recyleView;

    public void setRecyleView(NRecycleView recyleView) {
        Logs.i("xia", canChildScrollDown() + ",,,");
        this.recyleView = recyleView;
    }

}

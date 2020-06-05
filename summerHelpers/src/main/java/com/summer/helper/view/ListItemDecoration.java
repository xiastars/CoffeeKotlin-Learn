package com.summer.helper.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.summer.helper.adapter.SRecycleMoreAdapter;
import com.summer.helper.utils.SUtils;

/**
 * 设置分割线
 *
 * @author xiaqiliang
 */
public class ListItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    private Drawable mDividerDrawable;

    private int mOrientation;

    private int mDivideSize;

    private Paint mPaint;
    //    private int mPaintColor;
    private Rect mRectPadding;

    public ListItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDividerDrawable = a.getDrawable(0);
        a.recycle();
        setOrientation(VERTICAL_LIST);
    }

    public ListItemDecoration(Context context, Drawable drawable) {
        this(context, VERTICAL_LIST, drawable);
    }

    public ListItemDecoration(Context context, int orientation, Drawable drawable) {
        mDividerDrawable = drawable;
        mRectPadding = new Rect();
        setOrientation(orientation);
    }

    public ListItemDecoration(Context context, int color) {
        this(context, VERTICAL_LIST, color, new Rect());
    }

    public ListItemDecoration(Context context, int orientation, int color, Rect rectPadding) {
        mDivideSize = (int) SUtils.getDip(context, 0.5f);
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStrokeWidth(mDivideSize);
        this.mRectPadding = rectPadding;
        setOrientation(orientation);
    }

    public ListItemDecoration(Context context, int orientation, int color, int size) {
        mDivideSize = size;
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStrokeWidth(mDivideSize);
        this.mRectPadding = new Rect();
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {

        int left = parent.getPaddingLeft() + mRectPadding.left;
        int right = parent.getWidth() - parent.getPaddingRight() - mRectPadding.right;
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            if (parent.getAdapter() != null && parent.getAdapter() instanceof SRecycleMoreAdapter) {
                SRecycleMoreAdapter adapter = (SRecycleMoreAdapter) parent.getAdapter();
                if(adapter.getHeaderCount() != 0){
                    if(i < adapter.getHeaderCount()){
                        continue;
                    }
                }
            }
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int top = child.getBottom() + params.bottomMargin +
                    Math.round(ViewCompat.getTranslationY(child));
            int bottom = top;
            if (mDividerDrawable != null) {
                bottom += mDividerDrawable.getIntrinsicHeight();
                mDividerDrawable.setBounds(left, top, right, bottom);
                mDividerDrawable.draw(c);
            } else {
                bottom += mDivideSize / 2;
                c.drawLine(left, top, right, bottom, mPaint);
            }
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int left = child.getRight() + params.rightMargin;
            int right = left;
            if (mDividerDrawable != null) {
                right += mDividerDrawable.getIntrinsicWidth();
                mDividerDrawable.setBounds(left, top, right, bottom);
                mDividerDrawable.draw(c);
            } else {
                right += mDivideSize / 2;
                c.drawLine(left, top, right, bottom, mPaint);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getAdapter().getItemCount();
        int itemPosition = parent.getChildAdapterPosition(view);
        if (isLast(itemPosition, childCount)) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mDividerDrawable == null ? mDivideSize : mDividerDrawable.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, mDividerDrawable == null ? mDivideSize : mDividerDrawable.getIntrinsicWidth(), 0);
        }
    }

    private boolean isLast(int position, int childCount) {
        return position == childCount - 1;
    }

}

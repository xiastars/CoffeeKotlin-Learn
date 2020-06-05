package com.summer.helper.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.malata.summer.helper.R;
import com.summer.helper.utils.SUtils;

/**
 * author：ZXC
 * date: 2017/11/16 14:30
 * 改的比较冗余，得整整
 */

public class GridItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[] { android.R.attr.listDivider };
    private Drawable mDivider;
    private int mDividerWith, mDividerHeight;

    public GridItemDecoration(Context context)
    {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = new ColorDrawable(context.getResources().getColor(R.color.grey_e1));
        mDividerWith = mDivider.getIntrinsicWidth();
        mDividerHeight = mDivider.getIntrinsicHeight();
        a.recycle();
    }

    public GridItemDecoration(Context context, Drawable drawable)
    {
        mDivider = drawable;
        mDividerWith = mDivider.getIntrinsicWidth();
        mDividerHeight = mDivider.getIntrinsicHeight();
    }

    public GridItemDecoration(Context context, int color)
    {
        this(context, color, (int) SUtils.getDip(context, 1.0f));
    }

    public GridItemDecoration(Context context, int color, int size)
    {
        this(context, color, size, size);
    }

    public GridItemDecoration(Context context, int color, int width, int height){
        mDivider = new ColorDrawable(color);
        mDividerWith = width;
        mDividerHeight = height;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state)
    {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    private int getSpanCount(RecyclerView parent)
    {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager)
        {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager)
        {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    public void drawHorizontal(Canvas c, RecyclerView parent)
    {
        int childCount = parent.getChildCount();
        int spanCount = getSpanCount(parent);
        for (int i = 0; i < childCount; i++)
        {
          /*  if (isLastRaw(parent, i, spanCount, childCount))
                continue;*/
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin
                    + mDividerWith;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDividerHeight;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent)
    {
        final int childCount = parent.getChildCount();
        int spanCount = getSpanCount(parent);
        for (int i = 0; i < childCount; i++)
        {
        /*    if (isLastColum(parent, i, spanCount, childCount) || isLast(i, childCount))
                continue;*/
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDividerWith;

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private boolean isLast(int position, int childCount){
        return position == childCount-1;
    }

    private boolean isLastColum(RecyclerView parent, int pos, int spanCount,
                                int childCount)
    {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager)
        {
            if (((pos + 1)%spanCount==0))// 如果是最后一列，则不需要绘制右边
            {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager)
        {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL)
            {
                if (((pos + 1)%spanCount==0) || childCount<spanCount)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else
            {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,
                              int childCount)
    {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager)
        {
            int remain = childCount % spanCount;
            childCount = childCount - (remain==0 ? spanCount : remain);
            if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                return true;
        } else if (layoutManager instanceof StaggeredGridLayoutManager)
        {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL)
            {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else
            // StaggeredGridLayoutManager 且横向滚动
            {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0)
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        int itemPosition = parent.getChildAdapterPosition(view);
        int column=itemPosition%spanCount;
        int offestLeft = column * mDividerWith / spanCount;
        int offestRight = mDividerWith - (column + 1) * mDividerWith / spanCount;
        int offestBottom = mDividerHeight;
        long i = System.currentTimeMillis();
        if (isLastRaw(parent, itemPosition, spanCount, childCount))// 如果是最后一行，则不需要绘制底部
        {
            offestBottom = 0;
        }
        if (isLastColum(parent, itemPosition, spanCount, childCount) && !isLast(itemPosition, childCount))// 如果是最后一列和最后一个，则不需要绘制右边
        {
            offestRight = 0;
        }
        outRect.set(offestLeft, 0, offestRight, offestBottom);
    }
}

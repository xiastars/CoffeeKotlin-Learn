package com.summer.helper.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malata.summer.helper.R;
import com.summer.helper.utils.Logs;
import com.summer.helper.utils.SUtils;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * 当需要上拉加载时，底部添加加载完了
 */
public abstract class SRecycleMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected Context context;
    public List<?> items;
    protected int headerCount;
    protected int bottomCount = 1;
    boolean bottomVisible = false;
    protected boolean showEmptyView;
    protected LayoutInflater mInflater;

    int textMoreColor = R.color.grey_99;
    protected View headerView;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private String mEmptyViewHintContent;

    /**
     * 正在刷新中
     */
    protected boolean isAdapterRefresh;

    public SRecycleMoreAdapter(Context context) {
        this(context, null);
    }

    public SRecycleMoreAdapter(Context context, View headerView) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.headerView = headerView;
        items = new ArrayList<>();
        setHeaderCount(headerView == null ? 0 : 1);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null)
            return;
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup lookup = gridLayoutManager.getSpanSizeLookup();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isHeadView(position) || isFootView(position) || isOtherView(position))
                        return gridLayoutManager.getSpanCount();
                    return lookup == null ? 1 : lookup.getSpanSize(position);
                }
            });
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            //....
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ViewType.TYPE_BOTTOM) {
            View view = mInflater.inflate(R.layout.view_bottom_nomore, parent, false);
            return new BottomHolder(view);
        } else if (viewType == ViewType.TYPE_CONTENT) {
            return setContentView(parent);
        } else if (viewType == ViewType.TYPE_TOP) {
            if (headerView != null) {
                return new TopHolder(headerView);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (isHeadView(position)) {
            bindHeaderView(holder, position);
        } else if (isFootView(position)) {
            BottomHolder hv = (BottomHolder) holder;
            hv.tvNomore.setTextColor(getResourceColor(textMoreColor));
            RelativeLayout rlView = ((BottomHolder) holder).rlParent;
            if (rlView == null) {
                return;
            }

            ViewGroup.LayoutParams params = rlView.getLayoutParams();
            boolean showBottom = getRealItemCount() == 0 && showEmptyView;
            if (bottomVisible) {
                rlView.setVisibility(View.VISIBLE);
                showBottomView(params, showBottom, hv);
            } else {
                rlView.setVisibility(View.GONE);
                params.height = SUtils.getDip(context, 1);
                params.width = 1;
            }
        } else {
            if (mOnItemClickListener != null)
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null)
                            mOnItemClickListener.onItemClick(position);
                    }
                });
            if (mOnItemLongClickListener != null)
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOnItemLongClickListener != null)
                            mOnItemLongClickListener.onItemLongClick(position);
                        return false;
                    }
                });
            bindContentView(holder, position - headerCount);
        }
    }

    protected void showBottomView(ViewGroup.LayoutParams params, boolean showBottom, BottomHolder hv) {
        params.height = showBottom ? SUtils.getDip(context, 300) : SUtils.getDip(context, 70);
        params.width = SUtils.screenWidth;
        if (showBottom) {
            hv.llEmpty.setVisibility(View.VISIBLE);
            hv.rlNomore.setVisibility(View.GONE);
            if (mEmptyViewHintContent != null)
                hv.tvHintContent.setText(mEmptyViewHintContent);
            showReloadView(hv.tvReload);
        } else {
            hv.llEmpty.setVisibility(View.GONE);
            hv.rlNomore.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 留给子类调用
     *
     * @param tvReload
     */
    protected void showReloadView(TextView tvReload) {
    }

    public View createHolderView(int id, ViewGroup parent) {
        return mInflater.inflate(id, parent, false);
    }

    public boolean isHeadView(int position) {
        return position < headerCount;
    }

    public boolean isFootView(int position) {
        return position >= getItemCount() - bottomCount;
    }

    public boolean isOtherView(int position) {
        return false;
    }

    /**
     * 处理头部数据
     *
     * @param holder
     * @param position
     */
    protected void bindHeaderView(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemViewType(int position) {
        for (int i = 0; i < headerCount; i++) {
            if (position == i) {
                return ViewType.TYPE_TOP;
            }
        }
        if (position == getItemCount() - 1) {
            return ViewType.TYPE_BOTTOM;
        }
        return ViewType.TYPE_CONTENT;
    }

    public void notifyDataChanged(List<?> comments) {
        this.items = comments;
        if (comments != null && !comments.isEmpty()) {
            setBottomViewGONE();
        }
        notifyDataSetChanged();
    }

    /**
     * SRecycleView有自定义的空界面
     */
    public void showEmptyView() {
        this.items.clear();
        setBottomViewGONE();
        notifyDataSetChanged();
    }

    public void setEmptyViewHintContent(String content) {
        mEmptyViewHintContent = content;
    }

    /**
     * SRecycleView有自定义的空界面,当有headview时，headView高度可以超过父布局，改用这个
     */
    public void showSRecycleEmptyView() {
        this.items.clear();
        bottomVisible = true;
        notifyDataSetChanged();
    }

    /**
     * NRecycleVIew显示空页面
     */
    public void showNEmptyView() {
        items = new ArrayList<>();
        setBottomViewVisible();
        notifyDataSetChanged();
    }

    public void notifyDataChanged(List<?> comments, boolean hideBottom) {
        this.items = comments;
        showEmptyView = hideBottom;
        Logs.i("hideBottom:" + hideBottom);
        if (hideBottom && items != null && !items.isEmpty()) {
            setBottomViewGONE();
        } else {
            setBottomViewVisible();
        }
        if (items != null) {
            notifyDataSetChanged();
            notifyItemChanged(0);
        }
    }

    public void hideBottom() {
        setBottomViewGONE();
        notifyDataSetChanged();
    }

    public void notifyDataChanged(boolean hideBottom) {
        notifyDataChanged(items, hideBottom);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public int getRealItemCount() {
        return items != null ? items.size() : 0;
    }

    public int getItemCount() {
        int count = items != null ? items.size() + bottomCount + headerCount : bottomCount;
        return count;
    }

    public void setHeaderView(ViewGroup headerView) {
        this.headerView = headerView;
    }

    /**
     * 用来清楚一些保存的状态
     */
    public void clearTags() {
    }

    protected class BottomHolder extends RecyclerView.ViewHolder {

        public TextView tvNomore;
        public RelativeLayout rlParent;
        LinearLayout llEmpty;
        public TextView tvHintContent;
        public TextView tvReload;
        public RelativeLayout rlNomore;
        public View leftLine, rightLine;

        public BottomHolder(View itemView) {
            super(itemView);
            rlNomore = itemView.findViewById(R.id.rl_nomore);
            tvNomore = (TextView) itemView.findViewById(R.id.tv_nomore);
            rlParent = (RelativeLayout) itemView.findViewById(R.id.rl_parent);
            llEmpty = (LinearLayout) itemView.findViewById(R.id.ll_empty);
            tvHintContent = (TextView) itemView.findViewById(R.id.tv_hint_content);
            tvReload = itemView.findViewById(R.id.tv_reload);
            leftLine = itemView.findViewById(R.id.left_line);
            rightLine = itemView.findViewById(R.id.right_left);
        }
    }

    protected class TopHolder extends RecyclerView.ViewHolder {

        public TopHolder(View itemView) {
            super(itemView);
        }
    }

    public void setBottomViewVisible() {
        bottomVisible = true;
        if (isAdapterRefresh) {
            isAdapterRefresh = false;
            return;
        }
        isAdapterRefresh = true;
        try {
            notifyItemChanged(getItemCount() - 1);
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
        isAdapterRefresh = false;
    }

    public boolean isShowEmptyView() {
        return showEmptyView;
    }

    public void setShowEmptyView() {
        this.showEmptyView = true;
    }

    public boolean isBottomVisible() {
        return bottomVisible;
    }

    public void setBottomVisible(boolean bottomVisible) {
        this.bottomVisible = bottomVisible;
    }

    public void setBottomViewGONE() {
        bottomVisible = false;
    }

    public void setHeaderCount(int count) {
        this.headerCount = count;
    }

    public int getHeaderCount() {
        return this.headerCount;
    }

    /**
     * 返回对应颜色
     *
     * @param colorRes
     * @return
     */
    public int getResourceColor(int colorRes) {
        return context.getResources().getColor(colorRes);
    }

    /**
     * 为文本设置颜色
     *
     * @param colorRes
     * @return
     */
    protected void setHoderTextColor(TextView view, int colorRes) {
        view.setTextColor(getResourceColor(colorRes));
    }

    public class ViewType {

        /**
         * 主体内容
         */
        public static final int TYPE_CONTENT = 1;

        /**
         * 底部
         */
        public static final int TYPE_BOTTOM = 2;

        /**
         * 头部
         */
        public static final int TYPE_TOP = 0;

        /**
         * 插入类型
         */
        public static final int INSERT_TYPE1 = 3;

        /**
         * 插入类型
         */
        public static final int INSERT_TYPE2 = 4;

        /**
         * 插入类型
         */
        public static final int INSERT_TYPE3 = 5;

    }

    public int getTextMoreColor() {
        return textMoreColor;
    }

    public void setTextMoreColor(int textMoreColor) {
        this.textMoreColor = textMoreColor;
    }

    public abstract RecyclerView.ViewHolder setContentView(ViewGroup parent);

    public abstract void bindContentView(RecyclerView.ViewHolder holder, int position);

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
}

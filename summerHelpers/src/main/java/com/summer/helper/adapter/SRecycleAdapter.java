package com.summer.helper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * 基础Adapter
 */
public class SRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Context context;
    public List<?> items;
    boolean loadFinished;
    protected LayoutInflater mInflater;
    protected int bottomCount = 0;

    public SRecycleAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    protected View createHolderView(int id, ViewGroup parent) {
        return mInflater.inflate(id, parent, false);
    }

    public void showLoadFinish() {
        this.loadFinished = true;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void notifyDataChanged(List<?> comments) {
        this.items = comments;
        notifyDataSetChanged();
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

    public int getItemCount() {
        return items != null ? items.size() + (loadFinished ? 1 : 0) + bottomCount : bottomCount;
    }


}

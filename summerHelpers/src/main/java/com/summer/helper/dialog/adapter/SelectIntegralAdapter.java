package com.summer.helper.dialog.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.malata.summer.helper.R;
import com.summer.helper.adapter.SRecycleAdapter;
import com.summer.helper.listener.OnSimpleClickListener;

/**
 * Created by xiaqiliang on 2017/6/20.
 */

public class SelectIntegralAdapter extends SRecycleAdapter {
    int selectIndex;

    String[] datas;
    OnSimpleClickListener listener;

    public SelectIntegralAdapter(Context context, String[] datas, OnSimpleClickListener listener) {
        super(context);
        this.context = context;
        this.datas = datas;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_text, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder viewHodler = (ViewHolder) holder;
        String data = datas[position];
        viewHodler.tvTitle.setText(data);
        if (selectIndex == position) {
            viewHodler.tvTitle.setTextColor(getResourceColor(R.color.grey_33));
        } else {
            viewHodler.tvTitle.setTextColor(getResourceColor(R.color.grey_3f));
        }
        viewHodler.llParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectIndex = position;
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onClick(position);
                }
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return datas != null ? datas.length : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private LinearLayout llParent;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            llParent = (LinearLayout) itemView.findViewById(R.id.ll_parent);
        }
    }
}

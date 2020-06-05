/*
package com.summer.demo.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.summer.demo.R;
import com.summer.helper.recycle.MaterialRefreshLayout;
import com.summer.helper.recycle.MaterialRefreshListener;
import com.summer.helper.utils.SUtils;
import com.summer.helper.view.NRecycleView;

*/
/**
 * 横向的ListView示例，刷新状态正在优化中
 *
 * @author xiastars@vip.qq.com
 *//*

public class HorListFragment extends BaseSimpleFragment implements View.OnClickListener {
    NRecycleView refreshView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horlist, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        refreshView = (NRecycleView) view.findViewById(R.id.list);
        //支持上拉加载，默认为不支持
        refreshView.setAdapter(new SimpleImgAdapter(context));

        //下拉刷新与上拉加载回调
        refreshView.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                //一般是请求完数据后，结束刷新，这里没有请求，所以定时三秒后结束
                materialRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialRefreshLayout.finishRefresh();

                    }
                }, 3000);
            }

            @Override
            public void onfinish() {
                SUtils.makeToast(context, "加载完成");
            }

            @Override
            public void onRefreshLoadMore(final MaterialRefreshLayout materialRefreshLayout) {
                SUtils.makeToast(context, "加载下一页");
                materialRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialRefreshLayout.finishRefreshLoadMore();

                    }
                }, 3000);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    private static class SimpleImgAdapter
            extends RecyclerView.Adapter<SimpleImgAdapter.ViewHolder> {


        public static class ViewHolder extends RecyclerView.ViewHolder {

            public final ImageView mImageView;

            public ViewHolder(View view) {
                super(view);
                mImageView = (ImageView) view.findViewById(R.id.item_album);
            }
        }

        public SimpleImgAdapter(Context context) {
            super();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_icon, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if (position % 2 == 0) {
                holder.mImageView.setImageResource(R.drawable.xiehou01);
            } else {
                holder.mImageView.setImageResource(R.drawable.xiehou02);
            }
        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }

}*/

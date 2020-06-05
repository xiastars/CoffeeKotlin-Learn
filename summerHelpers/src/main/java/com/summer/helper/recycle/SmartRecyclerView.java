package com.summer.helper.recycle;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/17 11:14
 */
public class SmartRecyclerView extends SmartRefreshLayout {
    RecyclerView recyclerView;

    //风格列数
    int numGrid;

    public SmartRecyclerView(Context context) {
        super(context);
        addChild();
    }

    public SmartRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addChild();
    }

    private void addChild() {
        recyclerView = new RecyclerView(getContext());
        this.addView(recyclerView);
    }


    /**
     * 设置为ListView
     */
    public void setList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ((DefaultItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

    }

    public void setAdapter(@SuppressWarnings("rawtypes") RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    public RecyclerView.Adapter getAdapter(){
        return recyclerView.getAdapter();
    }

    /**
     * 设置为GridView
     *
     * @param num 列数
     */
    public void setGridView(int num) {
        numGrid = num;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), num));
        ((DefaultItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }
}

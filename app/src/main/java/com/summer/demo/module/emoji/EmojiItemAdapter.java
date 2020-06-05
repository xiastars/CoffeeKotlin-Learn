package com.summer.demo.module.emoji;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.summer.demo.R;
import com.summer.helper.adapter.SRecycleAdapter;
import com.summer.helper.listener.OnReturnObjectClickListener;
import com.summer.helper.utils.SUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xiaqiliang on 2017/9/7.
 */
public class EmojiItemAdapter extends SRecycleAdapter {
    private OnReturnObjectClickListener onEmojiSelectedListener;

    public EmojiItemAdapter(Context context, OnReturnObjectClickListener onEmojiSelectedListener) {
        super(context);
        this.onEmojiSelectedListener = onEmojiSelectedListener;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.emoji_item_layout, parent, false);
        return new TabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        TabViewHolder hd = (TabViewHolder) holder;
        IconEntity iconEntity = (IconEntity) items.get(position);
        String uri = iconEntity.getEmojiPath();
        if (!TextUtils.isEmpty(uri)) {
            SUtils.setPic(hd.emojiView, iconEntity.getEmojiPath());
        } else {
            if (iconEntity.getRes() != 0)
                SUtils.setPicResource(hd.emojiView, iconEntity.getRes());
        }
        bindEmojiClickListener(hd.rlEmojiLayout, iconEntity);
    }

    private void bindEmojiClickListener(final RelativeLayout view, final IconEntity iconEntity) {
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onEmojiSelectedListener != null) {
                    if(iconEntity.getRes() == 0){
                        return;
                    }
                    onEmojiSelectedListener.onClick(iconEntity);
                }
            }
        });
    }

    protected class TabViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.emojiView)
        ImageView emojiView;
        @BindView(R.id.rl_emoji_layout)
        RelativeLayout rlEmojiLayout;

        TabViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        /*    int width = (SUtils.screenWidth) / 4 - SUtils.getDip(context, 10);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
            params.setMargins(0, 25, 0, 0);
            holder.emojiView.setLayoutParams(params);*/
        }
    }
}

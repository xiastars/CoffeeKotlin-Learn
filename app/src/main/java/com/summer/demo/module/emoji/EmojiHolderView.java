package com.summer.demo.module.emoji;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.summer.demo.R;
import com.summer.demo.module.base.viewpager.Holder;
import com.summer.demo.module.emoji.source.Source;
import com.summer.helper.listener.OnReturnObjectClickListener;
import com.summer.helper.view.NRecycleView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmojiHolderView implements Holder<Source> {
    ViewHolder hd;
    OnReturnObjectClickListener listener;
    EmojiItemAdapter emojiItemAdapter;

    public EmojiHolderView(OnReturnObjectClickListener listener) {
        this.listener = listener;
    }

    @Override
    public View createView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.emoji_panel_item, null);
        hd = new ViewHolder(view);

        emojiItemAdapter = new EmojiItemAdapter(context, listener);
        hd.nvContainer.setAdapter(emojiItemAdapter);
        return view;
    }

    @Override
    public void UpdateUI(Context context, int position, Source data) {
        emojiItemAdapter.notifyDataChanged(data.getList());
    }

    static class ViewHolder {
        @BindView(R.id.nv_container)
        NRecycleView nvContainer;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            nvContainer.setGridView(7);
        }
    }
}

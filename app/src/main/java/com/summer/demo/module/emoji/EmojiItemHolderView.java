package com.summer.demo.module.emoji;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.summer.demo.R;
import com.summer.demo.module.base.viewpager.Holder;
import com.summer.helper.utils.SUtils;
import com.summer.helper.view.RoundAngleImageView;

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/11 10:19
 */
public class EmojiItemHolderView implements Holder<EmojiInfo> {
    private RoundAngleImageView imageView;
    private TextView tvTitle;
    private TextView tvSubTitle;

    @Override
    public View createView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_emoji_banner,null);
        //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
        imageView = (RoundAngleImageView) view.findViewById(R.id.iv_nav);
        imageView.setShowTouchAnim(false);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvSubTitle = (TextView) view.findViewById(R.id.tv_subtitle);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return view;
    }

    @Override
    public void UpdateUI(Context context, int position, EmojiInfo data) {
        SUtils.setPicWithHolder(imageView, data.getImg(), R.drawable.default_icon_linear);
        SUtils.setNotEmptText(tvTitle,data.getTitle());
        SUtils.setNotEmptText(tvSubTitle,data.getSubTitle());
    }
}

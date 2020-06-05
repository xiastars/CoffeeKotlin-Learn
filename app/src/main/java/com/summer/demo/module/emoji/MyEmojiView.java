package com.summer.demo.module.emoji;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.summer.demo.R;
import com.summer.demo.module.base.viewpager.CBViewHolderCreator;
import com.summer.demo.module.base.viewpager.ConvenientBanner;
import com.summer.demo.module.emoji.source.EmojiSource;
import com.summer.demo.module.emoji.source.Source;
import com.summer.helper.listener.OnReturnObjectClickListener;

import java.util.List;

/**
 * 表情主页面
 * Created by xiaqiliang on 2017/9/7.
 */
public class MyEmojiView {
    private ConvenientBanner viewPager;
    private EmojiSource emojiSource;
    private MyEmojiService emojiService;
    private Context context;
    private Activity activity;
    private View emojiLayout;
    private int type;//如果type==0，为非聊天页面，隐藏下面菜单与其它贴图
    private OnReturnObjectClickListener listener;
    private int clickIndex = 0;
    private RelativeLayout layout;
    CBViewHolderCreator holderCreator;

    public MyEmojiView(Context context, int type, OnReturnObjectClickListener listener) {
        this.context = context;
        this.activity = (Activity) context;
        this.listener = listener;
        this.type = type;
        init();
    }

    private void init() {
        emojiLayout = activity.findViewById(R.id.emoji_layout);
        if(emojiLayout == null){
            return;
        }
        emojiService = MyEmojiService.getInstance(context.getApplicationContext());
        emojiSource = EmojiSource.getInstance(context.getApplicationContext());
        findViewById();
        setIconAdapter(emojiSource.getSourceList());
    }

    private void findViewById() {

        viewPager = (ConvenientBanner) emojiLayout.findViewById(R.id.viewFlow);
        viewPager.setCanLoop(true);
        holderCreator = new CBViewHolderCreator<EmojiItemHolderView>() {
            @Override
            public EmojiItemHolderView createHolder() {
                return new EmojiItemHolderView();
            }
        };

    }

    /**
     * 设置表情界面 是否可见
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        if (emojiLayout == null) return;
        if (visible) {
            emojiLayout.setVisibility(View.VISIBLE);
        } else {
            emojiLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 获取表情界面可见不可见
     *
     * @return
     */
    public boolean getVisible() {
        if (emojiLayout == null) return false;
        return emojiLayout.getVisibility() == View.VISIBLE;
    }

    /**
     * @param sources
     */
    private void setIconAdapter(List<Source> sources) {
        CBViewHolderCreator creator = new CBViewHolderCreator<EmojiHolderView>() {
            @Override
            public EmojiHolderView createHolder() {
                return new EmojiHolderView(new OnReturnObjectClickListener() {
                    @Override
                    public void onClick(Object object) {
                        IconEntity iconEntity = (IconEntity) object;
                        String s = emojiService.replaceEmoji(iconEntity.getName(),14).toString();
                        iconEntity.setEmojiText(s);
                        listener.onClick(iconEntity);
                    }
                });
            }
        };
        viewPager.setPages(creator, sources).setPageIndicator(new int[]{R.drawable.so_greycc_oval, R.drawable.so_grey33_oval});
    }

}

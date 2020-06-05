package com.summer.demo.module.emoji.source;

import android.content.Context;

import com.summer.demo.module.emoji.IconEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Source {

    private int menuResId;
    private String emojiKey;
    private List<IconEntity> list;
    private boolean isSelected = false;

    public Source() {
        list = new ArrayList<IconEntity>();
        setMenuRes();
        setEmojiKey();
    }

    public List<IconEntity> getList() {
        return list;
    }

    /**
     * 设置组表情的向导icon
     *
     * @param menuResId
     */
    public void setMenuResId(int menuResId) {
        this.menuResId = menuResId;
    }

    /**
     * 设置组表情的向导icon
     *
     */
    public void setEmojiKey(String emojiKey) {
        this.emojiKey = emojiKey;
    }

    public abstract void initIcon(Context context, Map<String, Integer> map);

    public abstract void setMenuRes();

    public abstract void setEmojiKey();

    public int getMenuResId() {
        return menuResId;
    }

    public String getEmojiKey() {
        return emojiKey;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}

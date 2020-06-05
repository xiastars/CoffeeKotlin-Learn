package com.summer.demo.module.emoji.source;

import android.content.Context;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaqiliang on 2017/9/7.
 */

public class EmojiSource {

    private Context context;
    private static volatile EmojiSource instance = null;
    private Map<String, Integer> emojiMap;
    private List<Source> sourceList;

    private EmojiSource(Context context) {
        this.context = context;
        addSource();
        initMapData();
    }

    private void addSource() {
        sourceList = new ArrayList<>();
        sourceList.add(new CommonEmoji());
    }

    public void initMapData() {
        emojiMap = new LinkedHashMap<>();
        for (Source source : sourceList) {
            source.initIcon(context, emojiMap);
        }
    }

    /**
     * 获取整个表情库
     *
     * @return
     */
    public List<Source> getSourceList() {
        return sourceList;
    }

    public Map<String, Integer> getEmojiMap() {
        return emojiMap;
    }


    public static EmojiSource getInstance(Context context) {
        if (instance == null) {
            synchronized (EmojiSource.class) {
                if (instance == null) {
                    instance = new EmojiSource(context);
                }
            }
        }
        return instance;
    }

}

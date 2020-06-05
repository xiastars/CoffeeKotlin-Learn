package com.summer.demo.module.emoji;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;

import com.summer.demo.R;
import com.summer.demo.module.emoji.source.EmojiSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表情主页面
 * Created by xiaqiliang on 2017/9/7.
 */
public class MyEmojiService {
    public final static String TAG = "EmojiService";
    private final String PREFIX_FILE_NAME = "input_emoji_";
    private final String SUFFIX_FILE_NAME = "";
    private final String PREFIX_KEY = "[";
    private final String SUFFIX_KEY = "]";
    private final String REGULAR_RULE = "\\[[^\\]]+\\]";
    private int EMOJI_PAGE_SHOW_NUM = 21;
    private static volatile MyEmojiService instance = null;
    private Context context;

    /**
     * 将文本中的表情字符转换为表情
     *
     * @param contentStr
     * @return
     */
    public SpannableString replaceEmoji(String contentStr, int textSize) {
        SpannableString spannableString = new SpannableString(contentStr);
        Pattern pattern = Pattern.compile(REGULAR_RULE, Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(spannableString);
        //find会在整个输入中寻找是否有匹配的子字符串
        while (matcher.find()) {
            String key = matcher.group();//返回由以前匹配操作所匹配的输入子序列。
            int resId = 0;
            try {
                resId = EmojiSource.getInstance(context).getEmojiMap().get(key);
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                //设置表情显示的大小
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) (textSize * 1.3), (int) (textSize * 1.3), true);
                ImageSpan imageSpan = new ImageSpan(context, bitmap);
                spannableString.setSpan(imageSpan, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (NullPointerException e) {
                e.printStackTrace();
                continue;
            }
        }
        return spannableString;
    }

    /**
     * 将表情字符转换为Html图片标签格式
     *
     * @param contentStr
     * @return
     */
    public String parseEmoji(String contentStr) {
        if (TextUtils.isEmpty(contentStr)) return "";
        String s1 = contentStr.replaceAll("\\[([^\\]]+)+\\]", "<img src='$1'/>");
        return s1;
    }

    public List<IconEntity> getIconList(int index, int page, List<IconEntity> list) {
        List<IconEntity> resultList = new ArrayList<IconEntity>();
        //如果是帖图，一页只显示8个
        if (index != 0) {
            EMOJI_PAGE_SHOW_NUM = 8;
        } else {
            EMOJI_PAGE_SHOW_NUM = 21;
        }
        int start = page * EMOJI_PAGE_SHOW_NUM;
        int end = (page + 1) * EMOJI_PAGE_SHOW_NUM;
        if (end > list.size()) {
            end = list.size();
        }
        for (int i = start; i < end; i++) {
            resultList.add(list.get(i));
        }
        return resultList;
    }

    public int getPageCount(int count) {
        return count / EMOJI_PAGE_SHOW_NUM + 1;
    }

    /**
     * 将所有的表情添加到map
     *
     * @param context
     * @return
     */

    public IconEntity addEmojiToMap(Context context, String text, String name, Map<String, Integer> map) {
        String key = PREFIX_KEY + name + SUFFIX_KEY;
        int resId = R.drawable.input_icon_deleting;
        if (!TextUtils.isEmpty(name)) {
            resId = context.getResources().getIdentifier(PREFIX_FILE_NAME + text + SUFFIX_FILE_NAME, "drawable", context.getPackageName());
        } else {
            resId = R.drawable.input_icon_deleting;
        }
        map.put(key, resId);
        return new IconEntity(key, resId);
    }

    /**
     * 将所有的表情添加到map
     *
     * @param context
     * @return
     */

    public IconEntity addEmojiToMapByAssets(Context context, int id, String emojiKey, String text, String name, String version, int packageID) {
        String tag = "android_asset/emoji/";
        return new IconEntity(id, name, tag + emojiKey + "/" + text, version, packageID);
    }

    private MyEmojiService(Context context) {
        this.context = context;
    }

    public static MyEmojiService getInstance(Context context) {
        if (instance == null) {
            synchronized (MyEmojiService.class) {
                if (instance == null) {
                    instance = new MyEmojiService(context);
                }
            }
        }
        return instance;
    }

}

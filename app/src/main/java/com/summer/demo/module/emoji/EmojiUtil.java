package com.summer.demo.module.emoji;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Html.ImageGetter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.summer.demo.R;
import com.summer.demo.module.emoji.source.EmojiSource;

public class EmojiUtil {

    /**
     * 设置表情包显示
     *
     * @param tvContent
     * @param content
     */
    public static void setEmojiText(TextView tvContent, String content) {
        tvContent.setText(getEmojiText(tvContent.getContext(), content, (int) tvContent.getTextSize()));
    }

    /**
     * 设置评论回复显示
     *
     * @param tvContent
     * @param content   文本
     * @param name      回复的名称
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void setCommentReplayText(TextView tvContent, String content, String name) {
        Context context = tvContent.getContext();
        tvContent.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_START);
        SpannableString spannableString = new SpannableString(getEmojiText(context, "回复 @" + name + " " + content, (int) tvContent.getTextSize()));
        spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.blue_2e)), 3, 4 + name.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvContent.setText(spannableString);
    }

    /**
     * 设置评论回复显示
     *
     * @param tvContent
     * @param content   文本
     * @param name      回复的名称
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void setMyCommentReplyText(TextView tvContent, String content, String name) {
        Context context = tvContent.getContext();
        tvContent.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_START);
        SpannableString spannableString = new SpannableString(getEmojiText(context, "回复@" + name + ":" + content, (int) tvContent.getTextSize()));
        spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red_d4)), 2, 3 + name.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvContent.setText(spannableString);
    }

    public static Spanned getEmojiText(Context context, String content, int textSize) {
        return  MyEmojiService.getInstance(context).replaceEmoji(content,textSize);
    }

    /**
     * 根据文件名通过反射得到表情id
     *
     * @param name
     * @return
     */
    public static int getDrawableResourcesId(Context context, String name) {
        try {
            int id = EmojiSource.getInstance(context).getEmojiMap().get("[" + name + "]");
            return id;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获得表情ImageGetter
     *
     * @param context
     * @return
     */
    public static ImageGetter getImagetGetter(final Context context) {
        ImageGetter getter = new ImageGetter() {

            @Override
            public Drawable getDrawable(String source) {
                try {
                    Drawable drawable = context.getResources().getDrawable(
                            EmojiUtil.getDrawableResourcesId(context, source));
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 2,
                            drawable.getIntrinsicHeight() / 2);
                    return drawable;
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        return getter;
    }
}

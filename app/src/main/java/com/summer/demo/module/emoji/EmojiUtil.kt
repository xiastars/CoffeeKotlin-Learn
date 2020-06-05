package com.summer.demo.module.emoji

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.support.annotation.RequiresApi
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import com.summer.demo.R
import com.summer.demo.module.emoji.source.EmojiSource

object EmojiUtil {
    /**
     * 设置表情包显示
     *
     * @param tvContent
     * @param content
     */
    fun setEmojiText(tvContent: TextView, content: String?) {
        tvContent.text = getEmojiText(tvContent.context, content, tvContent.textSize.toInt())
    }

    /**
     * 设置评论回复显示
     *
     * @param tvContent
     * @param content   文本
     * @param name      回复的名称
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun setCommentReplayText(tvContent: TextView, content: String, name: String) {
        val context = tvContent.context
        tvContent.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
        val spannableString = SpannableString(getEmojiText(context, "回复 @$name $content", tvContent.textSize.toInt()))
        spannableString.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.blue_2e)), 3, 4 + name.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvContent.text = spannableString
    }

    /**
     * 设置评论回复显示
     *
     * @param tvContent
     * @param content   文本
     * @param name      回复的名称
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun setMyCommentReplyText(tvContent: TextView, content: String, name: String) {
        val context = tvContent.context
        tvContent.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
        val spannableString = SpannableString(getEmojiText(context, "回复@$name:$content", tvContent.textSize.toInt()))
        spannableString.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.red_d4)), 2, 3 + name.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvContent.text = spannableString
    }

    fun getEmojiText(context: Context, content: String?, textSize: Int): Spanned {
        return MyEmojiService.Companion.getInstance(context)!!.replaceEmoji(content, textSize)
    }

    /**
     * 根据文件名通过反射得到表情id
     *
     * @param name
     * @return
     */
    fun getDrawableResourcesId(context: Context?, name: String): Int {
        try {
            return EmojiSource.getInstance(context).emojiMap["[$name]"]!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 获得表情ImageGetter
     *
     * @param context
     * @return
     */
    fun getImagetGetter(context: Context): Html.ImageGetter {
        return Html.ImageGetter { source ->
            try {
                val drawable = context.resources.getDrawable(
                        getDrawableResourcesId(context, source))
                drawable.setBounds(0, 0, drawable.intrinsicWidth / 2,
                        drawable.intrinsicHeight / 2)
                return@ImageGetter drawable
            } catch (e: Resources.NotFoundException) {
                e.printStackTrace()
            }
            null
        }
    }
}
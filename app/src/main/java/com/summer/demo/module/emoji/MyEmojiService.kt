package com.summer.demo.module.emoji

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ImageSpan
import com.summer.demo.R
import com.summer.demo.module.emoji.source.EmojiSource
import java.util.*
import java.util.regex.Pattern

/**
 * 表情主页面
 * Created by xiaqiliang on 2017/9/7.
 */
class MyEmojiService private constructor(private val context: Context) {
    private val PREFIX_FILE_NAME = "input_emoji_"
    private val SUFFIX_FILE_NAME = ""
    private val PREFIX_KEY = "["
    private val SUFFIX_KEY = "]"
    private val REGULAR_RULE = "\\[[^\\]]+\\]"
    private var EMOJI_PAGE_SHOW_NUM = 21
    /**
     * 将文本中的表情字符转换为表情
     *
     * @param contentStr
     * @return
     */
    fun replaceEmoji(contentStr: String?, textSize: Int): SpannableString {
        val spannableString = SpannableString(contentStr)
        val pattern = Pattern.compile(REGULAR_RULE, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(spannableString)
        //find会在整个输入中寻找是否有匹配的子字符串
        while (matcher.find()) {
            val key = matcher.group() //返回由以前匹配操作所匹配的输入子序列。
            var resId = 0
            try {
                resId = EmojiSource.getInstance(context).emojiMap[key]!!
                var bitmap = BitmapFactory.decodeResource(context.resources, resId)
                //设置表情显示的大小
                bitmap = Bitmap.createScaledBitmap(bitmap, (textSize * 1.3).toInt(), (textSize * 1.3).toInt(), true)
                val imageSpan = ImageSpan(context, bitmap)
                spannableString.setSpan(imageSpan, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } catch (e: NullPointerException) {
                e.printStackTrace()
                continue
            }
        }
        return spannableString
    }

    /**
     * 将表情字符转换为Html图片标签格式
     *
     * @param contentStr
     * @return
     */
    fun parseEmoji(contentStr: String): String {
        return if (TextUtils.isEmpty(contentStr)) "" else contentStr.replace("\\[([^\\]]+)+\\]".toRegex(), "<img src='$1'/>")
    }

    fun getIconList(index: Int, page: Int, list: List<IconEntity>): List<IconEntity> {
        val resultList: MutableList<IconEntity> = ArrayList()
        //如果是帖图，一页只显示8个
        EMOJI_PAGE_SHOW_NUM = if (index != 0) {
            8
        } else {
            21
        }
        val start = page * EMOJI_PAGE_SHOW_NUM
        var end = (page + 1) * EMOJI_PAGE_SHOW_NUM
        if (end > list.size) {
            end = list.size
        }
        for (i in start until end) {
            resultList.add(list[i])
        }
        return resultList
    }

    fun getPageCount(count: Int): Int {
        return count / EMOJI_PAGE_SHOW_NUM + 1
    }

    /**
     * 将所有的表情添加到map
     *
     * @param context
     * @return
     */
    fun addEmojiToMap(context: Context, text: String, name: String, map: MutableMap<String?, Int?>): IconEntity {
        val key = PREFIX_KEY + name + SUFFIX_KEY
        var resId = R.drawable.input_icon_deleting
        resId = if (!TextUtils.isEmpty(name)) {
            context.resources.getIdentifier(PREFIX_FILE_NAME + text + SUFFIX_FILE_NAME, "drawable", context.packageName)
        } else {
            R.drawable.input_icon_deleting
        }
        map[key] = resId
        return IconEntity(key, resId)
    }

    /**
     * 将所有的表情添加到map
     *
     * @param context
     * @return
     */
    fun addEmojiToMapByAssets(context: Context?, id: Int, emojiKey: String, text: String, name: String?, version: String?, packageID: Int): IconEntity {
        val tag = "android_asset/emoji/"
        return IconEntity(id, name, "$tag$emojiKey/$text", version, packageID)
    }

    companion object {
        const val TAG = "EmojiService"
        @Volatile
        private var instance: MyEmojiService? = null

        @JvmStatic
        fun getInstance(context: Context): MyEmojiService? {
            if (instance == null) {
                synchronized(MyEmojiService::class.java) {
                    if (instance == null) {
                        instance = MyEmojiService(context)
                    }
                }
            }
            return instance
        }
    }

}
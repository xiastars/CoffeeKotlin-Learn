package com.summer.demo.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.*
import android.text.style.DynamicDrawableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import com.summer.demo.R
import com.summer.demo.bean.SpannableInfo
import com.summer.demo.bean.SubjectInfo
import com.summer.demo.ui.ViewBigPhotoActivity
import com.summer.helper.utils.JumpTo
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SUtils
import com.summer.helper.web.WebContainerActivity
import java.io.File
import java.io.IOException
import java.util.regex.Pattern

object TextWebUtils {

    private val UPPER_LEFT_X = 0
    private val UPPER_LEFT_Y = 0

    /**
     * 设置支持自定义标签与链接的Text
     *
     * @param contentStr
     * @param showName
     */
    fun setHtmlText(showName: TextView, contentStr: String) {
        val nameContent = replaceEmoji(showName.context, contentStr, 35)
        showName.text = nameContent.builder
    }

    @JvmOverloads
    fun append(spannableString: SpannableStringBuilder, content: String?, color: Int, isBold: Boolean = false): SpannableStringBuilder {
        var content = content
        if (content == null) {
            content = ""
        }
        if (color == 0) {
            spannableString.append(content)
        } else {
            val msp = SpannableString(content)
            msp.setSpan(ForegroundColorSpan(color), 0, content.length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            if (isBold) {
                msp.setSpan(StyleSpan(Typeface.BOLD), 0, content.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            }
            spannableString.append(msp)
        }
        return spannableString
    }

    @JvmOverloads
    fun append(content: String, color: Int = 0): SpannableStringBuilder {
        val spannableString = SpannableStringBuilder()
        if (color == 0) {
            spannableString.append(content)
        } else {
            val msp = SpannableString(content)
            val length = spannableString.toString().length
            msp.setSpan(ForegroundColorSpan(color), length, length + content.length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            spannableString.append(msp)
        }
        return spannableString
    }


    /**
     * 将文本中的自定义标签与链接转换为特殊的标识
     *
     * @param contentStr
     * @return
     */
    fun replaceEmoji(context: Context, contentStr: String, textSize: Int): SpannableInfo {
        return replaceEmoji(context, null, contentStr, textSize, false)
    }

    /**
     * 将文本中的自定义标签与链接转换为特殊的标识
     *
     * @param contentStr
     * @return
     */
    @JvmOverloads
    fun replaceEmoji(context: Context, spannableString: SpannableStringBuilder?, contentStr: String, textSize: Int, onlyImg: Boolean = false): SpannableInfo {
        var spannableString = spannableString
        var contentStr = contentStr
        if (spannableString == null) {
            spannableString = SpannableStringBuilder()
        }
        val spannableInfo = SpannableInfo()
        if (!TextUtils.isEmpty(contentStr)) {
            contentStr = trim(contentStr)
            //如果包含多条链接和话题就分割了做，暂时没有想到好法子
            if (contentStr.contains(">")) {
                var datas = contentStr.split(">".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                var index = 0
                for (data in datas) {
                    index++
                    var d = data
                    if (index < datas.size) {
                        d += ">"
                    }
                    val builder = replaceSingle(context, spannableInfo, d, textSize, onlyImg)
                    spannableString.append(builder)
                }
            } else {
                val builder = replaceSingle(context, spannableInfo, contentStr, textSize, onlyImg)
                spannableString.append(builder)
            }
        }
        spannableInfo.builder = spannableString
        return spannableInfo
    }


    /**
     * 将文本中的Tag转换
     *
     * @return
     */
    fun replaceTagsOnly(spannableString: SpannableStringBuilder?): SpannableStringBuilder {
        var spannableString = spannableString
        if (spannableString == null) {
            spannableString = SpannableStringBuilder()
        }
        val regex_http = "<e type=\"hashtag\".*>"
        val pattern = Pattern.compile(regex_http, Pattern.CASE_INSENSITIVE)

        val matcher = pattern.matcher(spannableString)
        //find会在整个输入中寻找是否有匹配的子字符串
        while (matcher.find()) {
            try {
                val content = spannableString.subSequence(matcher.start(), matcher.end()).toString()
                val url = getUrl(content)
                val title = getWebTitle(content)
                val hid = match(content, "e", "hid")
                val finalContent = "#$title#"
                Logs.i("title:$title")
                val imageSpan = SpannableString(finalContent)
                if (!TextUtils.isEmpty(hid)) {
                    imageSpan.setSpan(ForegroundColorSpan(Color.parseColor("#5620F0")), 0, finalContent.length,
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                    imageSpan.setSpan(StyleSpan(Typeface.BOLD), 0, finalContent.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                }

                spannableString.replace(matcher.start(), matcher.end(), imageSpan)
            } catch (e: NullPointerException) {
                e.printStackTrace()
                continue
            }

        }
        return spannableString
    }

    fun replaceSingle(context: Context, spannableInfo: SpannableInfo, contentData: String, textSize: Int, onlyImg: Boolean): SpannableStringBuilder {
        var textSize = textSize
        val regex_http = "<e type=\"web\".*>"
        val pattern = Pattern.compile(regex_http, Pattern.CASE_INSENSITIVE)
        val spannableString = SpannableStringBuilder()
        spannableString.append(contentData)
        val matcher = pattern.matcher(spannableString)
        //find会在整个输入中寻找是否有匹配的子字符串
        while (matcher.find()) {
            try {
                val content = spannableString.subSequence(matcher.start(), matcher.end()).toString()
                val url = getUrl(content)
                val title = getWebTitle(content)
                var showOnlyImg = false
                if (!TextUtils.isEmpty(title) && title == "「查看图片」" && onlyImg) {
                    showOnlyImg = true
                }
                var bitmap = BitmapFactory.decodeResource(context.resources, if (showOnlyImg) R.drawable.trans else R.drawable.link_icon)
                //设置表情显示的大小
                if (showOnlyImg) {
                    textSize = 5
                }
                bitmap = Bitmap.createScaledBitmap(bitmap, textSize, textSize, true)
                val imageSpan = ImageSpan(context, bitmap, DynamicDrawableSpan.ALIGN_BASELINE)
                spannableString.setSpan(imageSpan, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                val string = SpannableString(if (TextUtils.isEmpty(title)) "  网页链接" else title)
                if (matcher.start() >= 0) {
                    string.setSpan(StyleSpan(Typeface.BOLD), 0, string.toString().length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                }
                spannableString.insert(matcher.end(), string)
                Logs.i("$title-----------------")
                val clickableSpan = object : NfClickableSpan(context.resources.getColor(if (showOnlyImg) R.color.blue_56 else R.color.blue_56)) {
                    override fun onClick(widget: View) {
                        if (!TextUtils.isEmpty(title) && title == "「查看图片」") {
                            JumpTo.getInstance().commonJump(context, ViewBigPhotoActivity::class.java, url)
                        } else {
                            try {
                                if (isSymlink(File(url))) {
                                    SUtils.makeToast(context, "url$url")
                                } else {
                                    val intent = Intent(context, WebContainerActivity::class.java)
                                    intent.putExtra(JumpTo.TYPE_STRING, url)
                                    intent.putExtra("key_title", title)
                                    context.startActivity(intent)
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }


                        }

                    }
                }
                val end = matcher.end() + string.toString().length
                spannableString.setSpan(clickableSpan, matcher.end() - 1, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            } catch (e: NullPointerException) {
                e.printStackTrace()
                continue
            }

        }
        return replaceTags(context, spannableInfo, spannableString)
    }

    @Throws(IOException::class)
    fun isSymlink(file: File?): Boolean {
        if (file == null)
            throw NullPointerException("File must not be null")
        val canon: File
        if (file.parent == null) {
            canon = file
        } else {
            val canonDir = file.parentFile.canonicalFile
            canon = File(canonDir, file.name)
        }
        return canon.canonicalFile != canon.absoluteFile
    }

    /**
     * 将文本中的Tag转换
     *
     * @return
     */
    fun replaceTags(context: Context, spannableInfo: SpannableInfo?, spannableString: SpannableStringBuilder?): SpannableStringBuilder {
        var spannableString = spannableString
        if (spannableString == null) {
            spannableString = SpannableStringBuilder()
        }
        val regex_http = "<e type=\"hashtag\".*>"
        val pattern = Pattern.compile(regex_http, Pattern.CASE_INSENSITIVE)

        val matcher = pattern.matcher(spannableString)
        //find会在整个输入中寻找是否有匹配的子字符串
        while (matcher.find()) {
            try {
                val content = spannableString.subSequence(matcher.start(), matcher.end()).toString()
                val url = getUrl(content)
                val title = getWebTitle(content)
                val hid = match(content, "e", "hid")
                val finalContent = "#$title#"
                val imageSpan = SpannableString(finalContent)
                if (spannableInfo != null && spannableInfo.subjectInfo == null) {
                    val subjectInfo = SubjectInfo()
                    subjectInfo.id = hid
                    subjectInfo.title = title
                    spannableInfo.subjectInfo = subjectInfo
                }
                if (!TextUtils.isEmpty(hid)) {
                    imageSpan.setSpan(ForegroundColorSpan(Color.parseColor("#5620F0")), 0, finalContent.length,
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                    imageSpan.setSpan(StyleSpan(Typeface.BOLD), 0, finalContent.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                }

                spannableString.replace(matcher.start(), matcher.end(), imageSpan)
                val clickableSpan = object : NfClickableSpan(Color.parseColor("#5620F0")) {
                    override fun onClick(widget: View) {
                        Logs.i("=-=-")
                        SUtils.makeToast(context, "点击了标签")

                    }
                }
                spannableString.setSpan(clickableSpan, matcher.start(), matcher.start() + finalContent.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } catch (e: NullPointerException) {
                e.printStackTrace()
                continue
            }

        }
        return spannableString
    }

    /**
     * 生成一个Span
     *
     * @param spannableString
     * @param unSpanText
     */
    fun generateOneSpan(context: Context, spannableString: SpannableStringBuilder, unSpanText: String, textSize: Float, textColor: Int): SpannableStringBuilder {
        var unSpanText = unSpanText
        unSpanText = "$unSpanText "
        if (unSpanText.contains(">")) {
            val datas = unSpanText.split(">".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var index = 0
            for (data in datas) {
                index++
                var d = data
                if (index < datas.size) {
                    d += ">"
                }
                val builder = changeTagToImg(context, SpannableStringBuilder(d), textSize, textColor)
                spannableString.append(builder)
            }
        } else {
            val builder = changeTagToImg(context, SpannableStringBuilder(unSpanText), textSize, textColor)
            spannableString.append(builder)
        }
        return spannableString
    }

    /**
     * 将文本中的Tag转换
     *
     * @return
     */
    fun changeTagToImg(context: Context, spannableString: SpannableStringBuilder?, textSize: Float, textColor: Int): SpannableStringBuilder {
        var spannableString = spannableString
        if (spannableString == null) {
            spannableString = SpannableStringBuilder()
        }
        val regex_http = "<e type=\"hashtag\".*>"
        val pattern = Pattern.compile(regex_http, Pattern.CASE_INSENSITIVE)

        val matcher = pattern.matcher(spannableString)
        //find会在整个输入中寻找是否有匹配的子字符串
        Logs.i("spa:$spannableString")
        while (matcher.find()) {
            try {
                val content = spannableString.subSequence(matcher.start(), matcher.end()).toString()
                val title = getWebTitle(content)
                val hid = match(content, "e", "hid")
                val finalContent = "#$title#"
                Logs.i("finalContent:$finalContent")
                val spanView = getSpanView(context, finalContent, SUtils.screenWidth, textSize, textColor)

                val bitmpaDrawable = convertViewToDrawable(spanView) as BitmapDrawable
                bitmpaDrawable.setBounds(0, 0, bitmpaDrawable.intrinsicWidth, bitmpaDrawable.intrinsicHeight)
                val imageSpan = ImageSpan(bitmpaDrawable)
                val start = matcher.start()
                val end = matcher.end()

                Logs.i("spa::" + spannableString.toString().length + ",,," + start + ",,," + end)
                spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } catch (e: NullPointerException) {
                e.printStackTrace()
                continue
            }

        }
        return spannableString
    }


    /**
     * 生成一个Span
     *
     * @param spannableString
     * @param unSpanText
     */
    fun generateAllSpan(context: Context, spannableString: SpannableStringBuilder, unSpanText: String, textSize: Float, textColor: Int): SpannableStringBuilder {
        var spannableString = spannableString
        val title = getWebTitle(unSpanText)
        val spanView = getSpanView(context, "#$title#", SUtils.screenWidth, textSize, textColor)

        val bitmpaDrawable = convertViewToDrawable(spanView) as BitmapDrawable
        bitmpaDrawable.setBounds(0, 0, bitmpaDrawable.intrinsicWidth, bitmpaDrawable.intrinsicHeight)
        val imageSpan = ImageSpan(bitmpaDrawable)
        val start = spannableString.toString().length
        val end = start + unSpanText.length
        spannableString = spannableString.append(unSpanText)

        spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    fun convertViewToDrawable(view: View): Drawable {
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(spec, spec)
        Logs.i("width:::" + view.measuredWidth)
        view.layout(UPPER_LEFT_X, UPPER_LEFT_Y, view.measuredWidth, view.measuredHeight)

        val b = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        Logs.i("width:::" + view.measuredWidth)
        val c = Canvas(b)
        //c.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(c)

        view.isDrawingCacheEnabled = true
        val cacheBmp = view.drawingCache
        val viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true)
        cacheBmp.recycle()
        view.destroyDrawingCache()
        return BitmapDrawable(viewBmp)
    }

    /**
     * 获得span视图
     *
     * @param context
     * @return
     */
    fun getSpanView(context: Context, text: String, maxWidth: Int, textSize: Float, textColor: Int): View {
        val view = TextView(context)
        val paint = Paint()

        paint.textSize = textSize
        view.maxWidth = maxWidth * 2
        view.text = text
        view.setSingleLine(true)
        view.setTextColor(textColor)
        view.textSize = textSize
        return view
    }

    fun getTextWidth(paint: Paint, str: String?): Int {
        var w = 0
        if (str != null && str.length > 0) {
            val len = str.length
            val widths = FloatArray(len)
            paint.getTextWidths(str, widths)
            for (j in 0 until len) {
                w += Math.ceil(widths[j].toDouble()).toInt()
            }
        }
        return w
    }


    private fun getUrl(`var`: String): String {
        /**
         * 获取指定HTML标签的指定属性的值
         * @param source 要匹配的源文本
         * @param element 标签名称
         * @param attr 标签的属性名称
         * @return 属性值列表
         */

        return match(`var`, "e", "href")
    }

    /**
     * 清除链接里的换行符
     *
     * @param source
     * @return
     */
    fun trim(source: String): String {
        var source = source
        val reg = "<.*?>"
        source = source.replace("\n".toRegex(), "&#&")
        val m = Pattern.compile(reg).matcher(source)
        while (m.find()) {
            val r = m.group()
            val rr = r.replace("&#&".toRegex(), "")
            val builder = StringBuilder(source)
            builder.replace(m.start(), m.end(), rr)
            source = builder.toString()
            source = source.replace("&#&".toRegex(), "\n")
            return "$source "
        }
        return source.replace("&#&".toRegex(), "\n") + " "
    }

    fun getWebTitle(source: String): String {

        val reg = "title=\"(.*?)\""
        val m = Pattern.compile(reg).matcher(source)
        while (m.find()) {
            val r = m.group(1)
            Logs.i("source:" + r + ".." + m.group())
            return r.replace("&#&".toRegex(), " ")
        }
        return ""
    }

    fun match(source: String, element: String, attr: String): String {
        //source = source.trim();

        Logs.i("source:$source")
        val reg = "<$element[^<>]*?\\s$attr=['\"]?\\s?(.*?)\\s?['\"]?(\\s.*?)?>"
        val m = Pattern.compile(reg).matcher(source)
        while (m.find()) {
            val r = m.group(1)
            Logs.i("source:" + r + ".." + m.group())
            return r.replace("&#&".toRegex(), " ")
        }
        return ""
    }

    fun returnNewUrl(source: String): String {

        val URL_REGEX = "(((http|ftp|https)://)|(www\\.))[a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6}(:[0-9]{1,4})?(/[a-zA-Z0-9\\&%_\\./-~-]*)?"
        val reg = "(https:\\/\\/|http:\\/\\/)[a-zA-Z]+(\\.[a-z0-9A-Z]+)*\\.[a-zA-Z]+((\\/|\\?)(\\w|\\%|\\/|\\&|\\.|\\=|\\?|\\-|\\#)+)*/"
        val m = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE).matcher(source)
        while (m.find()) {
            val r2 = m.group()
            val r3 = m.group(2)
            Logs.i("rs:$r3")
            Logs.i(r2)
            return if (source.contains("<e type=\"web\"")) {
                source
            } else source.replace(r2, "<e type=\"web\" title=\"网页链接\" href=\"$r2\" />")
        }
        return source
    }

    /**
     * 将话题格式化
     *
     * @param info
     * @return
     */
    fun returnTag(info: SubjectInfo): String {
        return "<e type=\"hashtag\" title=\"" + info.title + "\" hid=\"" + info.id + "\" />"
    }

}

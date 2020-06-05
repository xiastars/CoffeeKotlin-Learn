package com.summer.helper.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.malata.summer.helper.R;
import com.summer.helper.web.WebContainerActivity;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.math.BigDecimal;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xiaqiliang on 2017/4/6.
 */
public class STextUtils {

    public static boolean isEmpty(String text) {
        return text == null || text.trim().length() == 0;
    }

    /**
     * 设置文本内的一段颜色
     *
     * @param content
     * @param tvContent
     * @param start
     * @param end
     * @param color
     */
    public static void setSpannableView(String content, TextView tvContent, int start, int end, int color) {
        setSpannableView(content, tvContent, start, end, color,0, false);
    }

    public static void setSpannableView(String content, TextView tvContent, int start, int end, int color, float large, boolean bold) {
        SpannableString msp = getSpannableString(content, start, end, color);
        if (bold) {
            msp.setSpan(new StyleSpan(Typeface.BOLD), start, end,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        if (large != 0) {
            RelativeSizeSpan sizeSpan = new RelativeSizeSpan(large);

            msp.setSpan(sizeSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        if (tvContent != null) {
            tvContent.setText(msp);
        }
    }

    public static SpannableString getSpannableView(String content, int start, int end, int color, float large, boolean bold) {
        SpannableString msp = getSpannableString(content, start, end, color);
        if (bold) {
            msp.setSpan(new StyleSpan(Typeface.BOLD), start, end,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        if (large != 0) {
            RelativeSizeSpan sizeSpan = new RelativeSizeSpan(large);

            msp.setSpan(sizeSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return msp;
    }

    public static SpannableString getSpannableString(String content, int start, int end, int color) {
        SpannableString msp = new SpannableString(content);
        msp.setSpan(new ForegroundColorSpan(color), start, end,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return msp;
    }

    /**
     * 如果数字过10万，则以万为单位
     *
     * @param tvContent
     * @param value
     * @param pre
     * @param suf
     */
    public static void setThousants(TextView tvContent, long value, String pre, String suf) {
        String content = getThousants(value, pre, suf);
        tvContent.setText(content);
    }

    public static String getThousants(long value, String pre, String suf) {
        if (value < 10000) {
            return spliceText(pre, value + "", suf);
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("0.0");
            String mul = decimalFormat.format(value / 10000F);
            return spliceText(pre, mul, "w+", suf);
        }
    }

    public static String setThousantsAndMore(long value) {
        return setThousantsAndMore(value, "", "");
    }

    /**
     * 如果数字过1万，则以万为单位
     *
     * @param value
     * @param pre
     * @param suf
     */
    public static String setThousantsAndMore(long value, String pre, String suf) {
        if (value < 10000) {
            return pre + value + suf;
        } else if (value < 10000 * 1000) {
            float lastV = value / (float) 10000;
            lastV = new BigDecimal(lastV).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            return pre + lastV + "万" + suf;
        } else if (value < 10000 * 10000) {
            float lastV = value / (float) 10000000;
            lastV = new BigDecimal(lastV).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            return pre + lastV + "千万" + suf;
        } else {
            float lastV = value / (float) 100000000;
            lastV = new BigDecimal(lastV).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            return pre + lastV + "亿" + suf;
        }
    }

    /**
     * 如果数字过10万，则以万为单位，返回格式化数据
     *
     * @param value
     * @return
     */
    public static String getThousants(int value) {
        if (value < 100000) {
            return value + "";
        } else if (value < 10000 * 10000) {
            float lastV = value / (float) 10000;
            lastV = new BigDecimal(lastV).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
            return lastV + "万";
        } else {
            float lastV = value / (float) 100000000;
            lastV = new BigDecimal(lastV).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
            return lastV + "亿";
        }
    }

    /**
     * 检查EditText是不是为空
     *
     * @param edtTitle
     * @return
     */
    public static boolean checkEditViewEmpty(EditText edtTitle, int toastRes) {
        String betTitle = edtTitle.getText().toString();
        betTitle = betTitle.replaceAll(" ", "");
        if (TextUtils.isEmpty(betTitle)) {
            SUtils.makeToast(edtTitle.getContext(), toastRes);
            return true;
        }
        return false;
    }

    /**
     * 拼接字符串
     *
     * @param args
     * @return
     */
    public static String spliceText(String... args) {
        StringBuilder builder = new StringBuilder();
        for (String content : args) {
            builder.append(content);
        }
        return builder.toString();
    }

    /**
     * 拼接字符串
     *
     * @param args
     * @return
     */
    public static void setSpliceText(TextView tvConent, String... args) {
        if (tvConent == null) {
            return;
        }
        tvConent.setText(spliceText(args));
    }

    /**
     * 设置支持表情的Text
     *
     * @param name
     * @param showName
     */
    public static void setHtmlText(TextView showName, String name) {
        showName.setText(Html.fromHtml(name));
    }

    /**
     * md5加密
     *
     * @param str
     * @return
     */
    public static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            str = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return str;
    }

    /**
     * 中文转unicode
     *
     * @param cn
     * @return
     */
    public static String cnToUnicode(String cn) {
        char[] chars = cn.toCharArray();
        String returnStr = "";
        for (int i = 0; i < chars.length; i++) {
            returnStr += "\\u" + Integer.toString(chars[i], 16);
        }
        return returnStr;
    }

    /**
     * 检查是否是汉字
     */
    public static boolean isChineseStr(String str) {
        Pattern p_str = Pattern.compile("[\\u4e00-\\u9fa5]+");


        Matcher m = p_str.matcher(str);
        return m.find() && m.group(0).equals(str);
    }

    /**
     * 获取汉字字符串的汉语拼音，英文字符不变
     */
    public static String getPinYin(String chines) {
        StringBuffer sb = new StringBuffer();
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(nameChar[i]);
            }
        }
        if (chines.startsWith("查")) {
            return "zha" + sb.toString();
        }
        if (chines.startsWith("曾")) {
            return "zeng" + sb.toString();
        }
        if (chines.startsWith("区")) {
            return "ou" + sb.toString();
        }
        if (chines.startsWith("解")) {
            return "xie" + sb.toString();
        }
        if (chines.startsWith("单")) {
            return "shan" + sb.toString();
        }
        if (chines.startsWith("翟")) {
            return "zha" + sb.toString();
        }
        if (chines.startsWith("仇")) {
            return "qiu" + sb.toString();
        }
        return sb.toString();
    }

    /**
     * 设置不为空的TextView
     *
     * @param view
     * @param text
     */
    public static void setNotEmptText(TextView view, String text) {
        if (view == null) {
            return;
        }
        if (!TextUtils.isEmpty(text)) {
            view.setText(text);
        } else {
            view.setText("");
        }
    }

    /**
     * 设置不为空的TextView
     *
     * @param view
     * @param text
     */
    public static void setNotEmptText(TextView view, String text, String replace) {
        if (!TextUtils.isEmpty(text)) {
            view.setText(text);
        } else {
            view.setText(replace);
        }
    }

    /**
     * 设置不为空的TextView
     *
     * @param view
     * @param text
     */
    public static void setNotEmptText(EditText view, String text) {
        if (!TextUtils.isEmpty(text)) {
            view.setText(text);
            SUtils.setSelection(view);
        } else {
            view.setText("");
        }
    }

    /**
     * 获取保留一个小数点
     *
     * @param value
     * @return
     */
    public static float getOneDot(float value) {
        BigDecimal b = new BigDecimal(value);
        float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        return f1;
    }

    /**
     * 将文本中的表情字符转换为表情
     *
     * @param contentStr
     * @return
     */
    public static SpannableStringBuilder replaceEmoji(final Context context, String contentStr, int textSize) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder(contentStr);
        String regex_http = "<e type=\"web\".*>";
        Pattern pattern = Pattern.compile(regex_http, Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(spannableString);
        //find会在整个输入中寻找是否有匹配的子字符串
        while (matcher.find()) {
            try {
                final String url = getUrl(spannableString.subSequence(matcher.start(), matcher.end()).toString());
                Logs.i("url:::" + url);
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.details_post_content_link_icon);
                //设置表情显示的大小
                bitmap = Bitmap.createScaledBitmap(bitmap, textSize, textSize, true);
                ImageSpan imageSpan = new ImageSpan(context, bitmap);
                spannableString.setSpan(imageSpan, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString string = new SpannableString(" 网页链接");
                spannableString.insert(matcher.end(), string);

                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        JumpTo.getInstance().commonJump(context, WebContainerActivity.class, url);
                        SUtils.makeToast(context, "url" + url);
                    }
                };
                spannableString.setSpan(clickableSpan, matcher.end() - 1, matcher.end() + 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // spannableString.setSpan(string, matcher.start()+1, matcher.end()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (NullPointerException e) {
                e.printStackTrace();
                continue;
            }
        }

        return spannableString;
    }

    /**
     * 根据机型，返回大小端Long的字节数组
     *
     * @param data
     * @return
     */
    public static byte[] getBytes(long data) {
        byte[] bytes = new byte[8];
        if (isLittleEndian()) {
            bytes[0] = (byte) (data & 0xff);
            bytes[1] = (byte) ((data >> 8) & 0xff);
            bytes[2] = (byte) ((data >> 16) & 0xff);
            bytes[3] = (byte) ((data >> 24) & 0xff);
            bytes[4] = (byte) ((data >> 32) & 0xff);
            bytes[5] = (byte) ((data >> 40) & 0xff);
            bytes[6] = (byte) ((data >> 48) & 0xff);
            bytes[7] = (byte) ((data >> 56) & 0xff);
        } else {
            bytes[7] = (byte) (data & 0xff);
            bytes[6] = (byte) ((data >> 8) & 0xff);
            bytes[5] = (byte) ((data >> 16) & 0xff);
            bytes[4] = (byte) ((data >> 24) & 0xff);
            bytes[3] = (byte) ((data >> 32) & 0xff);
            bytes[2] = (byte) ((data >> 40) & 0xff);
            bytes[1] = (byte) ((data >> 48) & 0xff);
            bytes[0] = (byte) ((data >> 56) & 0xff);
        }
        return bytes;
    }

    private static boolean isLittleEndian() {
        return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
    }

    private static String getUrl(String var) {
        Logs.i("url:::" + var);
        /**
         * 获取指定HTML标签的指定属性的值
         * @param source 要匹配的源文本
         * @param element 标签名称
         * @param attr 标签的属性名称
         * @return 属性值列表
         */

        return match(var, "e", "href");
    }

    public static String match(String source, String element, String attr) {
        String reg = "<" + element + "[^<>]*?\\s" + attr + "=['\"]?(.*?)['\"]?(\\s.*?)?>";
        Matcher m = Pattern.compile(reg).matcher(source);
        while (m.find()) {
            String r = m.group(1);
            return r;
        }
        return "";
    }


}

package com.summer.helper.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.malata.summer.helper.R;
import com.summer.helper.db.CommonService;
import com.summer.helper.db.DBType;
import com.summer.helper.downloader.DownloadTask;
import com.summer.helper.downloader.DownloadTaskListener;
import com.summer.helper.server.EasyHttp;
import com.summer.helper.utils.SFileUtils.FileType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 工具类
 *
 * @author malata_xiaqiliang
 * @time 2016年6月6日
 */

@SuppressWarnings({"rawtypes", "unchecked"})
public class SUtils {

    public static int screenHeight;
    public static int screenWidth;
    private static InputMethodManager inputMethodManager = null;
    private static Toast mToast = null;

    static Context context;

    public static void setContext(Context c) {
        context = c;
    }

    /**
     * 弹出提示
     *
     * @param context
     * @param id
     */
    public static void makeToast(Context context, int id) {
        makeToast(context, context.getResources().getString(id));
    }

    /**
     * 弹出提示
     *
     * @param context
     * @param text
     */
    public static void makeToast(Context context, String text) {
        if (VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            View view = null;
            if (mToast == null) {
                mToast = new Toast(context.getApplicationContext());
                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.layout_commont_toast, null);
            } else {
                view = mToast.getView();
            }
            TextView textview = (TextView) view.findViewById(R.id.textview_hint);
            textview.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setView(view);
            mToast.show();
        } else {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设置不为空的TextView
     *
     * @param view
     * @param text
     */
    public static void setNotEmptText(TextView view, String text) {
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
     * 获取int类型数据
     *
     * @param mContext
     * @param selected
     */
    public static void saveIntegerData(Context mContext, String type, int selected) {
        SharedPreferences settings = mContext.getSharedPreferences("savedata", 0);
        settings.edit().putInt(type, selected).commit();
    }

    /**
     * 得到int类型数据
     *
     * @param mContext
     * @return
     */
    public static int getIntegerData(Context mContext, String type) {
        SharedPreferences settings = mContext.getSharedPreferences("savedata", 0);
        int select = settings.getInt(type, 0);
        return select;
    }

    /**
     * 获取long类型数据
     *
     * @param mContext
     * @param selected
     */
    public static void saveLongData(Context mContext, String type, long selected) {
        SharedPreferences settings = mContext.getSharedPreferences("savedata", 0);
        settings.edit().putLong(type, selected).commit();
    }

    /**
     * 得到long类型数据
     *
     * @param mContext
     * @return
     */
    public static long getLongData(Context mContext, String type) {
        SharedPreferences settings = mContext.getSharedPreferences("savedata", 0);
        long select = settings.getLong(type, 0);
        return select;
    }

    /**
     * 获取boolean类型数据
     *
     * @param mContext
     * @param selected
     */
    public static void saveBooleanData(Context mContext, String type, Boolean selected) {
        SharedPreferences settings = mContext.getSharedPreferences("savedata", 0);
        settings.edit().putBoolean(type, selected).commit();
    }

    /**
     * 得到boolean类型数据
     *
     * @param mContext
     * @return
     */
    public static Boolean getBooleanData(Context mContext, String type) {
        SharedPreferences settings = mContext.getSharedPreferences("savedata", 0);
        Boolean select = settings.getBoolean(type, false);
        return select;
    }

    /**
     * 获取boolean类型数据
     *
     * @param mContext
     * @param selected
     */
    public static void saveStringData(Context mContext, String type, String selected) {
        SharedPreferences settings = mContext.getSharedPreferences("savedata", 0);
        settings.edit().putString(type, selected).commit();
    }

    /**
     * 得到boolean类型数据
     *
     * @param mContext
     * @return
     */
    public static String getStringData(Context mContext, String type) {
        SharedPreferences settings = mContext.getSharedPreferences("savedata", 0);
        String select = settings.getString(type, "");
        return select;
    }

    /**
     * 返回是否为当天，用于一些一天内只需请求一次的请求
     *
     * @param context
     * @return true 为是今天
     */
    public static boolean isToday(Context context) {
        Calendar ca = Calendar.getInstance();
        String lastDay = SUtils.getStringData(context, "isToday");
        if (!lastDay.equals(ca.get(Calendar.MONTH) + "-" + ca.get(Calendar.DATE) + "")) {
            return false;
        }
        return true;
    }

    /**
     * 返回是否为当天，用于一些一天内只需请求一次的请求
     *
     * @param context
     * @return true 为是今天
     */
    public static boolean isToday(Context context, String from) {
        Calendar ca = Calendar.getInstance();
        String lastDay = SUtils.getStringData(context, "isToday" + from);
        if (!lastDay.equals(ca.get(Calendar.MONTH) + "-" + ca.get(Calendar.DATE) + "")) {
            return false;
        }
        return true;
    }

    /**
     * 保存今天日期
     *
     * @param context
     */
    public static void saveToday(Context context, String from) {
        Calendar ca = Calendar.getInstance();
        SUtils.saveStringData(context, "isToday" + from, ca.get(Calendar.MONTH) + "-" + ca.get(Calendar.DATE));
    }

    /**
     * 今天日期仍然要刷新
     *
     * @param context
     */
    public static void refreshToday(Context context, String from) {
        SUtils.saveStringData(context, "isToday" + from, "hello world");
    }

    /**
     * 保存今天日期
     *
     * @param context
     */
    public static void saveToday(Context context) {
        Calendar ca = Calendar.getInstance();
        SUtils.saveStringData(context, "isToday", ca.get(Calendar.MONTH) + "-" + ca.get(Calendar.DATE));
    }

    /**
     * 为EditText设置光标
     */
    public static void setSelection(EditText editView) {
        editView.setSelection(editView.getText().length());
        editView.requestFocus();
    }

    /**
     * xml里是dp，但是到代码里是px，这个方法让传进去的dp仍然保持dp
     *
     * @param context
     * @param value   传的dp值
     * @return
     */
    public static int getDip(Context context, int value) {
        int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                context.getResources().getDisplayMetrics());
        return pageMargin;
    }

    public static int getDimen(Context context, int id) {
        return (int) context.getResources().getDimension(id);
    }

    public static int getSWidth(Activity context, int width) {
        if (screenWidth == 0) {
            initScreenDisplayMetrics(context);
        }
        return (int) ((float) width / 1920f * screenWidth);
    }

    public static int getSHeight(Activity context, int width) {
        if (screenHeight == 0) {
            initScreenDisplayMetrics(context);
        }
        return (int) ((float) width / 1080f * screenWidth * 9 / 16);
    }

    public static float getDip(Context context, float value) {
        float pageMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                context.getResources().getDisplayMetrics());
        return pageMargin;
    }

    /**
     * 初始化当前屏幕分辨率
     */
    public static void initScreenDisplayMetrics(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        SUtils.screenHeight = metric.heightPixels;
        SUtils.screenWidth = metric.widthPixels;
    }

    /**
     * 获取安卓唯一序列号
     *
     * @return
     */
    public static String getLocalMacAddress() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception ignored) {

        }
        return serial;
    }

    public static void hideSoftInpuFromWindow(View searchEdit) {
        if (searchEdit == null)
            return;
        if (inputMethodManager == null)
            inputMethodManager = (InputMethodManager) searchEdit.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
    }

    /**
     * 打开输入法
     */
    public static void showSoftInpuFromWindow(EditText searchEdit) {
        if (searchEdit == null)
            return;
        if (searchEdit != null) {
            searchEdit.requestFocus();
        }
        Context context = searchEdit.getContext();
        if (inputMethodManager == null)
            inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(searchEdit, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 打开输入法
     */
    public static void showSoftInpuFromWindow(View view, Context context) {
        if (inputMethodManager == null)
            inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 判断现在网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static Bitmap decodeBackgoundBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 设置支持表情的Text
     *
     * @param name
     * @param showName
     * @param context
     */
    public static void setHtmlText(String name, TextView showName) {
        showName.setText(Html.fromHtml(HtmlText(name)), null);
    }

    /**
     * 去除含html标签的字符串
     *
     * @param inputString
     * @return
     */
    public static String HtmlText(String inputString) {
        if (inputString == null || "".equals(inputString))
            return "";
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;
        try {
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
            // }
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
            // }
            // String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签

            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签
            //
            // p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            // m_html = p_html.matcher(htmlStr);
            // htmlStr = m_html.replaceAll(""); // 过滤html标签

            /* 空格 —— */
            // p_html = Pattern.compile("\\ ", Pattern.CASE_INSENSITIVE);
            // m_html = p_html.matcher(htmlStr);
            htmlStr = htmlStr.replaceAll(" ", " ");

            textStr = htmlStr;

        } catch (Exception e) {
        }
        return textStr;
    }

    /**
     * 将当前时间格式化为 --年--月--日
     *
     * @return
     */
    public static String getDays(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        String s = format.format(date);
        return s;
    }

    /**
     * 将中国格式的时间转化为毫秒
     *
     * @param fString
     * @return
     * @throws ParseException
     */
    public static long getTime(String fString) throws ParseException {
        long mTime = 0;
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy" + "-" + "MM" + "-" + "dd" + " " + "hh" + ":" + "mm" + ":" + "ss", Locale.CHINA);
        mTime = format.parse(fString).getTime();
        return mTime;
    }

    /**
     * 将中国格式的时间转化为毫秒
     *
     * @param fString
     * @return
     * @throws ParseException
     */
    public static String parseSceneTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy" + "-" + "MM" + "-" + "dd" + " " + "HH" + ":" + "mm" + ":" + "ss", Locale.CHINA);
        return format.format(new Date(time));
    }

    /**
     * 转换时间，格式--
     *
     * @param date
     * @return
     */
    public static String getDayWithFormat(String formatContent) {
        SimpleDateFormat format = new SimpleDateFormat(formatContent, Locale.CHINA);
        String s = format.format(new Date());
        return s;
    }

    /**
     * 将中国格式的时间转化为毫秒
     *
     * @param fString
     * @return
     * @throws ParseException
     */
    public static String getAudioNameByTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy" + "" + "MM" + "" + "dd" + "_" + "HH" + "mm" + "ss",
                Locale.CHINA);
        return format.format(new Date(System.currentTimeMillis()));
    }

    /**
     * TextView 点击改变颜色,使其透明化
     *
     * @param view
     */
    public static void clickTransColor(final TextView view) {
        if (null == view)
            return;
        final ColorStateList color = view.getTextColors();
        view.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setTextColor(color.withAlpha(100));
                        view.setScaleX(0.9f);
                        view.setScaleY(0.9f);
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                view.setAlpha(255);
                                view.setTextColor(color);
                                view.setScaleX(1f);
                                view.setScaleY(1f);
                            }
                        }, 100);
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 获取当天星期
     *
     * @return
     */
    public static String getWeekDay() {
        String mWeekDay = "";
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int index = calendar.get(Calendar.DAY_OF_WEEK);
        switch (index) {
            case 1:
                mWeekDay = "星期日";
                break;
            case 2:
                mWeekDay = "星期一";
                break;
            case 3:
                mWeekDay = "星期二";
                break;
            case 4:
                mWeekDay = "星期三";
                break;
            case 5:
                mWeekDay = "星期四";
                break;
            case 6:
                mWeekDay = "星期五";
                break;
            case 7:
                mWeekDay = "星期六";
                break;
            default:
                mWeekDay = "星期日";
                break;
        }
        calendar.clear();
        return mWeekDay;
    }

    // 返回格式化时间
    public static String getRecentlyTime(long dtTime, long serverTime, Context mContext) {
        String dateStr = "";
        long now = serverTime;
        now = now - dtTime;
        long d = 24L * 60L * 60L * 1000L;
        if (now < d) {// 小于一天
            if (now < 60 * 60 * 1000) {// 小于一h
                now = now / 1000 / 60;
                now = (now < 0 ? 0 : now);
                if (now > 0) {
                    dateStr = now + "分钟";
                } else {
                    dateStr = (now + 1) + "分钟";
                }
            } else {// h
                now = now / 1000 / 60 / 60;
                if (now > 1) {
                    dateStr = now + "小时";
                } else {
                    dateStr = now + "小时";
                }
            }
        } else {// d
            now = now / 1000 / 60 / 60 / 24;
            if (now > 1) {
                dateStr = now + "天";
            } else {
                dateStr = now + "天";
            }
        }
        return dateStr;
    }

    /**
     * TextView 点击改变颜色,使其透明化
     *
     * @param view
     */
    public static void clickTransColor(final ImageView view) {
        view.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (view.getBackground() != null) {
                            view.getBackground().setAlpha(100);
                        }

                        view.setScaleX(0.9f);
                        view.setScaleY(0.9f);
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                if (view.getBackground() != null) {
                                    view.getBackground().setAlpha(255);
                                }
                                view.setScaleX(1f);
                                view.setScaleY(1f);
                            }
                        }, 100);
                        break;
                }
                return false;
            }
        });
    }

    public static void clickTransColor(final View view) {

        view.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (view instanceof ViewGroup) {
                            int childCount = ((ViewGroup) view).getChildCount();
                            for (int i = 0; i < childCount; i++) {
                                final View child = ((ViewGroup) view).getChildAt(i);
                                if (child instanceof TextView) {
                                    final ColorStateList color = ((TextView) child).getTextColors();
                                    ((TextView) child).setTextColor(color.withAlpha(100));
                                    child.setScaleX(0.9f);
                                    child.setScaleY(0.9f);
                                    new Handler().postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            child.setAlpha(255);
                                            ((TextView) child).setTextColor(color);
                                            child.setScaleX(1f);
                                            child.setScaleY(1f);
                                        }
                                    }, 100);
                                } else if (child instanceof ImageView) {
                                    if (view.getBackground() != null) {
                                        view.getBackground().setAlpha(100);
                                    }

                                    view.setScaleX(0.9f);
                                    view.setScaleY(0.9f);
                                    new Handler().postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            if (view.getBackground() != null) {
                                                view.getBackground().setAlpha(255);
                                            }
                                            view.setScaleX(1f);
                                            view.setScaleY(1f);
                                        }
                                    }, 100);
                                } else if (child instanceof ViewGroup) {
                                    clickTransColor(child);
                                }
                            }
                        }

                        break;
                }
                return false;
            }
        });
    }

    /**** 取SD卡路径不带/ ****/
    public static String getSDPath() {
        File sdDir = null;
        try {
            boolean sdCardExist = android.os.Environment.getExternalStorageState()
                    .equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
            if (sdCardExist) {
                sdDir = android.os.Environment.getExternalStorageDirectory();// 获取跟目录
            } else {
                File file = new File(Environment.getDataDirectory() + "/sdcard");
                if (file.canRead()) {
                    return file.toString();
                } else {
                    return "";
                }
            }
            if (sdDir != null) {
                return sdDir.toString();
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return "";
    }

    public static int getStatusBarHeight(Activity activity) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    // 缩放系数
    public final static int SCALE = 8;

    /**
     * 模糊函数
     *
     * @param context
     * @param sentBitmap
     * @param radius
     * @return
     */
    @SuppressLint("NewApi")
    public static Bitmap doBlur(Context context, Bitmap sentBitmap, float radius) {
        if (sentBitmap == null)
            return null;
        if (radius <= 0 || radius > 25)
            radius = 25f;// 范围在1-25之间
        if (radius <= 6 && VERSION.SDK_INT > 16) {// 经测试，radius大于6后，fastBlur效率更高，并且RenderScript在api11以上使用
            Bitmap bitmap = Bitmap.createScaledBitmap(sentBitmap, sentBitmap.getWidth() / SCALE,
                    sentBitmap.getHeight() / SCALE, false);// 先缩放图片，增加模糊速度
            final RenderScript rs = RenderScript.create(context);
            final Allocation input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
            rs.destroy();
            return bitmap;
        } else {// 快速模糊
            return fastBlur(sentBitmap, radius);
        }
    }

    /**
     * 快速模糊算法
     *
     * @param sbitmap
     * @param radiusf
     * @return Stack Blur v1.0 from
     * http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
     * Java Author: Mario Klingemann <mario at quasimondo.com>
     * http://incubator.quasimondo.com created Feburary 29, 2004 Android
     * port : Yahel Bouaziz <yahel at kayenko.com>
     * http://www.kayenko.com ported april 5th, 2012
     * <p>
     * This is a compromise between Gaussian Blur and Box blur It
     * creates much better looking blurs than Box Blur, but is 7x faster
     * than my Gaussian Blur implementation. I called it Stack Blur
     * because this describes best how this filter works internally: it
     * creates a kind of moving stack of colors whilst scanning through
     * the image. Thereby it just has to add one new block of color to
     * the right side of the stack and remove the leftmost color. The
     * remaining colors on the topmost layer of the stack are either
     * added on or reduced by one, depending on if they are on the right
     * or on the left side of the stack. If you are using this algorithm
     * in your code please add the following line:
     * <p>
     * Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>
     */
    public static Bitmap fastBlur(Bitmap sbitmap, float radiusf) {
        try {
            Bitmap bitmap = Bitmap.createScaledBitmap(sbitmap, sbitmap.getWidth() / SCALE, sbitmap.getHeight() / SCALE,
                    false);// 先缩放图片，增加模糊速度
            int radius = (int) radiusf;
            if (radius < 1) {
                return (null);
            }

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            int[] pix = new int[w * h];
            bitmap.getPixels(pix, 0, w, 0, 0, w, h);

            int wm = w - 1;
            int hm = h - 1;
            int wh = w * h;
            int div = radius + radius + 1;

            int r[] = new int[wh];
            int g[] = new int[wh];
            int b[] = new int[wh];
            int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
            int vmin[] = new int[Math.max(w, h)];

            int divsum = (div + 1) >> 1;
            divsum *= divsum;
            int dv[] = new int[256 * divsum];
            for (i = 0; i < 256 * divsum; i++) {
                dv[i] = (i / divsum);
            }

            yw = yi = 0;

            int[][] stack = new int[div][3];
            int stackpointer;
            int stackstart;
            int[] sir;
            int rbs;
            int r1 = radius + 1;
            int routsum, goutsum, boutsum;
            int rinsum, ginsum, binsum;

            for (y = 0; y < h; y++) {
                rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                for (i = -radius; i <= radius; i++) {
                    p = pix[yi + Math.min(wm, Math.max(i, 0))];
                    sir = stack[i + radius];
                    sir[0] = (p & 0xff0000) >> 16;
                    sir[1] = (p & 0x00ff00) >> 8;
                    sir[2] = (p & 0x0000ff);
                    rbs = r1 - Math.abs(i);
                    rsum += sir[0] * rbs;
                    gsum += sir[1] * rbs;
                    bsum += sir[2] * rbs;
                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }
                }
                stackpointer = radius;

                for (x = 0; x < w; x++) {

                    r[yi] = dv[rsum];
                    g[yi] = dv[gsum];
                    b[yi] = dv[bsum];

                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;

                    stackstart = stackpointer - radius + div;
                    sir = stack[stackstart % div];

                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];

                    if (y == 0) {
                        vmin[x] = Math.min(x + radius + 1, wm);
                    }
                    p = pix[yw + vmin[x]];

                    sir[0] = (p & 0xff0000) >> 16;
                    sir[1] = (p & 0x00ff00) >> 8;
                    sir[2] = (p & 0x0000ff);

                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;

                    stackpointer = (stackpointer + 1) % div;
                    sir = stack[(stackpointer) % div];

                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];

                    yi++;
                }
                yw += w;
            }
            for (x = 0; x < w; x++) {
                rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                yp = -radius * w;
                for (i = -radius; i <= radius; i++) {
                    yi = Math.max(0, yp) + x;

                    sir = stack[i + radius];

                    sir[0] = r[yi];
                    sir[1] = g[yi];
                    sir[2] = b[yi];

                    rbs = r1 - Math.abs(i);

                    rsum += r[yi] * rbs;
                    gsum += g[yi] * rbs;
                    bsum += b[yi] * rbs;

                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }

                    if (i < hm) {
                        yp += w;
                    }
                }
                yi = x;
                stackpointer = radius;
                for (y = 0; y < h; y++) {
                    // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                    pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;

                    stackstart = stackpointer - radius + div;
                    sir = stack[stackstart % div];

                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];

                    if (x == 0) {
                        vmin[y] = Math.min(y + r1, hm) * w;
                    }
                    p = x + vmin[y];

                    sir[0] = r[p];
                    sir[1] = g[p];
                    sir[2] = b[p];

                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;

                    stackpointer = (stackpointer + 1) % div;
                    sir = stack[stackpointer];

                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];

                    yi += w;
                }
            }
            bitmap.setPixels(pix, 0, w, 0, 0, w, h);
            return (bitmap);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断网络状态
     *
     * @param context
     * @return
     */
    public static NetState getNetWorkType(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return NetState.BROKEN;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        if (info[i].getType() == ConnectivityManager.TYPE_WIFI) {
                            return NetState.WIFI;
                        } else {
                            return NetState.MOBILE;
                        }
                    }
                }
            }
        }
        return NetState.BROKEN;
    }

    /**
     * 将路径文件转为byte[]
     *
     * @return 二进制文件数据
     */
    public static byte[] readFileAsBytes(String filename) {
        try {
            InputStream ins = new FileInputStream(filename);
            byte[] data = new byte[ins.available()];

            ins.read(data);
            ins.close();

            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 将Asset里的文本读出来
     *
     * @return 二进制文件数据
     */
    public static String readAssetFileToString(Context context, String filename) {
        try {
            InputStream ins = context.getAssets().open(filename);
            byte[] data = new byte[ins.available()];

            ins.read(data);
            ins.close();

            return new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 将byte数组写进文件
     *
     * @param path
     * @param sub
     */
    public static void writeBytesToFile(String path, byte[] sub, int isFirst) {
        File file = new File(path);
        FileOutputStream stream = null;
        if (file.exists()) {
            file.delete();
        }
        try {
            stream = new FileOutputStream(file, true);
            stream.write(sub);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != stream) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getandSaveCurrentImage(Activity context, Bitmap bitmap, String savePath) {
        try {
            if (bitmap != null) {
                // 文件
                File file = new File(savePath);
                if (file.exists()) {
                    file.delete();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileOutputStream fos = null;
                fos = new FileOutputStream(file);
                if (null != fos) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                    fos.flush();
                    fos.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        }
        return savePath;
    }

    public static int[] getBitMapSize(String path) {
        int[] size = new int[2];
        File file = new File(path);
        if (file.exists()) {
            InputStream in = null;
            try {
                in = new FileInputStream(file);
                Options OPTIONS_GET_SIZE = new Options();
                BitmapFactory.decodeStream(in, null, OPTIONS_GET_SIZE);
                size[0] = OPTIONS_GET_SIZE.outWidth;
                size[1] = OPTIONS_GET_SIZE.outHeight;
            } catch (FileNotFoundException e) {
            } catch (OutOfMemoryError e) {
            } finally {
                if (null != in) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return size;
    }

    public static Bitmap createScaleBitmap(String path) {
        return createScaleBitmap(path, 0, 0);
    }

    public static Bitmap createScaleBitmap(String path, int width, int height) {
        File file = new File(path);
        if (file.exists()) {
            InputStream in = null;
            try {
                in = new FileInputStream(file);
                int[] size = getBitMapSize(path);
                if (size[0] == 0 || size[1] == 0) {
                    return null;
                }
                int scale = 1;
                if (width != 0 && height != 0) {
                    int a = size[0] / width;
                    int b = size[1] / height;
                    scale = Math.max(a, b);
                }
                Options OPTIONS_DECODE = new Options();
                synchronized (OPTIONS_DECODE) {
                    OPTIONS_DECODE.inSampleSize = scale;
                    Bitmap bitMap = BitmapFactory.decodeStream(in, null, OPTIONS_DECODE);
                    return bitMap;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (null != in) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 去除Bitmap的颜色
     *
     * @param bitmap
     * @param color
     * @return
     */
    public static Bitmap stripBitmapColor(Bitmap bitmap) {
        bitmap = Bitmap.createBitmap(bitmap).copy(Config.ARGB_8888, true);
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                int colorss = bitmap.getPixel(i, j);
                if (colorss != 0) {
                    bitmap.setPixel(i, j, Color.parseColor("#f3f3f3"));
                }

            }
        }
        return bitmap;
    }

    /**
     * 设置Asset里面的图片为背景
     *
     * @param imageView
     * @param path
     */
    public static void setAssetPic(ImageView imageView, String path) {
        try {
            InputStream bg = imageView.getContext().getAssets().open(path);
            Bitmap bitmap = BitmapFactory.decodeStream(bg);
            imageView.setImageBitmap(bitmap);
            bg.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置图片，使用默认Holder
     *
     * @param view
     * @param img
     */
    public static void setPic(ImageView view, String img, boolean download) {
        setPic(view, img, 0, R.drawable.trans, download);
    }

    /**
     * 设置图片，使用默认Holder
     *
     * @param view
     * @param img
     */
    public static void setPicWithSize(ImageView view, String img, int size) {
        setPic(view, img, size, R.drawable.trans, true);
    }

    /**
     * 设置图片，使用默认Holder
     *
     * @param view
     * @param img
     */
    public static void setPic(ImageView view, String img, int holder, boolean download) {
        setPic(view, img, 0, holder, download);
    }

    public static void setPic(ImageView view, String img) {
        setPic(view, img, 0, R.drawable.trans, true);
    }

    /**
     * 设置图片，使用自定义Holder
     *
     * @param view
     * @param img
     */
    public static void setPicWithHolder(ImageView view, String img, int holder) {
        setPic(view, img, 0, holder, true);
    }

    /**
     * 设置本地资源图片
     *
     * @param view
     * @param holder
     */
    public static void setPicResource(ImageView view, int holder) {
        setPic(view, null, 0, 0, holder, false, null);
    }


    public static void setPicWithHolder(ImageView view, String img, int holder, boolean download) {
        setPic(view, img, 0, holder, download);
    }

    public static void setPic(ImageView view, String img, int size, int holder, boolean download) {
        setPic(view, img, size, size, holder, download, null);
    }

    public static void setPic(ImageView view, String img, int size, int holder) {
        setPic(view, img, size, size, holder, true, null);
    }

    public static void setPic(ImageView view, String img, boolean dwnload, SimpleTarget target) {
        setPic(view, img, 0, 0, R.drawable.trans, dwnload, target);
    }

    public static void setPic(ImageView view, String img, int width, int height, int holder, boolean download) {
        setPic(view, img, width, height, holder, download, null);
    }

    public static void setPic(ImageView view, String img, int width, int height, int holder, boolean download,
                              SimpleTarget target) {
        if (null == view)
            return;
        if (TextUtils.isEmpty(img)) {
            view.setImageBitmap(null);
            Drawable drawable = view.getContext().getResources().getDrawable(holder);
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    view.setImageBitmap(bitmap);
                }
            } else {
                view.setImageResource(holder);
            }
            return;
        }
        if (img.contains("\\")) {
            img = img.replaceAll("\\", "");
        }

        String localpath = img;
        if (download) {
            String path = downloadImage(img, view.getContext());
            if (null != path) {
                File file = new File(path);
                if (file.exists() && file.length() > 0) {
                    localpath = path;
                }
            }
        }
        try {
            if (width == 0) {
                if (localpath.startsWith("http")) {
                    if (target == null) {
                        Glide.with(view.getContext()).load(localpath).crossFade().dontAnimate().into(view);
                    } else {
                        Glide.with(view.getContext()).load(localpath).crossFade().dontAnimate().into(target);
                    }
                } else {
                    File file = new File(localpath);
                    if (file.exists()) {
                        if (target == null) {
                            Glide.with(view.getContext()).load(file).dontAnimate().into(view);
                        } else {
                            Glide.with(view.getContext()).load(file).dontAnimate().into(target);
                        }
                    }
                }
            } else {
                if (localpath.startsWith("http")) {
                    if (target == null) {
                        Glide.with(view.getContext()).load(localpath).placeholder(holder).override(width, height)
                                .dontAnimate().into(view);
                    } else {
                        Glide.with(view.getContext()).load(localpath).placeholder(holder).override(width, height)
                                .dontAnimate().into(target);
                    }
                } else {
                    File file = new File(localpath);
                    if (file.exists()) {
                        if (target == null) {
                            Glide.with(view.getContext()).load(file).placeholder(holder).override(width, height)
                                    .dontAnimate().into(view);
                        } else {
                            Glide.with(view.getContext()).load(file).placeholder(holder).override(width, height)
                                    .dontAnimate().into(target);
                        }
                    }

                }
            }
        } catch (

                Exception e) {
            e.printStackTrace();
        }

    }

    public static String getUrlHashCode(String url) {
        String uuid = UUID.nameUUIDFromBytes(url.getBytes()).toString();
        String path = uuid.hashCode() + "";
        if (path.contains("-")) {
            path = path.replace("-", "00000");
        }
        return path;
    }

    /**
     * 下载图片
     *
     * @param url
     * @param context
     * @return
     */
    public static String downloadImage(String url, Context context) {
        if (!url.startsWith("http")) {
            return null;
        }
        if (url.contains("\\")) {
            url = url.replaceAll("\\", "");
        }
        String filepath = SFileUtils.getImageViewDirectory();
        String fileName = SUtils.getUrlHashCode(url) + FileType.FILE_PNG;
        String path = filepath + fileName;
        File file = new File(filepath + fileName);
        if (file.exists() && file.length() > 0) {
            path = file.getAbsolutePath();
            return path;
        } else {
            downloadURL(context, url, filepath, fileName);
            return null;
        }
    }

    private static void downloadURL(Context context, final String url, final String filepath, final String fileName) {
        EasyHttp.download(context, url, filepath, fileName, new DownloadTaskListener() {

            @Override
            public void onPause(DownloadTask downloadTask) {
            }

            @Override
            public void onError(DownloadTask downloadTask, int errorCode) {
                Logs.i("errorCode:" + errorCode);
            }

            @Override
            public void onDownloading(DownloadTask downloadTask) {

                if (downloadTask.getPercent() == 100) {
                    Logs.i("下载成功:" + filepath + fileName);
                    // service.insert(DBType.SOURCE_URL, url,finalPath);
                }
            }
        });
    }

    /**
     * 下载音频
     *
     * @param url
     * @return
     */
    public static String downloadAudio(Context context, final String url, boolean music) {
        if (null == url || !url.startsWith("http")) {
            return null;
        }
        String filepath = null;
        filepath = SFileUtils.getAudioDirectory();
        String fileName = SUtils.getUrlHashCode(url) + ".mp3";
        String path = filepath + fileName;
        File file = new File(path);
        if (file.exists() && file.length() > 0) {
            return path;
        } else {
            EasyHttp.download(context, url, filepath, fileName, new DownloadTaskListener() {

                @Override
                public void onPause(DownloadTask downloadTask) {
                }

                @Override
                public void onError(DownloadTask downloadTask, int errorCode) {
                    Logs.i("errorCode:" + errorCode);
                }

                @Override
                public void onDownloading(DownloadTask downloadTask) {

                    if (downloadTask.getPercent() == 100) {
                        Logs.i("下载成功:");
                    }
                }
            });
        }
        return null;
    }

    /**
     * 下载视频
     *
     * @param url
     * @return
     */
    public static String downloadVideo(Context context, final String url) {
        if (url == null || !url.startsWith("http")) {
            return null;
        }
        String filepath = SFileUtils.getAudioDirectory();
        String fileName = SUtils.getUrlHashCode(url) + FileType.FILE_MP4;
        String path = filepath + fileName;
        File file = new File(path + "");
        if (file.exists() && file.length() > 0) {
            return path;
        } else {
            EasyHttp.download(context, url, filepath, fileName, new DownloadTaskListener() {

                @Override
                public void onPause(DownloadTask downloadTask) {
                }

                @Override
                public void onError(DownloadTask downloadTask, int errorCode) {
                    Logs.i("errorCode:" + errorCode);
                }

                @Override
                public void onDownloading(DownloadTask downloadTask) {

                    if (downloadTask.getPercent() == 100) {
                        Logs.i("下载成功:");
                    }
                }
            });
        }
        return null;
    }

    /**
     * 根据链接获取图片集，如果没有，则保存到数据库
     *
     * @param img
     * @return
     */
    public static Bitmap[] compressBitmps(final String img, final Context context) {
        if (img == null)
            return null;
        final CommonService service = new CommonService(context);
        final List<byte[]> localePath = (List<byte[]>) service.getListData(DBType.SOURCE_BITMAP, img);
        if (null != localePath && localePath.size() > 0) {
            int count = localePath.size();
            Bitmap[] mBitmaps = new Bitmap[count];
            for (int i = 0; i < count; i++) {
                byte[] arrays = localePath.get(i);
                Bitmap bitma = BitmapFactory.decodeByteArray(arrays, 0, arrays.length);
                mBitmaps[i] = bitma;
            }
            return mBitmaps;
        }
        final String filepath = SFileUtils.getAvatarDirectory();
        String fileName = null;
        fileName = UUID.randomUUID() + FileType.FILE_PNG;
        boolean isExist = EasyHttp.existDownload(context, img);
        if (isExist) {
            return null;
        }
        final String savePath = filepath + fileName;
        EasyHttp.download(context, img, filepath, fileName, new DownloadTaskListener() {

            @Override
            public void onPause(DownloadTask downloadTask) {
            }

            @Override
            public void onError(DownloadTask downloadTask, int errorCode) {
                Logs.i("errorCode:" + errorCode);
            }

            @Override
            public void onDownloading(DownloadTask downloadTask) {

                if (downloadTask.getPercent() == 100) {
                    String[] picinfo = null;
                    if (img.contains("/")) {
                        String imgs = (String) img.subSequence(img.lastIndexOf("/") + 1, img.lastIndexOf("."));
                        picinfo = imgs.split("_");
                    }
                    Bitmap bitmap = BitmapFactory.decodeFile(savePath);
                    if (null != bitmap) {
                        int rowCount = Integer.parseInt(picinfo[2]);
                        int perWidth = Integer.parseInt(picinfo[0]);
                        int perHeight = Integer.parseInt(picinfo[1]);
                        int collumCount = Integer.parseInt(picinfo[3]);
                        int totalCount = Integer.parseInt(picinfo[4]);
                        int index = 0;
                        int startX = 0;
                        int startY = 0;
                        Bitmap[] mBitmaps = new Bitmap[totalCount];
                        for (int i = 0; i < rowCount; i++) {
                            startX = 0;
                            startY = i * perHeight;
                            for (int x = 0; x < collumCount; x++) {
                                if (index == totalCount) {
                                    break;
                                }
                                startX = x * perWidth;
                                mBitmaps[index] = Bitmap.createBitmap(bitmap, startX, startY, perWidth, perHeight);
                                index++;
                            }
                        }
                        List<byte[]> bitmappath = new ArrayList<>();
                        int rIndex = 0;
                        for (int i = 0; i < rowCount; i++) {
                            startX = 0;
                            startY = i * perHeight;
                            for (int x = 0; x < collumCount; x++) {
                                if (rIndex == totalCount) {
                                    break;
                                }
                                startX = x * perWidth;
                                Bitmap child = Bitmap.createBitmap(bitmap, startX, startY, perWidth, perHeight);
                                int size = child.getWidth() * child.getHeight() * 4;
                                ByteArrayOutputStream out = new ByteArrayOutputStream(size);
                                child.compress(Bitmap.CompressFormat.PNG, 100, out);
                                try {
                                    out.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        out.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                byte[] arrays = out.toByteArray();
                                bitmappath.add(arrays);
                                rIndex++;
                            }
                        }

                        service.insert(DBType.SOURCE_BITMAP, img, bitmappath);
                    }
                }
            }
        });
        return null;
    }

    /**
     * 获取Bitmap的流
     *
     * @param bitmap
     * @return
     */
    public static byte[] getBitmapArrays(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return out.toByteArray();
    }

    /**
     * 获取本地资源的Bitmap
     *
     * @param context
     * @param size
     * @return
     */
    public static Drawable getDrawableBitmap(Context context, int id) {
        Drawable drawable = context.getResources().getDrawable(id);
        drawable.mutate();
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        drawable.setColorFilter(colorFilter);
        ;
        return drawable;
    }

    /**
     * 将int转为byte数组
     *
     * @param data
     * @return
     */
    public static byte[] intToBytes(int data) {
        byte[] src = new byte[2];
        src[0] = (byte) (data >> 8 & 0xFF);
        ;
        src[1] = (byte) (data & 0xFF);
        return src;
    }

    public static byte[] audioToByteArray(ByteArrayInputStream audio){
        try {
            return IOUtils.toByteArray(audio);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] audioToByteArray(InputStream audio) throws IOException {
        return IOUtils.toByteArray(audio);
    }

    /**
     * 将路径文件转为byte[]
     *
     * @return 二进制文件数据
     */
    public static byte[] readFileAsBytes(File filename) throws IOException {
        return FileUtils.readFileToByteArray(filename);
    }

    /**
     * 刷新本地相册
     *
     * @param activity
     * @param path
     */
    public static void notifyLocalAlbum(Context activity, String path) {
        Uri localUri = Uri.fromFile(new File(SFileUtils.SOURCE_PATH));
        Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Logs.i("SFIle:" + SFileUtils.SOURCE_PATH);
        localIntent.setData(localUri);
        activity.sendBroadcast(localIntent);
        ContentValues localContentValues = new ContentValues();

        localContentValues.put("_data", path);

        localContentValues.put("description", "save image ---");

        localContentValues.put("mime_type", "image/jpeg");

        ContentResolver localContentResolver = context.getContentResolver();

        Uri local = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        localContentResolver.insert(local, localContentValues);
        // activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }

    /**
     * 判断List是否为空
     * @param list
     * @return
     */
    public static boolean isEmptyArrays(List list){
        return list==null || list.isEmpty();
    }

    public enum NetState {
        BROKEN, WIFI, MOBILE;
    }

}

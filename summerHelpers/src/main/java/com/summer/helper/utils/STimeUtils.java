package com.summer.helper.utils;

import android.content.Context;

import com.malata.summer.helper.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 有关时间的方法
 * Created by xiastars on 2017/8/10.
 */

public class STimeUtils {

    private static SimpleDateFormat mSimpleDateFormat;

    /**
     * 获取前后六个月
     *
     * @return
     */
    public static String[] getMonthAboutSix() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月", Locale.CHINA);
        String[] titles = new String[12];
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        for (int i = 5; i > -1; i--) {
            calendar.add(Calendar.MONTH, -1);
            titles[i] = format.format(calendar.getTimeInMillis());
        }
        calendar = Calendar.getInstance();
        for (int i = 0; i < 6; i++) {
            calendar.add(Calendar.MONTH, 1);
            titles[i + 6] = format.format(calendar.getTimeInMillis());
        }
        return titles;
    }

    /**
     * 获取上一个月
     *
     * @return
     */
    public static int[] getPreMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        return getYearAndMonth(calendar);
    }

    private static int[] getYearAndMonth(Calendar calendar) {
        int[] datas = new int[2];
        datas[0] = calendar.get(Calendar.YEAR);
        datas[1] = calendar.get(Calendar.MONTH);
        return datas;
    }

    /**
     * 单独获取年月日
     *
     * @param time
     * @return
     */
    public static int[] getYearMonthDay(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        return getYearMonthDay(calendar);
    }

    private static int[] getYearMonthDay(Calendar calendar) {
        int[] datas = new int[3];
        datas[0] = calendar.get(Calendar.YEAR);
        datas[1] = calendar.get(Calendar.MONTH) + 1;
        datas[2] = calendar.get(Calendar.DAY_OF_MONTH);
        return datas;
    }

    /**
     * 获取下一个月
     *
     * @return
     */
    public static int[] getNextMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        return getYearAndMonth(calendar);
    }

    /**
     * 获取下一个月
     *
     * @return
     */
    public static int[] getCurYearAndMonth() {
        Calendar calendar = Calendar.getInstance();
        return getYearAndMonth(calendar);
    }

    /**
     * 转换时间，格式--
     *
     * @return
     */
    public static String getDayWithFormat(String formatContent) {
        SimpleDateFormat format = new SimpleDateFormat(formatContent, Locale.CHINA);
        String s = format.format(new Date());
        return s;
    }

    /**
     * 转换时间，格式--
     *
     * @return
     */
    public static String getDayWithFormat(String formatContent, Date date) {
        SimpleDateFormat format = new SimpleDateFormat(formatContent, Locale.CHINA);
        String s = format.format(date);
        return s;
    }

    /**
     * 转换时间，格式--
     *
     * @return
     */
    public static String getDayWithFormat(String formatContent, long date) {
        if(date < 10000000000L){
            date = date * 1000;
        }
        return getDayWithFormat(formatContent, new Date(date));
    }

    /**
     * 获取当天星期
     *
     * @return
     */
    public static String getWeekDayXQ(long time) {
        String mWeekDay = "";
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(new Date(time));
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

    public static String getDayBreak( long timestamp) {
        if (timestamp < 10000000000L) {
            timestamp = timestamp * 1000;
        }
        long dividetime = System.currentTimeMillis()- timestamp;
        long days = dividetime/1000/60/60/24;
        return (days+1)+"";
    }

    /**
     * 时间规则：
     * 先判断日期，再判断时间差距。
     * <1> 如果日期是今天，
     * 如果距现在15分钟内，显示为：“刚刚”
     * 如果距现在15-60分钟内，显示为：“XX分钟前”“15分钟前”
     * 如果距现在60分钟以上，显示为：“XX小时前”“1小时前”
     * <2> 如果日期是昨天，显示为：“昨天”
     * <3> 如果日期是昨天之前，显示为：“XXXX年X月XX日XX时XX分” 例如“2017-06-15 12：15”
     */
    public static String parseTimeToHxqString(Context context, long timestamp) {
        if(timestamp < 10000000000L){
            timestamp = timestamp * 1000;
        }
        String str;
        Calendar cur = Calendar.getInstance();
        Calendar ct = Calendar.getInstance();
        ct.setTimeInMillis(timestamp);
        if (cur.get(Calendar.YEAR) == (ct.get(Calendar.YEAR))) {
            int diffDay = cur.get(Calendar.DAY_OF_YEAR)
                    - ct.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                long diff = cur.getTimeInMillis() - timestamp;
                if (diff < 60000 * 15) {
                    str = context.getString(R.string.label_just);
                } else if (diff < 60000 * 60) {
                    str = String.format(context.getString(R.string.label_before_minutes), diff / 60000);
                } else {
                    str = String.format(context.getString(R.string.label_before_hours), diff / (60000 * 60));
                }
            } else if (diffDay == 1) {
                str = context.getString(R.string.label_yesterday);
            } else {
                str = STimeUtils.getDayWithFormat("yyyy-MM-dd HH:mm", timestamp);
            }
        } else {
            str = STimeUtils.getDayWithFormat("yyyy-MM-dd HH:mm", timestamp);
        }
        return str;
    }

    /**
     * 时间规则：
     * 先判断日期，再判断时间差距。
     * <1> 如果日期是今天，
     * 如果距现在15分钟内，显示为：“刚刚”
     * 如果距现在15-60分钟内，显示为：“XX分钟前”“15分钟前”
     * 如果距现在60分钟以上，显示为：“XX小时前”“1小时前”
     * <2> 如果日期是昨天，显示为：“昨天”
     * <3> 如果日期是昨天之前，显示为：“XXXX年X月XX日XX时XX分” 例如“2017-06-15 12：15”
     */
    public static String parseChatTime(long timestamp) {
        if(timestamp < 10000000000L){
            timestamp = timestamp * 1000;
        }
        String str;
        Calendar cur = Calendar.getInstance();
        Calendar ct = Calendar.getInstance();
        ct.setTimeInMillis(timestamp);
        if (cur.get(Calendar.YEAR) == (ct.get(Calendar.YEAR))) {
            int diffDay = cur.get(Calendar.DAY_OF_YEAR)
                    - ct.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                str = STimeUtils.getDayWithFormat("HH:mm", timestamp);
            } else if (diffDay == 1) {
                str = STimeUtils.getDayWithFormat("MM月dd日 HH:mm", timestamp);
            } else {
                str = STimeUtils.getDayWithFormat("MM月dd日 HH:mm", timestamp);
            }
        } else {
            str = STimeUtils.getDayWithFormat("MM月dd日 HH:mm", timestamp);
        }
        return str;
    }


    public static String getWeekDayZhou(long time) {
        String mWeekDay;
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(new Date(time));
        int index = calendar.get(Calendar.DAY_OF_WEEK);
        switch (index) {
            case 1:
                mWeekDay = "周日";
                break;
            case 2:
                mWeekDay = "周一";
                break;
            case 3:
                mWeekDay = "周二";
                break;
            case 4:
                mWeekDay = "周三";
                break;
            case 5:
                mWeekDay = "周四";
                break;
            case 6:
                mWeekDay = "周五";
                break;
            case 7:
                mWeekDay = "周六";
                break;
            default:
                mWeekDay = "周日";
                break;
        }
        calendar.clear();
        return mWeekDay;
    }

    /**
     * 将String格式转换成Long格式时间
     *
     * @param strTime
     * @param formatType
     * @return
     * @throws ParseException
     */
    public static long parseStringDate(String strTime, String formatType) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        try {
            date = formatter.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return 0;
        } else {
            long currentTime = date.getTime(); // date类型转成long类型
            return currentTime;
        }
    }

    public static String getOverTimeFull(long startTime) {
        try {
            long l = startTime;
            long day = l / (24 * 60 * 60 * 1000);
            long hour = (l / (60 * 60 * 1000) - day * 24);
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            String str = "";
            String st = s + "";
            String shour = hour + "";
            String smin = min + "";
            if (hour > 0) {
                str = shour + "小时" + smin + "分" + st + "秒";
            } else {
                if (min > 0) {
                    str = shour + "小时" + smin + "分" + st + "秒";
                } else if (s > 0) {
                    str = shour + "小时" + st + "秒";
                }
            }
            if (day > 0) {
                str = day + "天" + str;
            }
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0小时0分0秒";
    }

    /**
     * 返回剩下时间
     *
     * @return
     */
    public static String getOverTimeString(long startTime) {
        return getOverTimeString(startTime, true);
    }

    public static String getOverTimeString(long startTime, boolean showHour) {
        try {
            long l = startTime;
            long day = l / (24 * 60 * 60 * 1000);
            long hour = (l / (60 * 60 * 1000) - day * 24);
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            String str = "00:00:00";
            if (!showHour) {
                str = "00:00";
            }
            String st = s + "";
            if (s < 10) {
                st = "0" + s;
            }
            String shour = hour + "";
            if (hour < 10) {
                shour = "0" + shour;
            }
            String smin = min + "";
            if (min < 10) {
                smin = "0" + smin;
            }
            if (hour > 0) {
                str = shour + ":" + smin + ":" + st;
                if (!showHour) {
                    str = smin + ":" + st;
                }
            } else {
                if (min > 0) {
                    str = shour + ":" + smin + ":" + st;
                    if (!showHour) {
                        str = smin + ":" + st;
                    }
                } else if (s > 0) {
                    str = shour + ":00:" + st;
                    if (!showHour) {
                        str = "00:" + st;
                    }
                }
            }
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "00:00:00";
    }

    /**
     * 判断是否为今天
     *
     * @param dateStr 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     */
    public static boolean isToday(String dateStr) {
        Date date = null;
        try {
            date = getSimpleDateFormat().parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date!=null ? isToday(date) : false;
    }

    /**
     * 判断是否为今天
     *
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean isToday(Date date) {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    public static SimpleDateFormat getSimpleDateFormat(){
        if (mSimpleDateFormat == null)
            mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return mSimpleDateFormat;
    }
}

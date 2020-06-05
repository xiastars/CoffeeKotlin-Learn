package com.summer.demo.utils

import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

import android.os.Environment
import android.util.Log

object CommonUtils {
    /* 本软件的存储路径 */
    val SDPATH = "$sdPath/summer"

    /**
     * 获取SD路径
     */
    // 判断sd卡是否存在
    // 获取跟目录
    val sdPath: String
        get() {
            var sdDir: File? = null
            try {
                val sdCardExist = Environment
                        .getExternalStorageState() == Environment.MEDIA_MOUNTED
                if (sdCardExist) {
                    sdDir = Environment
                            .getExternalStorageDirectory()
                } else {
                    val file = File(Environment.getDataDirectory().path + "/SUMMER")
                    if (!file.exists()) {
                        file.mkdir()
                    }
                    return if (file.canRead()) {
                        file.toString()
                    } else {
                        ""
                    }
                }
                if (sdDir != null) {
                    return sdDir.toString()
                }
            } catch (e: Exception) {
                Log.e("Error", e.message)
            }

            return ""
        }

    /**
     * 计算星座
     *
     * @param birth传入生日
     * ，格式：1900-00-00
     * @return
     */
    fun getSign(birth: String): String {
        var birth = birth
        if (birth.length != 10) {
            val format = SimpleDateFormat("yy-MM-dd")
            birth = format.format(Date())
        }
        val month = Integer.valueOf(birth.substring(5, 7))
        val day = Integer.valueOf(birth.substring(8, 10))
        val s = "魔羯水瓶双鱼白羊金牛双子巨蟹狮子处女天秤天蝎射手魔羯"
        val arr = arrayOf(20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22)
        val num = month * 2 - if (day < arr[month - 1]) 2 else 0
        return s.substring(num, num + 2) + "座"
    }

    /**
     * 计算用户的年龄
     *
     * @param birthDay 传入的生日，格式为1989-07-07
     * @return
     * @throws Exception
     */
    fun getAge(birthDay: String): Int {
        /* 当传入的格式不正确时，就设置为0岁 */
        if (birthDay.length != 10) {
            return 0
        }
        val cal = Calendar.getInstance()

        val year = Integer.valueOf(birthDay.substring(0, 4))
        val month = Integer.valueOf(birthDay.substring(5, 7))
        val day = Integer.valueOf(birthDay.substring(8, 10))

        val yearBirth = cal.get(Calendar.YEAR)
        val monthBirth = cal.get(Calendar.MONTH)
        val dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH)
        /* 年龄 = 当前的年份 - 设置的年份*/
        var age = yearBirth - year
        /*
         * 情况一：当月份与当前月份相同，如果设置的天小于今天，则减一岁
         * 情况二：当月份小于当前月份时，直接减一岁
         */
        if (month <= monthBirth) {
            if (month == monthBirth) {
                if (day < dayOfMonthBirth)
                    age--
            } else {
                age--
            }
        }
        return age
    }

    /**
     * 将时间转换成1900-00-00这种格式
     * @param year
     * @param month
     * @param day
     * @return
     */
    fun properBirth(year: Int, monthOfYear: Int, dayOfMonth: Int): String {
        val month: String
        val day: String
        /*当月数小于10时在前面+0，因为月数会自动+1，所以判断<9就可以了*/
        if (monthOfYear < 9) {
            month = "0" + Integer.toString(monthOfYear + 1)
        } else {
            month = Integer.toString(monthOfYear + 1)
        }

        if (dayOfMonth < 10) {
            day = "0" + Integer.toString(dayOfMonth)
        } else {
            day = Integer.toString(dayOfMonth)
        }
        return "$year-$month-$day"
    }

}

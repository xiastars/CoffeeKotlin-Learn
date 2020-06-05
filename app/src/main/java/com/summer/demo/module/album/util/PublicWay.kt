package com.summer.demo.module.album.util

import android.app.Activity
import java.util.*

/**
 * 存放所有的list在最后退出时一起关闭
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日  下午11:50:49
 */
object PublicWay {
    var activityList: MutableList<Activity> = ArrayList()

    var MAX_SELECT_COUNT = 9

}

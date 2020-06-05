package com.summer.demo.module.video.util

import java.io.Serializable

/**
 * ================================================
 * 作    者：顾修忠-guxiuzhong@youku.com/gfj19900401@163.com
 * 版    本：
 * 创建日期：2017/3/2-下午8:52
 * 描    述：
 * 修订历史：
 * ================================================
 */

class VideoEditInfo : Serializable {

    var path: String? = null //图片的sd卡路径
    var time: Long = 0//图片所在视频的时间  毫秒


    override fun toString(): String {
        return "VideoEditInfo{" +
                "path='" + path + '\''.toString() +
                ", time='" + time + '\''.toString() +
                '}'.toString()
    }
}

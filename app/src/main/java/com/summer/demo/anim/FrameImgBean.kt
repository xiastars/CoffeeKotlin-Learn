package com.summer.demo.anim

import android.graphics.Bitmap

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/6/11 17:43
 */
class FrameImgBean {

    var imgType: Int = 0//0是本地资源，1是网络链接，2是asset资源
    var imgName: String? = null
    var bitmap: Bitmap? = null
    var createTime: Long = 0//Bitmap创建时间
}

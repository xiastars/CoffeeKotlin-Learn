package com.summer.demo.ui.module.bean

import java.io.Serializable

/**
 *
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2020/6/5 9:51
 */

class MovieInfo:Serializable{
    var rate :Float = 0.0f
    lateinit var title:String
    lateinit var cover:String
}
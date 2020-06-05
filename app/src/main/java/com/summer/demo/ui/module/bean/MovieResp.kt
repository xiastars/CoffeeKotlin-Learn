package com.summer.demo.ui.module.bean

import com.summer.demo.bean.BaseResp
import kotlin.properties.Delegates

/**
 * 读取豆瓣电影的接口数据，这里把数据做了特殊处理以满足框架
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2020/6/5 10:44
 */

class MovieResp : BaseResp() {
    var subjects: MutableList<MovieInfo> by Delegates.observable(mutableListOf<MovieInfo>()) {

        property, oldValue, newValue ->
        info = subjects
        isResult = true
        error = 0

    }

}
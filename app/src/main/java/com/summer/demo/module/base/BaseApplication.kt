package com.summer.demo.module.base

import android.app.Application
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.summer.demo.R
import com.summer.helper.server.EasyHttp
import com.summer.helper.utils.SUtils

/**
 * @Description: 基本Application
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/17 11:32
 */
open class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initDefault()
    }

    private fun initDefault() {
        SUtils.setContext(this)
        EasyHttp.init(this)
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.blue_56, android.R.color.white)//全局设置主题颜色
            ClassicsHeader(context)//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            //指定为经典Footer，默认是 BallPulseFooter
            ClassicsFooter(context).setDrawableSize(20f)
        }
    }
}

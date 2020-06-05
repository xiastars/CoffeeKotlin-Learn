package com.summer.demo

import android.content.Context
import android.content.pm.PackageManager
import android.os.StrictMode
import android.text.TextUtils
import com.summer.demo.constant.SharePreConst
import com.summer.demo.helper.FFMepgHelper
import com.summer.demo.module.base.BaseApplication
import com.summer.helper.server.PostData
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SFileUtils
import com.summer.helper.utils.SThread
import com.summer.helper.utils.SUtils
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mm.opensdk.openapi.IWXAPI
import java.util.*


class AppContext : BaseApplication() {
    var wxApi: IWXAPI? = null
        internal set

    /**
     * 微信是否安装
     *
     * @return
     */
    val isWxInstall: Boolean
        get() = if (wxApi != null) {
            wxApi!!.isWXAppInstalled
        } else false

    var imageIds: List<String> = ArrayList()


    var myUserId: String? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        Logs.isDebug = DEBUGMODE

        initApp()
        initUMLib()
        if (DEBUGMODE) {
            SERVER_MODE = SUtils.getIntegerData(this, "server_mode")
        }
        setDefault()
    }

    private fun setDefault() {

    }

    fun initAll() {


        //true:代表捕捉错误 false:代表不捕捉  平常写代码设置为false  提交测试置为true
        CrashReport.initCrashReport(applicationContext, "f258763397", true)
        //Bugly.init(this, "f258763397", false);
        initJPushAndShare()

        initCacheAndFFmeg()


    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // you must install multiDex whatever tinker is installed!
        // 安装tinker
        //Beta.installTinker();
    }

    private fun initCacheAndFFmeg() {
        SThread.getIntances().submit {
            SFileUtils.initCache(instance)//初始化文件缓存
            FFMepgHelper.initFFMepg()
        }
    }

    /**
     * 读取本地channel，只需一次
     */
    private fun readLocalChannel() {
        PostData.CHANNEL = SUtils.getStringData(this, SharePreConst.READ_LOCAL_CHANNEL)
        Logs.i("CHANNEL:" + PostData.CHANNEL)
        if (!TextUtils.isEmpty(PostData.CHANNEL)) {
            return
        }
        try {
            val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            if (appInfo.metaData != null) {
                PostData.CHANNEL = appInfo.metaData.getString("JPUSH_CHANNEL")
                SUtils.saveStringData(this, SharePreConst.READ_LOCAL_CHANNEL, PostData.CHANNEL)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }


    private fun initApp() {
        SFileUtils.DIR_NAME = "SummerDemo"
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()

    }

    fun initJPushAndShare() {
        //JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        //JPushInterface.init(this);

    }

    fun initUMLib() {

    }

    /**
     * 取消微信授权
     */
    fun unregisterWxApi() {
        if (wxApi != null) {
            wxApi!!.unregisterApp()
        }
    }

    companion object {

        var instance: AppContext? = null
            private set

        /**
         * 切换服务器
         */
        var SERVER_MODE = 2//0.是开发，1是测试，2是正式，3是预发布
        val DEBUGMODE = true
        val DEFAULT_TOKEN = ""//444392d5387d9415a5d8e7370b96645f
        val WEIXIN_ID = ""//微信KEY
        val TEXT_TIME = "2018.11.12 - 15：12"
    }
}
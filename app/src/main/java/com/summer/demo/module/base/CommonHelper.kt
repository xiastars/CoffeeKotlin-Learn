package com.summer.demo.module.base

import android.content.Context
import android.os.Handler
import android.os.Message

import com.summer.helper.listener.OnReturnDataResultListener
import com.summer.helper.server.SummerParameter

import java.lang.ref.WeakReference

/**
 * Created by xiastars on 2017/10/31.
 */

abstract class CommonHelper(protected var context: Context) {
    protected var baseHelper: BaseHelper
    protected var myHandler: MyHandler
    internal var listener: OnReturnDataResultListener? = null


    init {
        myHandler = MyHandler(this)
        baseHelper = BaseHelper(context, myHandler)
    }

    inner class MyHandler(activity: CommonHelper) : Handler() {
        private val mActivity: WeakReference<CommonHelper>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity = mActivity.get()
            if (null != activity) {
                activity.baseHelper.cancelLoading()
                when (msg.what) {
                    BaseHelper.MSG_SUCCEED -> {
                        activity.dealDatas(msg.arg1, msg.obj)
                        if (activity.listener != null) {
                            activity.listener!!.onClick(msg.arg1, msg.obj)
                        }
                    }
                    BaseHelper.MSG_CACHE -> {
                        activity.dealDatas(msg.arg1, msg.obj)
                        if (activity.listener != null) {
                            activity.listener!!.onClick(msg.arg1, msg.obj)
                        }
                    }
                    BaseHelper.MSG_ERRO -> {
                        activity.baseHelper.cancelLoading()
                        activity.dealErrors(msg.arg1, msg.arg2.toString() + "", msg.obj as String, false)
                    }
                    else -> activity.handleMsg(msg.what, msg.obj)
                }
            }
        }
    }

    fun requestData(requestCode: Int, className: Class<*>, params: SummerParameter, url: String, post: Boolean) {
        baseHelper.requestData(requestCode, className, params, url, post)
    }

    fun setListener(listener: OnReturnDataResultListener) {
        this.listener = listener
    }

    protected abstract fun handleMsg(position: Int, `object`: Any)

    /**
     * 处理返回的数据
     */
    protected abstract fun dealDatas(requestCode: Int, obj: Any)

    protected abstract fun dealErrors(requstCode: Int, requestType: String, errString: String, requestCode: Boolean)
}

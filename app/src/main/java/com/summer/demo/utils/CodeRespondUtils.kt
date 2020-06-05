package com.summer.demo.utils

import android.app.Activity
import android.content.Context
import android.text.TextUtils

import com.summer.demo.bean.BaseResp
import com.summer.helper.utils.SUtils

/**
 * 根据返回状态码弹出相应提示
 *
 * @author xiastars@vip.qq.com
 */
class CodeRespondUtils(internal var context: Context, baseResp: BaseResp?) {
    internal var msg: String

    init {
        msg = baseResp!!.msg
        (context as Activity).runOnUiThread { dealWithCode(context, baseResp.error) }
    }

    private fun dealWithCode(context: Context, code: Int) {
        var toastMsg = "操作失败，请稍后重试！"
        if (!TextUtils.isEmpty(msg)) {
            toastMsg = msg
        }
        /*        if(code == 40219){//绑定手机
            BaseUtils.needPhoneCheck(context);
            return;
        }else if(40101 == code){
            FSXQSharedPreference.getInstance().setTokenEable("");
            context.sendBroadcast(new Intent(BroadConst.AFTER_LOGIN_REFRESH_ALL));
            BaseUtils.showLoginDialog(context);
            return;
        }*/
        SUtils.makeToast(context, toastMsg)
    }
}

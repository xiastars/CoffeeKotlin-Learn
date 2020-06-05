package com.summer.demo.dialog

import android.app.Dialog
import android.content.Context
import com.summer.demo.R

/**
 * 加载进度条
 * @author xiastars@vip.qq.com
 */
class LoadingDialog(context: Context?) {
    var dialog: Dialog?
    fun cancelLoading() {
        if (dialog != null) {
            dialog!!.cancel()
        }
    }

    fun startLoading() {
        if (dialog != null) {
            dialog!!.show()
        }
    }

    init {
        dialog = Dialog(context, R.style.TagFullScreenDialog)
        dialog!!.setContentView(R.layout.dialog_loading1)
        //是否点击空白处关掉Dialog,一般设置为true
        dialog!!.setCanceledOnTouchOutside(true)
    }
}
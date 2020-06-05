package com.summer.demo.view

import android.app.Activity
import android.app.Dialog
import com.summer.demo.AppContext
import com.summer.demo.R
import com.summer.demo.helper.DialogHelper

object LoadingDialog {

    /**
     * 加载数据对话框
     */
    private var mLoadingDialog: Dialog? = null


    /**
     * 显示加载对话框
     *
     * @param context    上下文
     * @param msg        对话框显示内容
     * @param cancelable 对话框是否可以取消
     */
    @JvmOverloads
    fun showDialogForLoading(context: Activity, msg: String = AppContext.instance!!.getString(R.string.loading), cancelable: Boolean = true): Dialog {

        mLoadingDialog = DialogHelper.getProgressDialog(context, msg, cancelable)

        mLoadingDialog!!.setCancelable(cancelable)
        try {
            mLoadingDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return mLoadingDialog as Dialog
    }

    /**
     * 关闭加载对话框
     */
    fun cancelDialogForLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog!!.cancel()
        }
    }
}

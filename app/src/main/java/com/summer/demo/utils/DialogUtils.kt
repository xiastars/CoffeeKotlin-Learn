package com.summer.demo.utils

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

import com.summer.demo.R

object DialogUtils {

    /**
     * 用于给一段文字进行提示的Dialog
     */
    fun startConfirm(titleText: Int, context: Context, listener: DialogClickListener) {
        try {
            val mDialog = Dialog(context, R.style.MyDialog)
            mDialog.show()
            mDialog.setContentView(R.layout.dialog_common)
            val title = mDialog.findViewById<View>(R.id.title) as TextView
            title.setText(titleText)
            val llCancel = mDialog.findViewById<View>(R.id.ll_cancel) as LinearLayout
            /* 取消 */
            llCancel.setOnClickListener {
                mDialog.cancel()
                listener.doNegative()
            }
            /* 确定 */
            val llLogout = mDialog.findViewById<View>(R.id.ll_sure) as LinearLayout
            llLogout.setOnClickListener {
                listener.doPositive()
                mDialog.cancel()
            }
            mDialog.setCanceledOnTouchOutside(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    interface DialogClickListener {
        fun doPositive()
        fun doNegative()
    }

}

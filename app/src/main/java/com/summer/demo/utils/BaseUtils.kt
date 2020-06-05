package com.summer.demo.utils

import android.content.Context

import com.summer.demo.dialog.BaseTipsDialog

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/17 10:11
 */
object BaseUtils {

    /**
     * 显示简单的对话框
     *
     * @param context
     * @param content
     * @param listener
     */
    fun showEasyDialog(context: Context, content: String, listener: BaseTipsDialog.DialogAfterClickListener) {
        val baseTipsDialog = BaseTipsDialog(context, content, listener)
        baseTipsDialog.hideTitle()
        baseTipsDialog.show()
    }
}

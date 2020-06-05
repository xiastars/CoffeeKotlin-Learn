package com.summer.demo.ui.fragment.dialog

import android.content.Context
import android.view.View

import com.summer.demo.R
import com.summer.helper.dialog.BaseBottomDialog

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/6/17 17:19
 */
class BottomTestDialog(context: Context) : BaseBottomDialog(context) {

    override fun setContainerView(): Int {
        return R.layout.dialog_bottom_test
    }

    override fun initView(view: View) {

    }
}

package com.summer.helper.dialog

import android.content.Context
import com.malata.summer.helper.R

/**
 * 中间弹出框基本样式
 * Created by xiastars on 2017/6/20.
 */
abstract class BaseCenterDialog(context: Context) : BaseDialog(context) {

    override fun showEnterAnim(): Int {
        return R.anim.dialog_center_enter
    }

    override fun showQuitAnim(): Int {
        return R.anim.dialog_center_quit
    }
}
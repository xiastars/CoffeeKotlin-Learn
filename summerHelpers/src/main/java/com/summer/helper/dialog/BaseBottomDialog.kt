package com.summer.helper.dialog

import android.content.Context
import android.os.Bundle
import com.malata.summer.helper.R
import com.summer.helper.view.SRecycleView

/**
 * 底部弹出框基本样式
 * Created by xiaqiliang on 2017/6/20.
 */
abstract class BaseBottomDialog(context:Context) : BaseDialog(context) {
    protected var sRecycleView: SRecycleView? = null
    var pageIndex = 0
    protected var fromId: Long = 0
    var lastId: String? = null
    var isRefresh = false

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(true)
        setDialogBottom()
    }

    override fun showEnterAnim(): Int {
        return R.anim.slide_up
    }

    override fun showQuitAnim(): Int {
        return R.anim.slide_bottom
    }

    protected fun loadData() {}
}
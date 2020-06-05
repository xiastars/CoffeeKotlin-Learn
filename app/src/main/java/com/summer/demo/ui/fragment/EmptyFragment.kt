package com.summer.demo.ui.fragment

import android.view.View

import com.summer.demo.R
import com.summer.demo.module.base.BaseFragment
import com.summer.helper.utils.Logs

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/6/14 10:56
 */
class EmptyFragment : BaseFragment() {
    override fun initView(view: View) {
        Logs.i("view------------pause")
    }

    public override fun loadData() {

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.view_empty
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

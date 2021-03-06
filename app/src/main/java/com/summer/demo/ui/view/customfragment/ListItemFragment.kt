package com.summer.demo.ui.view.customfragment

import android.view.View
import com.summer.demo.R
import com.summer.demo.module.base.BaseFragment
import com.summer.helper.view.NRecycleView

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/10 16:38
 */
class ListItemFragment : BaseFragment() {
    internal val nvContainer: NRecycleView by Bind(R.id.sv_container)

    override fun initView(view: View) {
        nvContainer.setList()
    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.view_nrecyleview
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

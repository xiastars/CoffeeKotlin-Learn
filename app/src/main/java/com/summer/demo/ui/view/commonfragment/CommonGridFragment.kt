package com.summer.demo.ui.view.commonfragment

import android.view.View
import com.summer.demo.R
import com.summer.demo.adapter.CommonListAdapter
import com.summer.demo.module.base.BaseFragment
import com.summer.helper.view.NRecycleView

/**
 * 学习GridView静态数据的写法
 *
 * @author xiastars@vip.qq.com
 */
class CommonGridFragment : BaseFragment(), View.OnClickListener {
    private val svContainer: NRecycleView by Bind(R.id.sv_container)


    override fun initView(view: View) {
        //设置每行几列
        svContainer.setGridView(3)
        svContainer.setDivider()
        svContainer.adapter = CommonListAdapter(context!!)
    }

    public override fun loadData() {

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.view_nrecyleview
    }

    override fun onClick(v: View) {
        when (v.id) {

        }
    }

}
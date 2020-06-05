package com.summer.demo.ui.view

import android.view.View
import com.summer.demo.R
import com.summer.demo.adapter.CommonGridAdapter
import com.summer.demo.bean.ModuleInfo
import com.summer.demo.ui.main.BaseMainFragment
import com.summer.helper.listener.OnSimpleClickListener
import com.summer.helper.utils.JumpTo
import com.summer.helper.view.NRecycleView
import java.util.*

/**
 * @Description: 自定义View
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/9 10:29
 */
class CustomViewFragment : BaseMainFragment() {
    private val svContainer: NRecycleView by Bind(R.id.sv_container)

    internal lateinit var adapter: CommonGridAdapter

    override fun initView(view: View) {
        svContainer!!.setGridView(3)
        svContainer!!.setDivider()


        val moduleInfos = ArrayList<ModuleInfo>()
        moduleInfos.add(ModuleInfo(R.drawable.so_gradient_redffe_blued8, "item收集", ElementPosition.POS_ITEM))
        moduleInfos.add(ModuleInfo(R.drawable.ic_view_gallery, "Gallery", ElementPosition.POS))
        moduleInfos.add(ModuleInfo(R.drawable.ic_view_danmake, "简单弹幕", ElementPosition.DANMAKU))
        moduleInfos.add(ModuleInfo(R.drawable.ic_module_vibrate, "dd", ElementPosition.CAL))
        moduleInfos.add(ModuleInfo(R.drawable.ic_view_calendar, "日历(当月)", ElementPosition.CALENDAR))

        adapter = CommonGridAdapter(context!!, OnSimpleClickListener { position -> clickChild(moduleInfos[position].pos) })
        svContainer!!.adapter = adapter
        adapter.notifyDataChanged(moduleInfos)
    }

    private fun clickChild(position: Int) {
        JumpTo.getInstance().commonJump(getContext(), ViewCustomContainerActivity::class.java, position)
    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.view_nrecyleview
    }

    override fun onClick(v: View?) {

    }

}

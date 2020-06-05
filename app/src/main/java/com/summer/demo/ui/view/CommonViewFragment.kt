package com.summer.demo.ui.view

import android.view.View
import com.summer.demo.R
import com.summer.demo.adapter.CommonGridAdapter
import com.summer.demo.bean.ModuleInfo
import com.summer.demo.ui.FragmentContainerActivity
import com.summer.demo.ui.main.BaseMainFragment
import com.summer.helper.listener.OnSimpleClickListener
import com.summer.helper.utils.JumpTo
import com.summer.helper.view.NRecycleView
import java.util.*

/**
 * @Description: 普通View
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/9 10:29
 */
class CommonViewFragment : BaseMainFragment() {
    private val svContainer: NRecycleView by Bind(R.id.sv_container)

    internal lateinit var adapter: CommonGridAdapter

    override fun initView(view: View) {
        svContainer.setGridView(3)
        svContainer.setDivider()


        val moduleInfos = ArrayList<ModuleInfo>()
        moduleInfos.add(ModuleInfo(R.drawable.so_gradient_redffe_blued8, "Drawable", UiPosition.POS_DRAWABLE))
        moduleInfos.add(ModuleInfo(R.drawable.ic_view_text, "文本", UiPosition.POS_TEXT))
        moduleInfos.add(ModuleInfo(R.drawable.ic_view_danmake, "ConstarintLayout", UiPosition.POS_CONSTRAINT))
        moduleInfos.add(ModuleInfo(R.drawable.ic_view_list, "ListView", UiPosition.POS_LIST_REC))
        moduleInfos.add(ModuleInfo(R.drawable.ic_view_grid, "GridView", UiPosition.POS_GRID_REC))
        moduleInfos.add(ModuleInfo(R.drawable.ic_view_list, "可刷新List", UiPosition.POS_REFRESH_LIST))
        moduleInfos.add(ModuleInfo(R.drawable.ic_view_grid, "可刷新Grid", UiPosition.POS_REFRESH_GRID))
        moduleInfos.add(ModuleInfo(R.drawable.ic_view_calendar, "进度条", UiPosition.PROGRESS))

        adapter = CommonGridAdapter(context!!, OnSimpleClickListener { position -> clickChild(moduleInfos[position].pos) })
        svContainer.adapter = adapter
        adapter.notifyDataChanged(moduleInfos)
    }

    private fun clickChild(position: Int) {
        JumpTo.getInstance().commonJump(context!!, FragmentContainerActivity::class.java, position)
    }

    public override fun loadData() {

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

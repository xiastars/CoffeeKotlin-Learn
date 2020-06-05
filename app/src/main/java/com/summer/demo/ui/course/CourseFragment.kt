package com.summer.demo.ui.course

import android.view.View
import com.summer.demo.R
import com.summer.demo.adapter.CommonGridAdapter
import com.summer.demo.bean.ModuleInfo
import com.summer.demo.ui.course.calculation.CalculationActivity
import com.summer.demo.ui.main.BaseMainFragment
import com.summer.helper.listener.OnSimpleClickListener
import com.summer.helper.utils.JumpTo
import com.summer.helper.view.NRecycleView
import java.util.*

/**
 * @Description: 教程页面
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/12 11:01
 */
class CourseFragment : BaseMainFragment() {
    private val svContainer: NRecycleView by Bind(R.id.sv_container)
    internal lateinit var adapter: CommonGridAdapter

    override fun initView(view: View) {
        svContainer!!.setGridView(3)
        svContainer!!.setDivider()
        adapter = CommonGridAdapter(context!!, OnSimpleClickListener { position -> clickChild(position) })
        svContainer!!.adapter = adapter
        val moduleInfos = ArrayList<ModuleInfo>()
        moduleInfos.add(ModuleInfo(R.drawable.ic_course_computer, "常用工具", CoursePos.POS_TOOL))
        moduleInfos.add(ModuleInfo(R.drawable.ic_course_java, "JAVA基础", CoursePos.POS_JAVA))
        moduleInfos.add(ModuleInfo(R.drawable.ic_course_computer, "计算机网络", CoursePos.POS_NET))
        moduleInfos.add(ModuleInfo(R.drawable.ic_course_computer, "工程计算", CoursePos.POS_CALCULATION))
        adapter.notifyDataChanged(moduleInfos)
    }

    private fun clickChild(position: Int) {
        when (position) {
            CoursePos.POS_TOOL -> JumpTo.getInstance().commonJump(context, LearnJavaActivity::class.java, CoursePos.POS_TOOL)
            CoursePos.POS_JAVA -> JumpTo.getInstance().commonJump(context, LearnJavaActivity::class.java, CoursePos.POS_JAVA)
            CoursePos.POS_NET -> JumpTo.getInstance().commonJump(context, LearnNetActivity::class.java, CoursePos.POS_NET)
            CoursePos.POS_CALCULATION -> JumpTo.getInstance().commonJump(context, CalculationActivity::class.java)
            else -> JumpTo.getInstance().commonJump(context, LearnJavaActivity::class.java)
        }

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


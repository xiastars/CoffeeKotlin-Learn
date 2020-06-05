package com.summer.demo.ui.view.customfragment

import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.summer.demo.R
import com.summer.demo.module.base.BaseFragment
import com.summer.demo.ui.view.customfragment.calendar.CalendarView
import com.summer.helper.utils.STimeUtils

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/11/11 16:45
 */
class CalendarFragment : BaseFragment() {
    internal val rlContainer: RelativeLayout by Bind(R.id.rl_container)

    override fun initView(view: View) {
        val calendarView = CalendarView(context!!)
        calendarView.isClickable = true
        calendarView.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        rlContainer!!.addView(calendarView)
        val yearAndMonth = STimeUtils.getCurYearAndMonth()
        val curYear = yearAndMonth[0]
        val curMonth = yearAndMonth[1]
        loadData()
        calendarView.creatCells(yearAndMonth, curMonth)
        calendarView.invalidate()
        calendarView.requestLayout()
    }


    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.view_container
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

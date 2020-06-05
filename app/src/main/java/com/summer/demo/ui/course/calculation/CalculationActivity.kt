package com.summer.demo.ui.course.calculation

import android.content.Context
import android.view.View
import com.summer.demo.R
import com.summer.demo.ui.BaseTitleListActivity
import com.summer.helper.utils.Logs
import java.util.*

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/11/12 10:26
 */
class CalculationActivity : BaseTitleListActivity() {

    override fun initData() {
        super.initData()
        setTitle("工程计算")
    }


    override fun setData(): List<String> {
        return getData(context!!)
    }

    override fun clickChild(pos: Int) {
        when (pos) {
            0 -> showFragment(TrapezoidFragment())
        }
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        fun getData(context: Context): List<String> {
            val title = ArrayList<String>()
            /* 从XML里获取String数组的方法*/
            val group = context.resources.getStringArray(R.array.calculation)
            Logs.i("group:$group")
            for (i in group.indices) {
                val ti = group[i]
                title.add(ti)
            }
            return title
        }
    }

}

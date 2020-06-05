package com.summer.demo.ui

import android.content.Context
import android.view.View
import com.summer.demo.R
import com.summer.demo.ui.fragment.viewpager.LeftDotViewPager
import com.summer.demo.ui.fragment.views.TextViewFragment
import java.util.*

/**
 * @Description: 常见View的一些用法
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/6/14 16:19
 */
class AcViews : BaseTitleListActivity(), View.OnClickListener {

    override fun initData() {
        super.initData()

    }

    override fun setData(): List<String> {
        return getData(context!!)
    }

    override fun clickChild(pos: Int) {
        when (pos) {
            0 -> showFragment(TextViewFragment())
            1 -> showFragment(LeftDotViewPager.newInstance())
        }
    }

    override fun onClick(v: View) {
        when (v.id) {

        }
    }

    companion object {


        fun getData(context: Context): List<String> {
            val title = ArrayList<String>()
            /* 从XML里获取String数组的方法*/
            val group = context.resources.getStringArray(R.array.titl_view)
            for (i in group.indices) {
                val ti = group[i]
                title.add(ti)
            }
            return title
        }
    }

}

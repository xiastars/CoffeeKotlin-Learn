package com.summer.demo.ui

import android.content.Context
import android.view.View
import com.summer.demo.R
import com.summer.demo.ui.fragment.viewpager.CommonVPFragment
import com.summer.demo.ui.fragment.viewpager.LeftDotViewPager
import java.util.*


/**
 * ViewPager的一般用法
 *
 * @author xiastars@vip.qq.com
 */
class AcViewPager : BaseTitleListActivity(), View.OnClickListener {

    override fun setData(): List<String> {
        return getData(context!!)
    }

    override fun clickChild(pos: Int) {
        when (pos) {
            0 -> showFragment(CommonVPFragment.newInstance())
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
            val group = context.resources.getStringArray(R.array.title_viewpager)
            for (i in group.indices) {
                val ti = group[i]
                title.add(ti)
            }
            return title
        }
    }

}

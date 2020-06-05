package com.summer.demo.module.base

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by xiastars on 2017/8/7.
 */

class SFragmentPagerAdapter(fm: FragmentManager, private val fragmentList: List<Fragment>?, private var titleList: Array<String>?) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return fragmentList!![position]
    }

    fun setTitleList(titleList: Array<String>) {
        this.titleList = titleList
        notifyDataSetChanged()
    }


    override fun getCount(): Int {
        return fragmentList?.size ?: 0
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleList!![position]
    }
}

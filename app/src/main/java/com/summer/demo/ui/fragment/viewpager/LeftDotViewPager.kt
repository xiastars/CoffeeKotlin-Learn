package com.summer.demo.ui.fragment.viewpager

import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import com.summer.demo.R
import com.summer.demo.module.base.BaseFragment
import com.summer.demo.module.base.viewpager.NFPagerTabView
import com.summer.demo.module.base.viewpager.VFragmentPagerAdapter
import com.summer.demo.ui.fragment.EmptyFragment
import com.summer.demo.utils.CUtils
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SUtils
import com.summer.helper.view.CustomerViewPager
import com.summer.helper.view.PagerSlidingTabStrip
import java.util.*

/**
 * @Description: 标题栏自定义位置，标题栏有消息提醒
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/6/14 9:44
 */
class LeftDotViewPager : BaseFragment() {

    internal val pagerStrip: PagerSlidingTabStrip by Bind(R.id.pagerStrip)
    internal val viewPager: CustomerViewPager by Bind(R.id.viewPager)
    internal var fragments: MutableList<Fragment> = ArrayList()

    internal var currentPage = -1

    private var mTabViews: MutableList<NFPagerTabView>? = null

    override fun initView(view: View) {
        Logs.i("pause")
        Logs.i("pause" + Runtime.getRuntime().totalMemory() / 1024)
        pagerStrip!!.setPadding(0, 15, 0, 15)
        pagerStrip!!.setTabWidth(SUtils.getSWidth(activity, 15))
        fragments.add(EmptyFragment())
        fragments.add(EmptyFragment())
        fragments.add(EmptyFragment())
        mTabViews = ArrayList()
        pagerStrip!!.setAssitTextColor(getResColor(R.color.grey_cd))
        pagerStrip!!.indicatorColor = getResColor(R.color.transparent)
        pagerStrip!!.textColor = getResColor(R.color.grey_4a)
        mTabViews!!.add(NFPagerTabView(context, "天", 22))
        mTabViews!!.add(NFPagerTabView(context, "地", 22))
        mTabViews!!.add(NFPagerTabView(context, "人", 22))
        val adapter = VFragmentPagerAdapter(activity!!.supportFragmentManager, fragments, mTabViews)
        viewPager!!.adapter = adapter
        viewPager!!.offscreenPageLimit = fragments.size
        viewPager!!.currentItem = 0
        pagerStrip!!.setViewPager(viewPager)
        pagerStrip!!.setTabWidth(SUtils.getDip(context!!, 32))
        //埋点
        val clickMark = arrayOf("sky", "earth", "people")
        pagerStrip!!.setOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
                CUtils.onClick(context!!, clickMark[position])

            }
        })
        //显示新消息提醒
        showDots(0, true)
    }

    public override fun loadData() {
        if (pagerStrip == null) {
            return
        }

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun onPause() {
        super.onPause()
        //警告：Fragment里写Viewpager，Viewpager里的Fragment必须清除掉，否则下次进来，无法显示View
        for (i in fragments.indices) {
            activity!!.supportFragmentManager.beginTransaction().remove(fragments[i]).commit()
        }
    }

    override fun refresh() {
        pageIndex = 0
        lastId = null
        loadData()
    }

    private fun showDots(pos: Int, show: Boolean) {
        mTabViews!![pos].isShowIndicate = show
    }


    override fun setContentView(): Int {
        return R.layout.fragment_leftdot_viewpager
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        fun newInstance(): LeftDotViewPager {
            return LeftDotViewPager()
        }
    }
}


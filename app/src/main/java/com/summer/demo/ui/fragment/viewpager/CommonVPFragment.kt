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
 * @Description: 最普通的一个
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/6/14 9:44
 */
class CommonVPFragment : BaseFragment() {

    internal val pagerStrip: PagerSlidingTabStrip by Bind(R.id.pagerStrip)
    internal val viewPager: CustomerViewPager by Bind(R.id.viewPager)
    internal var fragments: MutableList<Fragment> = ArrayList()

    internal var currentPage = -1

    private var mTabViews: MutableList<NFPagerTabView>? = null

    override fun initView(view: View) {
        pagerStrip!!.setTabWidth(SUtils.getSWidth(activity, 15))
        Logs.i("-------" + EmptyFragment())
        fragments.add(EmptyFragment())
        fragments.add(EmptyFragment())
        mTabViews = ArrayList()
        //设置未选中的标题颜色
        pagerStrip!!.setAssitTextColor(getResColor(R.color.grey_cd))
        //底部滑动的线
        pagerStrip!!.indicatorColor = getResColor(R.color.red_d3)
        //底部滑动的线的高度
        pagerStrip!!.indicatorHeight = 1
        //中间的标题分割线
        pagerStrip!!.dividerColor = getResColor(R.color.blue_0a)
        //设置选中的标题颜色
        pagerStrip!!.textColor = getResColor(R.color.grey_4a)
        val homeTab = NFPagerTabView(context, "帧动画")
        homeTab.setmTextSize(22)
        mTabViews!!.add(homeTab)
        val trendsTab = NFPagerTabView(context, "属性动画")
        trendsTab.setmTextSize(22)
        mTabViews!!.add(trendsTab)
        val adapter = VFragmentPagerAdapter(activity!!.supportFragmentManager, fragments, mTabViews)
        viewPager!!.adapter = adapter
        viewPager!!.offscreenPageLimit = fragments.size
        viewPager!!.currentItem = 0
        pagerStrip!!.setViewPager(viewPager)
        pagerStrip!!.setTabWidth(SUtils.getDip(context!!, 32))
        //埋点
        val clickMark = arrayOf("frame_anim", "object_anim")
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
        return R.layout.fragment_common_viewpager
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        fun newInstance(): CommonVPFragment {
            return CommonVPFragment()
        }
    }
}


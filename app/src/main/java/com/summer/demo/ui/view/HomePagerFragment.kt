package com.summer.demo.ui.view

import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import com.summer.demo.R
import com.summer.demo.module.base.viewpager.NFPagerTabView
import com.summer.demo.module.base.viewpager.VFragmentPagerAdapter
import com.summer.demo.ui.main.BaseMainFragment
import com.summer.demo.utils.CUtils
import com.summer.helper.utils.SUtils
import com.summer.helper.view.CustomerViewPager
import com.summer.helper.view.PagerSlidingTabStrip
import java.util.*

/**
 * 首页两个页面 - 普通与自定义
 */
class HomePagerFragment : BaseMainFragment(), View.OnClickListener {

    private val pagerStrip:PagerSlidingTabStrip by Bind(R.id.pagerStrip)

    private val viewPager:CustomerViewPager by Bind(R.id.viewPager)


    internal var isFirstEnter: Boolean = false
    internal var currentPage = -1

    internal lateinit var mTabViews: MutableList<NFPagerTabView>
    internal var fragments: MutableList<Fragment> = ArrayList()

    override fun initView(view: View) {
        pagerStrip!!.setPadding(0, 15, 0, 15)

        fragments.add(CommonViewFragment())
        fragments.add(CustomViewFragment())
        mTabViews = ArrayList()
        //未选中时的文字颜色
        pagerStrip!!.setAssitTextColor(getResColor(R.color.grey_cd))
        pagerStrip!!.indicatorColor = getResColor(R.color.transparent)
        //选中时的文字颜色
        pagerStrip!!.textColor = getResColor(R.color.grey_4a)
        val homeTab = NFPagerTabView(getContext(), "视图")
        homeTab.setmTextSize(22)
        mTabViews.add(homeTab)
        val trendsTab = NFPagerTabView(getContext(), "组件")
        trendsTab.setmTextSize(22)
        mTabViews.add(trendsTab)
        val adapter = VFragmentPagerAdapter(getActivity()!!.supportFragmentManager, fragments, mTabViews)
        viewPager!!.adapter = adapter
        viewPager!!.offscreenPageLimit = fragments.size
        viewPager!!.currentItem = 0
        pagerStrip!!.setViewPager(viewPager)
        //单个Tab的宽度
        pagerStrip!!.setTabWidth(SUtils.getDip(getContext()!!, 52))
        //埋点
        val clickMark = arrayOf("homepager_home", "homepager_trends")
        pagerStrip!!.setOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position

                CUtils.onClick(context!!, clickMark[position])

            }
        })
        //显示新消息提示
        showDots(0, true)
    }

    public override fun loadData() {
        if (pagerStrip == null) {
            return
        }
    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.rl_search -> CUtils.onClick(context!!, "home_search")
        }//
    }

    override fun refresh() {

    }

    private fun showDots(pos: Int, show: Boolean) {
        mTabViews[pos].isShowIndicate = show
    }

    private fun getFragment(pos: Int): BaseMainFragment {
        return fragments[pos] as BaseMainFragment
    }

    override fun setContentView(): Int {
        return R.layout.fragment_home_pager
    }

    companion object {

        fun newInstance(): HomePagerFragment {
            return HomePagerFragment()
        }
    }

}

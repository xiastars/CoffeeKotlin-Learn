package com.summer.demo.ui.view

import com.summer.demo.ui.FragmentContainerActivity
import com.summer.demo.ui.view.customfragment.CalendarFragment
import com.summer.demo.ui.view.customfragment.DanmakuFragment
import com.summer.demo.ui.view.customfragment.GalleryFragment

/**
 * @Description: Fragment容器
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/9 11:44
 */
class ViewCustomContainerActivity : FragmentContainerActivity() {

    override fun showViews(type: Int) {
        when (type) {
            ElementPosition.POS_ITEM -> {
            }
            ElementPosition.POS -> {
                title = "Gallery"
                showFragment(GalleryFragment())
            }
            ElementPosition.DANMAKU -> {
                title = "弹幕"
                showFragment(DanmakuFragment())
            }
            ElementPosition.CAL -> title = "CAL"
            ElementPosition.CALENDAR -> {
                title = "日历"
                showFragment(CalendarFragment())
            }
        }//showFragment(new );
        //  showFragment(new CalCuWEbFragment());
    }
}

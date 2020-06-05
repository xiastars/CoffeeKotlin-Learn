package com.summer.demo.ui.main

import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentManager
import android.view.View
import android.view.animation.Animation
import android.widget.FrameLayout
import com.summer.demo.R
import com.summer.demo.dialog.LoadingDialog
import com.summer.demo.module.view.NavigationButton
import com.summer.demo.ui.course.CourseFragment
import com.summer.demo.ui.mine.MineFragment
import com.summer.demo.ui.module.ModuleFragment
import com.summer.demo.ui.view.HomePagerFragment
import com.summer.demo.utils.CUtils
import com.summer.helper.utils.Logs
import com.summer.helper.view.transformer.MoveAnimation
import java.util.*


/**
 * 主Fragment
 */
class MainFragment : BaseMainFragment(), View.OnClickListener {

    private val flUi: FrameLayout by Bind(R.id.fl_ui)
    private val flCourse: FrameLayout by Bind(R.id.fl_course)
    private val minLayout: FrameLayout by Bind(R.id.home_content2)

    private val flModule: FrameLayout by Bind(R.id.fl_module)
    private val navView: NavigationButton by Bind(R.id.nav_view,true)
    private val navItemDynamic: NavigationButton by Bind(R.id.nav_item_dynamic,true)
    private val nvItemExplore: NavigationButton by Bind(R.id.nav_item_explore,true)
    private val navItemMe: NavigationButton by Bind(R.id.nav_item_me,true)

    internal var frameLayouts: MutableList<FrameLayout>? = ArrayList()
    internal var fragments: MutableList<BaseMainFragment>? = ArrayList()
    private val homePagerFragment: HomePagerFragment by lazy {
         HomePagerFragment()

    }
    internal val moduleFragment: ModuleFragment by lazy{
         ModuleFragment()
    }
    internal val mineFragment: MineFragment by lazy{
        MineFragment()
    }
    internal val fgManager: FragmentManager by lazy{
         getActivity()!!.supportFragmentManager
    }

    internal var firstAnimDisable: Boolean = false//第一次游客跳发现不需要动画
    internal var firstShowLoading: Boolean = false

    override fun onClick(v: View?) {
        Logs.i("view:"+v)
        Logs.i("id:"+v!!.id)
        when (v!!.id) {
            R.id.nav_item_release ->
                //发表页面
                CUtils.onClick(context!!, "home_bottom_release")
            R.id.nav_item_explore -> switchToFragment(1)
            R.id.nav_view -> switchToFragment(0)
            R.id.nav_item_me -> switchToFragment(3)
            R.id.nav_item_dynamic -> switchToFragment(2)
        }//ReleaseTopicActivity.show(context, MarUser.DEFAULT_GROUP_ID);

    }

    override fun setContentView(): Int {
        return R.layout.fragment_nav
    }

    override fun initView(view: View) {
        initMainView()
    }

    public override fun loadData() {

    }

    private fun initMainView() {
        Logs.i(firstShowLoading.toString() + "vivo firstShowLoading" + getContext())
        if (!firstShowLoading) {
            firstShowLoading = true
            val loadingDialog = LoadingDialog(context)
            loadingDialog.startLoading()
            //给充足的时间让页面渲染
            myHandlder.postDelayed({ loadingDialog.cancelLoading() }, 1500)
        }

        navView.init(R.drawable.tab_icon_view,
                R.string.tab_view,
                HomePagerFragment::class.java)
        nvItemExplore.init(R.drawable.tab_icon_discover,
                R.string.tab_module,
                ModuleFragment::class.java)
        navItemDynamic.init(R.drawable.tab_icon_discover,
                R.string.tab_course,
                CourseFragment::class.java)
        navItemMe.init(R.drawable.tab_icon_mine,
                R.string.tab_mine,
                MineFragment::class.java)
        initFragment()
    }

    private fun initFragment() {

        frameLayouts!!.add(flUi)
        frameLayouts!!.add(flModule)
        frameLayouts!!.add(flCourse)
        frameLayouts!!.add(minLayout)

        fragments!!.add(homePagerFragment)

        fragments!!.add(moduleFragment)
        fragments!!.add(CourseFragment())
        fragments!!.add(mineFragment)
        for (i in fragments!!.indices) {
            try {
                val frament = fragments!![i]
                if (frament.isAdded) {

                    fgManager.beginTransaction().show(frament).commitAllowingStateLoss()
                } else {
                    fgManager.beginTransaction().add(frameLayouts!![i].id, frament).commitAllowingStateLoss()
                }

            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }

        }
        //1.3.2 游客跳到发现
        myHandlder.postDelayed({
            firstAnimDisable = true
            switchToFragment(0)
        }, 1000)
    }

    /**
     * 刷新单个Fragment数据
     */
    fun refreshTabData(position: Int) {
        if (fragments == null || fragments!!.size < position + 1) {
            return
        }
        val tabFragment = fragments!![position]
        tabFragment.refresh()
    }

    /**
     * 显示动态小红点
     *
     * @param isShowDot
     */
    fun showRedDot(isShowDot: Boolean) {
        navItemDynamic!!.showRedDot(isShowDot)
    }


    /**
     * 显示当前模块
     *
     * @param position
     */
    fun switchToFragment(position: Int) {
        Logs.i("---------------");
        (activity as MainActivity).setSmEnable(position == 3);
        if (position == -1) {
            return
        }
        onTabSelect(position)
        var anim = true
        if (position == curFragmentIndex || firstAnimDisable) {
            anim = false
        }
        firstAnimDisable = false
        if (fragments!!.size > curFragmentIndex && fragments!![curFragmentIndex] != null) {
            val direction = setDirection(position)
            setFragmentAnim(direction, View.GONE, false, anim)
        }

        val direction = setDirection(position)
        curFragmentIndex = position
        setFragmentAnim(direction, View.VISIBLE, true, anim)
    }

    private fun onTabSelect(position: Int) {
        navView!!.isSelected = if (position == 0) true else false
        nvItemExplore!!.isSelected = if (position == 1) true else false
        navItemDynamic!!.isSelected = if (position == 2) true else false
        navItemMe!!.isSelected = if (position == 3) true else false
    }


    /**
     * 为当前模块配置转场动画
     *
     * @param direction
     * @param visible
     * @param enter
     */
    private fun setFragmentAnim(direction: Int, visible: Int, enter: Boolean, anim: Boolean) {
        if (frameLayouts == null || curFragmentIndex >= frameLayouts!!.size) {
            return
        }
        val curLayout = frameLayouts!![curFragmentIndex]
        curLayout.visibility = View.VISIBLE
        if (!anim) {
            curLayout.visibility = visible
            return
        }
        val animation = MoveAnimation.create(direction, enter, 400)
        curLayout.animation = animation
        animation.start()
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                curLayout.visibility = visible
                if (enter) {
                    //fragments.get(curFragmentIndex).onShow();
                }
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }

    private fun setDirection(position: Int): Int {
        return if (curFragmentIndex != position) {
            if (curFragmentIndex > position) {
                MoveAnimation.RIGHT
            } else {
                MoveAnimation.LEFT
            }
        } else {
            -1
        }
    }

    override fun finishLoad() {

    }

    override fun dealDatas(requestCode: Int, obj: Any) {

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            //Jzvd.releaseAllVideos();
        }
    }

    companion object {
        var curFragmentIndex: Int = 0//当前所在位置
        fun show(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }

}


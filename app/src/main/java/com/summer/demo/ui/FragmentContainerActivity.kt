package com.summer.demo.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.FrameLayout
import com.summer.demo.R
import com.summer.demo.module.base.BaseFragmentActivity
import com.summer.demo.ui.fragment.views.TextViewFragment
import com.summer.demo.ui.view.UiPosition
import com.summer.demo.ui.view.commonfragment.*
import com.summer.demo.ui.view.customfragment.ProgressBarFragment
import com.summer.helper.utils.JumpTo

/**
 * @Description: Fragment容器
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/9 11:44
 */
open class FragmentContainerActivity : BaseFragmentActivity() {
    private val rlContainer: FrameLayout by Bind(R.id.rl_container)

    internal var mFragment: Fragment? = null
    internal val fragmentManager: FragmentManager by lazy {
        this.supportFragmentManager
    }

    override fun loadData() {

    }

    override fun finishLoad() {

    }

    override fun dealDatas(requestCode: Int, obj: Any) {

    }

    override fun setTitleId(): Int {
        return 0
    }

    override fun setContentView(): Int {
        return R.layout.activity_fragment_container
    }

    override fun initData() {
        val type = JumpTo.getInteger(this)
        showViews(type)
    }

    protected open fun showViews(type: Int) {
        when (type) {
            UiPosition.POS_DRAWABLE -> {
                title = "Drawable"
                showFragment(CDrawableFragment())
            }
            UiPosition.POS_TEXT -> {
                title = "文本"
                showFragment(TextViewFragment())
            }
            UiPosition.POS_CONSTRAINT -> {
                title = "ConstraintLayout"
                showFragment(ConstraintLayoutFragment())
            }
            UiPosition.POS_LIST_REC -> {
                title = "ListView"
                showFragment(CommonListFragment())
            }
            UiPosition.POS_GRID_REC -> {
                title = "GridView"
                showFragment(CommonGridFragment())
            }
            UiPosition.POS_REFRESH_LIST -> {
                title = "可刷新List"
                showFragment(ListRecyclerFragment())
            }
            UiPosition.POS_REFRESH_GRID -> {
                title = "可刷新Grid"
                showFragment(GridRecyclerFragment())
            }
            UiPosition.PROGRESS -> {
                title = "进度条"
                showFragment(ProgressBarFragment())
            }
        }
    }

    fun showFragment(fragment: Fragment) {
        //销毁已显示的Fragment
        removeFragment()
        beginTransation(fragment)
    }

    /**
     * 添加Fragment
     *
     * @param fragment
     */
    private fun beginTransation(fragment: Fragment) {
        mFragment = fragment
        rlContainer!!.visibility = View.VISIBLE
        fragmentManager.beginTransaction().add(R.id.rl_container, fragment).commit()
    }

    /**
     * 销毁Fragment最适用的方法是将它替换成一个空的
     */
    private fun removeFragment() {
        mFragment = null
        rlContainer.visibility = View.GONE
        val fragment = Fragment()
        fragmentManager.beginTransaction().replace(R.id.rl_container, fragment).commit()
    }


}

package com.summer.demo.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.KeyEvent
import android.view.View
import com.summer.demo.R
import com.summer.demo.adapter.CommonGridAdapter
import com.summer.demo.module.base.BaseActivity
import com.summer.demo.module.base.BaseFragment
import com.summer.helper.listener.OnSimpleClickListener
import com.summer.helper.utils.Logs
import com.summer.helper.view.NRecycleView
import java.util.*

/**
 * @Description:格子列表页面
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/6/10 18:00
 */
abstract class BaseGridListActivity : BaseActivity() {
    internal val nvContainer: NRecycleView by Bind(R.id.nv_container)
    internal lateinit var fragmentManager: FragmentManager
    /* 当前显示的Fragment */
    internal var mFragment: BaseFragment? = null

    internal lateinit var adapter: CommonGridAdapter

    override fun setTitleId(): Int {
        return 0
    }

    override fun setContentView(): Int {
        return R.layout.ac_main
    }

    override fun initData() {
        fragmentManager = this.supportFragmentManager
        nvContainer!!.setGridView(3)
        nvContainer!!.setDivider()
        adapter = CommonGridAdapter(context!!, OnSimpleClickListener { position -> clickChild(position) })
        nvContainer!!.adapter = adapter
    }

    protected fun setData(titles: Array<String>, bgs: IntArray) {
        val title = ArrayList<String>()
        /* 从XML里获取String数组的方法*/
        for (i in titles.indices) {
            val ti = titles[i]
            title.add(ti)
        }
        //adapter.notifyDataChanged(title);
    }


    override fun dealDatas(requestCode: Int, obj: Any) {

    }


    fun showFragment(fragment: BaseFragment) {
        //销毁已显示的Fragment
        removeFragment()
        beginTransation(fragment)
    }

    /**
     * 销毁Fragment最适用的方法是将它替换成一个空的
     */
    private fun removeFragment() {
        Logs.i("removeFragment" + mFragment!!)
        mFragment = null
        findViewById(R.id.ll_container)!!.visibility = View.GONE
        val fragment = Fragment()
        fragmentManager.beginTransaction().replace(R.id.ll_container, fragment).commit()
    }

    /**
     * 监听系统的返回键，当处于Fragment时，点击返回，则回到主界面
     *
     * @param keyCode
     * @param event
     * @return
     */
    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mFragment != null) {
                removeFragment()
                return true
            } else {
                finish()
            }
            return false
        }
        return false
    }

    /**
     * 添加Fragment
     *
     * @param fragment
     */
    private fun beginTransation(fragment: BaseFragment) {
        mFragment = fragment
        findViewById(R.id.ll_container)!!.visibility = View.VISIBLE
        fragmentManager.beginTransaction().add(R.id.ll_container, fragment).commit()
    }

    protected abstract fun clickChild(pos: Int)
}

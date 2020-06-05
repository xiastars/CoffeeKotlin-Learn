package com.summer.demo.ui.main

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.FrameLayout
import com.summer.demo.R
import com.summer.demo.constant.SharePreConst
import com.summer.demo.module.base.BaseFragmentActivity
import com.summer.demo.module.view.CustomViewAbove
import com.summer.demo.module.view.CustomViewBehind
import com.summer.demo.module.view.SlidingMenu
import com.summer.helper.permission.PermissionUtils
import com.summer.helper.utils.SUtils
import com.yanzhenjie.permission.Permission

/**
 * 主界面
 *
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/9 9:50
 */
class MainActivity : BaseFragmentActivity() {


    val llMenu: FrameLayout by Bind(R.id.ll_menu)
    val above: CustomViewBehind by Bind(R.id.above)
    val llHome: FrameLayout by Bind(R.id.ll_home)
    val behind: CustomViewAbove by Bind(R.id.behind)
    val sm:SlidingMenu by Bind(R.id.menu)
    val viewBg: View by Bind(R.id.view_bg)

    internal lateinit var fragmentManager: FragmentManager
    internal lateinit var menuFragment: MenuFragment
    internal lateinit var mainFragment: MainFragment
    internal var mFragment: BaseMainFragment? = null
    internal var isDrag: Boolean = false

    private var mBackPressedTime: Long = 0

    /**
     * 针对一些功能请求权限
     */
    private fun requestPermission() {
        PermissionUtils.rationRequestPermission(this, Permission.READ_EXTERNAL_STORAGE,
                Permission.WRITE_EXTERNAL_STORAGE,
                Permission.READ_PHONE_STATE)

    }


    override fun setTitleId(): Int {
        return 0
    }

    override fun setContentView(): Int {
        return R.layout.activity_home
    }

    override fun initData() {
        removeTitle()
        requestPermission()
        initMainView()
    }

    private fun initMainView() {
        fragmentManager = this.supportFragmentManager
        menuFragment = MenuFragment()
        mainFragment = MainFragment()
        showMenu(menuFragment)
        showHome(mainFragment)
        initSlidingMenu()
        sm.showContent()
    }

    private fun initSlidingMenu() {
        sm.setMenu(above)
        sm.setContent(behind)
        sm.mode = SlidingMenu.LEFT
        sm.setFadeDegree(0.2f)
        sm.touchModeAbove = SlidingMenu.TOUCHMODE_FULLSCREEN
        sm.setShadowWidthRes(R.dimen.shadow_width)
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset)
        sm.toggle()//动态断定主动封闭或开启SlidingMenu
        sm.showMenu()//显示SlidingMenu
        sm.showContent()//显示内容
        sm.setBackgroundColor(Color.WHITE)
        val transformer = object : SlidingMenu.CanvasTransformer {
            override fun transformCanvas(canvas: Canvas, percentOpen: Float) {
                setViewBgColor(percentOpen)
            }
        }
        sm!!.setBehindCanvasTransformer(transformer)

        var dragListener = object : SlidingMenu.OnMenuDragListener {
            override fun onDrag(x: Int, y: Int) {
                if (x > 0 && !isDrag) {
                    sm!!.setBehindWidth(SUtils.screenWidth)
                    isDrag = true
                } else if (x < 0 && isDrag) {
                    sm!!.setBehindOffsetRes(R.dimen.slidingmenu_offset)
                    isDrag = false
                }
            }
        }
        sm!!.setOnMenuDragListener(dragListener)
        toggleMenu()

    }

    fun setViewBgColor(percentOpen: Float) {
        viewBg!!.setBackgroundColor(Color.argb((percentOpen * 255 / 3).toInt(), 30, 30, 32))
    }

    fun toggleMenu() {
        sm!!.toggle()
    }

    fun toggleToHome() {
        sm!!.toggle()
        viewBg!!.setBackgroundColor(Color.TRANSPARENT)

    }

    fun setSmEnable(enable: Boolean) {
        sm!!.isSlidingEnabled = enable
    }

    /**
     * 添加Fragment
     *
     * @param fragment
     */
    private fun showMenu(fragment: BaseMainFragment) {
        mFragment = fragment
        fragmentManager.beginTransaction().add(R.id.ll_menu, fragment).commit()
    }

    /**
     * 添加Fragment
     *
     * @param fragment
     */
    private fun showHome(fragment: BaseMainFragment) {
        removeFragment()
        mFragment = fragment
        fragmentManager.beginTransaction().add(R.id.ll_home, fragment).commit()
    }


    /**
     * 销毁Fragment最适用的方法是将它替换成一个空的
     */
    private fun removeFragment() {
        mFragment = null
        val fragment = Fragment()
        fragmentManager.beginTransaction().replace(R.id.ll_home, fragment).commit()
    }

    override fun loadData() {

    }

    fun refreshMenuFragment() {
        menuFragment!!.refresh()
    }

    override fun finishLoad() {

    }

    override fun dealDatas(requestCode: Int, obj: Any) {

    }


    fun showTrendsDot(isShowDot: Boolean) {
        mainFragment.showRedDot(isShowDot)
    }


    override fun onBackPressed() {
        val isDoubleClick = SUtils.getBooleanData(context, SharePreConst.DOUBLE_CLICK)!!
        if (isDoubleClick) {
            val curTime = SystemClock.uptimeMillis()
            if (curTime - mBackPressedTime < 3 * 1000) {
                finish()
            } else {
                mBackPressedTime = curTime
                SUtils.makeToast(context, "再次点击退出应用！")
            }
        } else {
            finish()
        }
    }

    companion object {

        fun show(context: Context) {
            MainFragment.curFragmentIndex = 0
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
}


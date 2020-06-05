package com.summer.helper.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.malata.summer.helper.R
import com.summer.helper.utils.SUtils
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * 底部弹出框基本样式
 * Created by xiaqiliang on 2017/6/20.
 */
abstract class BaseDialog(context: Context, private var resStyle: Int = R.style.TagFullScreenDialog) : Dialog(context) {
    var flParent: FrameLayout? = null
    var rlParent: RelativeLayout? = null
    var parentRes by Delegates.observable(0) { _, _, newValue ->
        rlParent!!.setBackgroundResource(newValue)
    }
    private var isShowAnim = true
    lateinit var mView: View

    class Bind<V>(val id: Int, val shouldClick: Boolean = false) : ReadOnlyProperty<BaseDialog, V> {
        override fun getValue(thisRef: BaseDialog, property: KProperty<*>): V {
            var view: View = thisRef.mView.findViewById(id)
            if (shouldClick) {
                //view.setOnClickListener(thisRef)
            }
            return view as V
        }
    }

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_base)
        rlParent = findViewById<View>(R.id.rl_base_parent) as RelativeLayout
        rlParent!!.setOnClickListener {
            //onParentClick();
        }
        flParent = findViewById<View>(R.id.fl_parent) as FrameLayout
        val view = LayoutInflater.from(context).inflate(containerLayoutResId, null)
        view.isFocusable = true
        flParent!!.addView(view)
        initView(view)
    }

    protected fun onParentClick() {
        cancelDialog()
    }

    /**
     * 将对话框置在底部，而且宽度全屏
     */
    protected fun setDialogBottom() {
        val window = window
        if (window != null) {
            val lp = window.attributes
            lp.width = SUtils.screenWidth
            lp.gravity = Gravity.BOTTOM
        }
    }

    /**
     * 返回容器视图资源Id
     */
    protected val containerLayoutResId: Int
        protected get() = setContainerView()

    abstract fun setContainerView(): Int
    abstract fun initView(view: View?)
    override fun show() {
        try {
            super.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (!isShowAnim) {
            return
        }
        val animRes = showEnterAnim()
        if (animRes != 0) {
            val anim = AnimationUtils.loadAnimation(context,
                    animRes)
            flParent!!.startAnimation(anim)
        }
    }

    /**
     * 返回对应颜色
     *
     * @param colorRes
     * @return
     */
    fun getResourceColor(colorRes: Int): Int {
        return context.resources.getColor(colorRes)
    }

    /**
     * 取消显示必须调用此方法，展现动画
     */
    fun cancelDialog() {
        if (!isShowAnim) {
            cancel()
            return
        }
        val animRes = showQuitAnim()
        if (animRes != 0) {
            if (flParent == null) {
                cancel()
                return
            }
            flParent!!.clearAnimation()
            val anim = AnimationUtils.loadAnimation(context,
                    animRes)
            flParent!!.startAnimation(anim)
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    cancel()
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        } else {
            cancel()
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cancelDialog()
        }
        return false
    }

    //显示出场动画
    protected abstract fun showEnterAnim(): Int

    //显示退场动�?
    protected abstract fun showQuitAnim(): Int
}
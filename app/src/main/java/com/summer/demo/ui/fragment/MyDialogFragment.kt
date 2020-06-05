package com.summer.demo.ui.fragment

import android.app.Dialog
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout

import com.summer.demo.R
import com.summer.demo.module.base.BaseFragment
import com.summer.demo.dialog.BaseTipsDialog
import com.summer.demo.dialog.DialogWeixin
import com.summer.demo.dialog.LoadingDialog
import com.summer.demo.ui.fragment.dialog.BottomTestDialog
import com.summer.helper.utils.SUtils

import butterknife.BindView
import butterknife.OnClick

/**
 * Dialog的用法
 *
 * @author Administrator
 */
class MyDialogFragment : BaseFragment(), View.OnClickListener {
    internal var dialog: Dialog? = null
    @BindView(R.id.iv_bg)
    internal var ivBg: ImageView? = null
    @BindView(R.id.btn_common)
    internal var btnCommon: Button? = null
    @BindView(R.id.btn_longer)
    internal var btnLonger: Button? = null
    @BindView(R.id.btn_special)
    internal var btnSpecial: Button? = null
    @BindView(R.id.btn4)
    internal var btn4: Button? = null
    @BindView(R.id.btn5)
    internal var btn5: Button? = null
    @BindView(R.id.btn6)
    internal var btn6: Button? = null


    public override fun initView(view: View) {

        //监听View的长按事件
        view.findViewById<View>(R.id.btn5).setOnLongClickListener { v ->
            val pos = IntArray(2)
            v.getLocationInWindow(pos)
            showPupDialog(pos[0].toFloat(), pos[1].toFloat(), v)
            false
        }
    }

    public override fun loadData() {

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.fragment_dialog
    }

    @OnClick(R.id.btn_common, R.id.btn_special, R.id.btn_longer, R.id.btn4, R.id.btn5, R.id.btn6)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_common -> {
                val dialog = Dialog(context!!, R.style.TagFullScreenDialog)
                //设置样式，注意布局里indeterminateDrawable这个属性，而且5.0版本及以上与5.0以下写法不一样，注意看SummerHelper里的drawable-v21,对比差别
                dialog.setContentView(R.layout.dialog_loading)
                dialog.show()
            }
            R.id.btn_special -> {
                //弹出提示dialog，这是经常使用的功能,这里封装了一个简单的，传入想要提示的内容
                val baseTipsDialog = BaseTipsDialog(context, "没有网络哦!", object : BaseTipsDialog.DialogAfterClickListener {
                    override fun onSure() {
                        SUtils.makeToast(context, "去设置里设置网络吧")
                    }

                    override fun onCancel() {
                        SUtils.makeToast(context, "算了")
                    }
                })
                baseTipsDialog.show()
            }
            R.id.btn_longer -> {
                //通常为了方便，我们把加载的Dialog封装出来
                val dialog1 = LoadingDialog(context)
                dialog1.startLoading()
            }
            R.id.btn4 -> {
                val weixin = DialogWeixin(context)
                weixin.show()
            }
            R.id.btn6 -> {
                val bottomTestDialog = BottomTestDialog(context!!)
                bottomTestDialog.show()
            }
        }
    }

    /**
     * 长按一个按钮，显示一个Dialog在这个按钮正上方
     */
    private fun showPupDialog(left: Float, top: Float, view: View) {
        var left = left
        var top = top
        if (dialog != null) setCancelDialog()
        dialog = Dialog(context!!, R.style.dialog_pup)
        dialog!!.setContentView(R.layout.dialog_pup)
        val window = dialog!!.window
        //设置Dialog动画
        window!!.setWindowAnimations(R.anim.scale_with_alpha)
        window.setGravity(Gravity.BOTTOM)
        val ivdelte = dialog!!.findViewById<View>(R.id.iv_delete) as ImageView
        SUtils.clickTransColor(ivdelte)
        val dialogWidth = context!!.resources.getDimension(R.dimen.size_80).toInt()
        left = left + (view.width - dialogWidth) / 2
        top = top - dialogWidth * 2
        dialog!!.findViewById<View>(R.id.ll_parent).setOnClickListener { setCancelDialog() }
        ivdelte.setOnClickListener { setCancelDialog() }
        val mParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        ivdelte.layoutParams = mParams
        mParams.leftMargin = left.toInt()
        mParams.topMargin = top.toInt()
        dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.show()
    }

    private fun setCancelDialog() {
        if (null != dialog) {
            dialog!!.cancel()
            dialog = null
        }
    }

}

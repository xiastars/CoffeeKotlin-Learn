package com.summer.demo.ui.course.calculation

import android.app.Activity
import android.graphics.RectF
import android.text.InputType
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.summer.demo.R
import com.summer.demo.dialog.DialogModifyContent
import com.summer.demo.listener.OnModifyContentListener
import com.summer.demo.module.base.BaseFragment
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SUtils

/**
 * @Description: 计算
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/11/6 17:44
 */
class TrapezoidFragment : BaseFragment(), View.OnClickListener {
    @BindView(R.id.line_bottom)
    internal var lineBottom: View? = null
    @BindView(R.id.line_left)
    internal var lineLeft: View? = null
    @BindView(R.id.line_right)
    internal var lineRight: View? = null
    @BindView(R.id.line_top)
    internal var lineTop: View? = null

    internal lateinit var bottom: RectF
    internal lateinit var left: RectF
    internal lateinit var right: RectF
    internal lateinit var top: RectF
    @BindView(R.id.tv_bottom)
    internal var tvBottom: TextView? = null
    @BindView(R.id.tv_left)
    internal var tvLeft: TextView? = null
    @BindView(R.id.tv_right)
    internal var tvRight: TextView? = null
    @BindView(R.id.tv_top)
    internal var tvTop: TextView? = null

    internal var scale = 0f

    override fun initView(view: View) {
        SUtils.initScreenDisplayMetrics((context as Activity?)!!)
        val marginTop = (SUtils.screenHeight / 3f * 2).toInt()
        val width = SUtils.screenWidth / 3
        bottom = RectF()
        bottom.top = SUtils.getDip(context!!, 20).toFloat()
        bottom.left = (SUtils.screenWidth - width) / 2f
        bottom.right = bottom.left + width.toFloat() + SUtils.getDip(context!!, 50).toFloat()
        bottom.bottom = (marginTop + 1).toFloat()
        Logs.i("lllllll---$bottom")
        setLayout(bottom, lineBottom!!)

        left = RectF()
        left.top = bottom.top - SUtils.getDip(context!!, 200)
        left.left = bottom.left
        left.bottom = bottom.top
        left.right = bottom.left + 1
        setLayout(left, lineLeft!!)

        right = RectF()
        right.top = bottom.top - SUtils.getDip(context!!, 200)
        right.left = bottom.right
        right.right = bottom.right + 1
        right.bottom = bottom.top
        setLayout(right, lineRight!!)

        top = RectF()
        top.top = left.top - 1
        top.left = left.left
        top.bottom = left.top
        top.right = right.left
        Logs.i("rect:$top")
        setLayout(top, lineTop!!)

        tvBottom!!.text = (bottom.right - bottom.left).toString() + ""
        tvLeft!!.text = (left.bottom - left.top).toString() + ""
        tvRight!!.text = (right.bottom - right.top).toString() + ""
        tvTop!!.text = (top.right - top.left).toString() + ""
        (tvBottom!!.layoutParams as RelativeLayout.LayoutParams).leftMargin = (bottom.left + (bottom.right - bottom.left) / 2).toInt()
        (tvLeft!!.layoutParams as RelativeLayout.LayoutParams).topMargin = (left.bottom - (left.bottom - left.top) / 2).toInt()

        (tvTop!!.layoutParams as RelativeLayout.LayoutParams).leftMargin = (top.left + (top.right - top.left) / 2).toInt()
        (tvRight!!.layoutParams as RelativeLayout.LayoutParams).topMargin = (right.bottom - (right.bottom - right.top) / 2).toInt()


    }

    private fun setLayout(rectF: RectF, view: View) {
        val params = view.layoutParams as RelativeLayout.LayoutParams
        params.width = (rectF.right - rectF.left).toInt()
        params.height = (rectF.bottom - rectF.top).toInt()
        params.leftMargin = rectF.left.toInt()
        params.topMargin = rectF.top.toInt()
    }

    public override fun loadData() {

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.view_cal
    }

    @OnClick(R.id.tv_bottom)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_bottom -> {
                val dialogModifyContent = DialogModifyContent(context!!, OnModifyContentListener { content ->
                    val value = java.lang.Float.parseFloat(content)
                    scale = value / bottom.width()
                    resetLayout()
                })
                dialogModifyContent.setInputType(InputType.TYPE_CLASS_NUMBER)
                dialogModifyContent.setMaxTextLength(100)
                dialogModifyContent.show()
            }
        }
    }

    private fun resetLayout() {
        var s = scale
        if (s == 0f) {
            s = 1f
        }
        tvBottom!!.text = ((bottom.right - bottom.left) * s).toString() + ""
        tvLeft!!.text = ((left.bottom - left.top) * s).toString() + ""
        tvRight!!.text = ((right.bottom - right.top) * s).toString() + ""
        tvTop!!.text = ((top.right - top.left) * s).toString() + ""
    }
}

package com.summer.helper.dialog

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.TextUtils
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.malata.summer.helper.R
import com.summer.helper.dialog.adapter.SelectIntegralAdapter
import com.summer.helper.dialog.adapter.VotingValueAdapter
import com.summer.helper.listener.OnSimpleClickListener
import com.summer.helper.utils.SUtils
import com.summer.helper.view.NRecycleView

/**
 * 底部列表Dialog
 * Created by xiaqiliang on 2017/6/22.
 */
class BottomListDialog(context: Context) : BaseBottomDialog(context) {
    val tvTopTitle: TextView by Bind(R.id.tv_top_title)
    val tvFinish: TextView by Bind(R.id.tv_finish)
    protected var nvContainer: NRecycleView? = null
    var tvBottomContent: TextView? = null
    var rlTop: RelativeLayout? = null
    var tvCancel: TextView? = null
    var showFinishView = 0
    var showBottomContent = 0
    var showTopContent = 0
    var showCancelView = false
    var topTitle: String? = null
    var bottomContent: String? = null
    var datas = arrayOf("50", "200", "500")
    var selectPosition = 0
    var stringType = false
    var listener: OnSimpleClickListener? = null
    var spannbleString: SpannableString? = null
    var adapter: RecyclerView.Adapter<*>? = null


    override fun setContainerView(): Int {
        return R.layout.strirup_dialog_in
    }

    /**
     * 纯数字类型
     */
    fun setStringType() {
        stringType = true
    }

    override fun initView(view: View?) {
        init()
        setCanceledOnTouchOutside(true)
        if (!TextUtils.isEmpty(topTitle)) {
            tvTopTitle.setText(topTitle)
        }
        if (!TextUtils.isEmpty(bottomContent)) {
            tvBottomContent!!.text = bottomContent
        }
        if (spannbleString != null) {
            tvBottomContent!!.text = spannbleString
        }
        rlTop!!.visibility = showTopContent
        tvFinish.setOnClickListener(View.OnClickListener {
            onFinishClick()
            cancelDialog()
        })
        tvFinish.setVisibility(showFinishView)
        tvBottomContent!!.visibility = showBottomContent
        tvCancel!!.visibility = if (showCancelView) View.VISIBLE else View.GONE
        setDialogBottom()
        if (stringType) {
            nvContainer!!.setList()
            nvContainer!!.setCommonDividerGrey(SUtils.getDip(context, 10), SUtils.getDip(context, 10))
           adapter = SelectIntegralAdapter(context, datas, OnSimpleClickListener { position ->
                if (listener != null) {
                    listener!!.onClick(position)
                }
            })
        } else {
            nvContainer!!.setGridView(3)
            adapter = VotingValueAdapter(context, datas, OnSimpleClickListener { position ->
                selectPosition = position
                if (listener != null) {
                    listener!!.onClick(position)
                }
            })
        }
    }

    /**
     * 点击完成键
     */
    protected fun onFinishClick() {}

    /**
     * 留给子类配置
     */
    protected fun init() {}

    override fun showEnterAnim(): Int {
        return R.anim.slide_up
    }

    override fun showQuitAnim(): Int {
        return R.anim.slide_bottom
    }

    fun showTopContent(gone: Int) {
        showTopContent = gone
    }
}
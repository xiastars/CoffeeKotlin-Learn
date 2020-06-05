package com.summer.demo.dialog

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.summer.demo.R
import com.summer.helper.dialog.BaseCenterDialog
import com.summer.helper.utils.SUtils

/**
 * 基础的提示
 */
class BaseTipsDialog : BaseCenterDialog {
    private var listener: DialogAfterClickListener?
    private var layoutid: Int
    private var tvContent: TextView? = null
    private var tvTitle: TextView? = null
    var name: String? = null
    var okContent: String? = null
    var cancelContent: String? = null
    var content: String? = null
    var title: String? = null
    var cancelRes = 0
    var isShowCancelView = false //显示X = false
    var showTitle = true

    constructor(context: Context?, name: String?, listener: DialogAfterClickListener?) : super(context!!) {
        this.listener = listener
        this.name = name
        layoutid = R.layout.dialog_nf
    }

    constructor(context: Context?, layoutid: Int, listener: DialogAfterClickListener?) : super(context!!) {
        this.listener = listener
        this.layoutid = layoutid
    }

    override fun setContainerView(): Int {
        return layoutid
    }

    override fun initView(view: View?) {
        tvContent = view!!.findViewById<View>(R.id.tv_content) as TextView
        if (tvContent != null) {
            if (!TextUtils.isEmpty(name)) {
                SUtils.setHtmlText(name, tvContent)
            }
            if (!TextUtils.isEmpty(content)) {
                SUtils.setHtmlText(content, tvContent)
            }
        }
        val tvOK = view.findViewById<View>(R.id.tips_ok_tv) as TextView
        if (tvOK != null) {
            if (!TextUtils.isEmpty(okContent)) {
                tvOK.text = okContent
            }
            SUtils.clickTransColor(tvOK)
            tvOK.setOnClickListener {
                if (null != listener) {
                    listener!!.onSure()
                }
                cancelDialog()
            }
        }
        tvTitle = view.findViewById<View>(R.id.tv_title) as TextView
        if (title != null) {
            tvTitle!!.text = title
        }
        tvTitle!!.visibility = if (showTitle) View.VISIBLE else View.GONE
        val tvCancel = view.findViewById<View>(R.id.tips_cancel_tv) as TextView
        if (tvCancel != null) {
            if (!TextUtils.isEmpty(cancelContent)) {
                tvCancel.text = cancelContent
            }
            if (cancelRes != 0) {
                tvCancel.setTextColor(cancelRes)
            }
            SUtils.clickTransColor(tvCancel)
            tvCancel.setOnClickListener {
                if (null != listener) {
                    listener!!.onCancel()
                }
                cancelDialog()
            }
        }
        val ivCancel = view.findViewById<View>(R.id.iv_cancel) as ImageView
        if (ivCancel != null) {
            if (isShowCancelView) {
                ivCancel.visibility = View.VISIBLE
            }
            ivCancel.setOnClickListener { cancelDialog() }
        }
    }

    fun hideTitle() {
        showTitle = false
    }


    interface DialogAfterClickListener {
        fun onSure()
        fun onCancel()
    }

    companion object {
        private var tipDialog: BaseTipsDialog? = null
        fun getInstance(context: Context?, name: String?, listener: DialogAfterClickListener?): BaseTipsDialog? {
            if (tipDialog != null) {
                tipDialog!!.cancel()
                tipDialog = null
            }
            tipDialog = BaseTipsDialog(context, name, listener)
            return tipDialog
        }
    }
}
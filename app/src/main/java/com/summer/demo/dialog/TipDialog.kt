package com.summer.demo.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.summer.demo.R
import com.summer.helper.utils.SUtils

/**
 * @author xiastars@vip.qq.com
 */
class TipDialog(context: Context?, private val name: String, private val listener: DialogAfterClickListener?) : Dialog(context, R.style.TagFullScreenDialog) {
    private val layoutid: Int
    private val mLoadUrl: String? = null
    var tvOK: TextView? = null
    var tvCancel: TextView? = null
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(layoutid)
        initView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        val tips_tv = findViewById<View>(R.id.tips_tv) as TextView
        if (!TextUtils.isEmpty(name)) {
            tips_tv.text = name
        }
        tvOK = findViewById<View>(R.id.tips_ok_tv) as TextView
        SUtils.clickTransColor(tvOK)
        tvOK!!.setOnClickListener {
            listener?.onSure()
            cancel()
        }
        tvCancel = findViewById<View>(R.id.tips_cancel_tv) as TextView
        SUtils.clickTransColor(tvCancel)
        tvCancel!!.setOnClickListener {
            listener?.onCancel()
            cancel()
        }
        setOnCancelListener { listener?.onCancel() }
    }

    /**
     * 自定义左边按钮的文字
     *
     * @param id
     */
    fun setOkText(id: Int) {
        tvOK!!.text = context.resources.getString(id)
    }

    /**
     * 自定义右边按钮的文字
     *
     * @param id
     */
    fun setCancelText(id: Int) {
        tvCancel!!.text = context.resources.getString(id)
    }

    interface DialogAfterClickListener {
        fun onSure()
        fun onCancel()
    }

    init {
        layoutid = R.layout.dialog_tips
    }
}
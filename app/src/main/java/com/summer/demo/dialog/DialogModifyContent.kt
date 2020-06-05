package com.summer.demo.dialog

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.summer.demo.R
import com.summer.demo.listener.OnModifyContentListener
import com.summer.helper.dialog.BaseCenterDialog
import com.summer.helper.utils.STextUtils
import com.summer.helper.utils.SUtils

/**
 * 通用修改内容
 */
class DialogModifyContent(context: Context, var onReturnObjectClickListener: OnModifyContentListener) : BaseCenterDialog(context) {
    internal val tvTitle: TextView by Bind(R.id.tv_title)
    var line: View? = null
    internal val tvContent: EditText by Bind(R.id.tv_content)
    internal val view: View by Bind(R.id.view)
    internal val llCancel: LinearLayout by Bind(R.id.ll_cancel)

    internal val llSure: LinearLayout by Bind(R.id.ll_sure)

    internal val iamfather: RelativeLayout by Bind(R.id.iamfather)

    internal val tvSure: TextView by Bind(R.id.tv_sure)

    var isSureable = false
    var titleContent: String? = null
    var maxTextLength = 1
    var defaultContent: String? = null
    var defaultHint: String? = null
    var okContent: String? = null
    var inputType = 0
    override fun setContainerView(): Int {
        return R.layout.dialog_edit_content
    }

    override fun initView(view: View?) {
        tvContent!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                isSureable = s.length > 0
                tvSure!!.setTextColor(if (isSureable) getResourceColor(R.color.yellow_ff9) else getResourceColor(R.color.grey_c5))
            }
        })
        if (!TextUtils.isEmpty(titleContent)) {
            tvTitle.setText(titleContent)
        }
        if (!TextUtils.isEmpty(defaultHint)) {
            tvContent!!.hint = defaultHint
        }
        if (!TextUtils.isEmpty(okContent)) {
            tvSure!!.text = okContent
        }
        if (inputType != 0) {
            tvTitle.setInputType(inputType)
        }
        tvContent!!.filters = arrayOf(InputFilter.LengthFilter(maxTextLength))
        llCancel!!.setOnClickListener { cancelDialog() }
        if (!TextUtils.isEmpty(defaultContent)) {
            tvContent!!.setText(defaultContent)
            SUtils.setSelection(tvContent)
        }
        llSure!!.setOnClickListener(View.OnClickListener {
            if (!isSureable) {
                return@OnClickListener
            }
            val content = tvContent!!.text.toString()
            if (STextUtils.isEmpty(content)) {
                SUtils.makeToast(context, R.string.toast_no_empty)
                return@OnClickListener
            }
            cancelDialog()
            onReturnObjectClickListener.returnContent(content)
        })
        tvContent!!.postDelayed({ SUtils.showSoftInpuFromWindow(tvContent) }, 300)
    }


}
package com.summer.demo.ui.course.fragment

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import butterknife.BindView
import butterknife.OnClick
import com.summer.demo.R
import com.summer.demo.module.base.BaseFragment
import com.summer.demo.view.CommonSureView5
import com.summer.helper.utils.SUtils
import java.io.File

/**
 * @Description: 视频获取封面
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/14 9:31
 */
class VideoGetCoverFragment : BaseFragment(), View.OnClickListener {
    @BindView(R.id.edt_content)
    internal var edtContent: EditText? = null
    @BindView(R.id.btn_sure)
    internal var btnSure: CommonSureView5? = null

    override fun initView(view: View) {
        edtContent!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                btnSure!!.changeStyle(s.length > 0)
            }
        })
    }

    public override fun loadData() {

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.fragment_video_frame
    }

    @OnClick(R.id.btn_sure)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_sure -> {
                val fileName = edtContent!!.text.toString()
                val rootPath = SUtils.getSDPath() + fileName
                val file = File(rootPath)
                val files = file.list()
            }
        }
    }

}

package com.summer.demo.ui.course

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.summer.demo.R
import com.summer.demo.module.base.BaseFragment
import com.summer.helper.utils.SUtils
import com.zzhoujay.richtext.RichText

/**
 * @Description: 用来承载MarkDown内容
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/6/10 15:03
 */
class MarkDownFragment : BaseFragment() {
    internal val tvContent: TextView by Bind(R.id.tv_content)


    override fun setContentView(): Int {
        return R.layout.ac_markdown
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initView(view: View) {
        val path = arguments!!.getString("path")
        val content = SUtils.readAssetFileToString(context!!, path)
        RichText.fromMarkdown(content).clickable(true).urlLongClick {
            // Toast.makeText(getApplicationContext(),url,Toast.LENGTH_SHORT).show();
            true
        }.into(tvContent)
    }

    public override fun loadData() {

    }

    override fun dealDatas(requestCode: Int, obj: Any) {

    }

    companion object {

        fun show(path: String): MarkDownFragment {
            val markDownFragment = MarkDownFragment()
            val bundle = Bundle()
            bundle.putString("path", path)
            markDownFragment.arguments = bundle
            return markDownFragment
        }
    }
}

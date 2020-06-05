package com.summer.demo.ui.view.commonfragment

import android.text.SpannableStringBuilder
import android.view.View
import android.widget.TextView
import com.summer.demo.R
import com.summer.demo.module.base.BaseFragment

/**
 * @Description: 介绍SViewUtils
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/11/8 11:38
 */
class SViewUtilsFragment : BaseFragment() {
    override fun initView(view: View) {
        val tvContent = TextView(context)
        val builder = SpannableStringBuilder()
        // STextUtils.getSpannableView("SViewUtils的使用讲解:",0,-1,R.color.grey_22,1,5f,true);
    }

    public override fun loadData() {

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.view_empty
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

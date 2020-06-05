package com.summer.demo.ui.module.fragment

import android.view.View

import com.summer.demo.R
import com.summer.demo.module.base.BaseFragment
import com.summer.demo.ui.module.comment.CommentHelper

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/11 17:16
 */
class EmojiFragment : BaseFragment() {
    override fun initView(view: View) {
        val commentHelper = CommentHelper(view)
    }

    public override fun loadData() {

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.fragment_emoji
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

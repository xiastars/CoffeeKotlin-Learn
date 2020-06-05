package com.summer.demo.ui.module.fragment.socket

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.summer.demo.R
import com.summer.demo.module.base.BaseFragment

/**
 * @Description: socket使用演示
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/31 17:36
 */
class SocketFragment : BaseFragment() {
    internal val ivNav: ImageView by Bind(R.id.iv_nav)
    internal val tvHintContent: TextView by Bind(R.id.tv_hint_content)
    internal val tvReload: TextView by Bind(R.id.tv_reload)

    override fun initView(view: View) {


        tvHintContent!!.text = "Socket连接，重连，收发；看代码"
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

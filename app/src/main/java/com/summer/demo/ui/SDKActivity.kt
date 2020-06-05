package com.summer.demo.ui

import android.view.View
import com.summer.demo.R

/**
 * @Description: 有关sdk对接
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/6/20 16:07
 */
class SDKActivity : BaseGridListActivity() {

    override fun initData() {
        super.initData()
        val imgs = intArrayOf(R.drawable.alexa)
        setData(context!!.resources.getStringArray(R.array.sdks), imgs)
    }

    override fun clickChild(pos: Int) {
        when (pos) {
            0 -> {
            }
        }//showFragment(new AlexaFragment());
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

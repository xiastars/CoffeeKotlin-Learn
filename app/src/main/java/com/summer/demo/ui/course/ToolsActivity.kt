package com.summer.demo.ui.course

import android.content.Context
import android.view.View
import com.summer.demo.R
import com.summer.demo.ui.BaseTitleListActivity
import com.summer.helper.utils.JumpTo
import java.util.*


/**
 * 一些常用工具
 *
 * @author xiastars@vip.qq.com
 */
class ToolsActivity : BaseTitleListActivity() {

    override fun initData() {
        super.initData()
        setTitle("常用工具")
    }

    override fun setData(): List<String> {
        return getData(context!!)
    }

    override fun clickChild(pos: Int) {
        when (pos) {
            0 -> JumpTo.getInstance().commonJump(context, CourseContainerActivity::class.java, MarkdownPos.NET_FIVE_STRUCTRURE)
            1 -> JumpTo.getInstance().commonJump(context, CourseContainerActivity::class.java, MarkdownPos.NET_HANKSHAKE)
            2 -> JumpTo.getInstance().commonJump(context, CourseContainerActivity::class.java, MarkdownPos.NET_HTTPS)
        }
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        fun getData(context: Context): List<String> {
            val title = ArrayList<String>()
            /* 从XML里获取String数组的方法*/
            val group = context.resources.getStringArray(R.array.net)
            for (i in group.indices) {
                val ti = group[i]
                title.add(ti)
            }
            return title
        }
    }

}

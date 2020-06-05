package com.summer.demo.ui.course

import android.content.Context
import android.view.View
import com.summer.demo.R
import com.summer.demo.ui.BaseTitleListActivity
import com.summer.helper.utils.JumpTo
import java.util.*


/**
 * Java基础
 *
 * @author xiastars@vip.qq.com
 */
class LearnJavaActivity : BaseTitleListActivity() {

    override fun initData() {
        super.initData()
        setTitle("Java零基础教程")
    }

    override fun setData(): List<String> {
        return getData(context!!)
    }

    override fun clickChild(pos: Int) {
        when (pos) {
            0 -> JumpTo.getInstance().commonJump(context, CourseContainerActivity::class.java, MarkdownPos.JAVA_OBJECT)
            1 -> JumpTo.getInstance().commonJump(context, CourseContainerActivity::class.java, MarkdownPos.JAVA_CHILD)
        }
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        fun getData(context: Context): List<String> {
            val title = ArrayList<String>()
            /* 从XML里获取String数组的方法*/
            val group = context.resources.getStringArray(R.array.java_titles)
            for (i in group.indices) {
                val ti = group[i]
                title.add(ti)
            }
            return title
        }
    }

}

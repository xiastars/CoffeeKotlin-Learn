package com.summer.demo.ui.fragment.views

import android.os.Build
import android.support.annotation.RequiresApi
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.TextView
import com.summer.demo.R
import com.summer.demo.module.base.BaseFragment
import com.summer.demo.utils.TextWebUtils
import com.summer.helper.utils.Logs
import com.summer.helper.utils.STextUtils
import java.math.BigDecimal

/**
 * @Description: TextView的基本用法
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/6/14 16:21
 */
class TextViewFragment : BaseFragment() {
    private val tvContent: TextView by Bind(R.id.tv_content)
    private val tvSuper: TextView by Bind(R.id.tv_super)
    private val tvSpan: TextView by Bind(R.id.tv_span)


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun initView(view: View) {
        tvContent!!.text = "有一些特殊的操作是xml里没有提供的，比如下划线，这里演示用代码来改变样式"
        //设置文本字体大小
        tvContent!!.textSize = 20f
        tvContent!!.letterSpacing = 1f
        val paint = tvContent!!.paint
        //给文本添加下划线
        paint.isUnderlineText = true
        paint.textSkewX = -1f

        val builder = SpannableStringBuilder()

        builder.append("TextView配合SpannableStringBuilder的一些基本用法：成功续费 ")
        val fee = "￥" + BigDecimal((10000 / 100f).toDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).toFloat()
        builder.append(STextUtils.getSpannableView(fee, 0, fee.length, getResColor(R.color.blue_4c), 0f, true))
        builder.append(" 继续关注 ")
        builder.append(STextUtils.getSpannableView("xiastars", 0, 8, getResColor(R.color.red_d3), 1.5f, true))
        tvSpan!!.text = builder

        val content = "TextView配合SpanningString的高级用法，试试点击以下三个标签：将链接转为短链<e type=\"web\" title='=\"图片链接\"" +
                " href=\"https://movie.douban.com/\">；自定义标签识别<e type=\"hashtag\" hid='=\"ViewPager\" " +
                " title=\"ViewPager\" >; 图片标签<e type=\"web\" title=\"「查看图片」\" href=\"https://img1.doubanio.com/view/photo/l/public/p2324017307.webp\" >；"

        TextWebUtils.setHtmlText(tvSuper!!, content)
        var time = System.currentTimeMillis()
        //尽量不要用这个format，特别是在列表里
        for (i in 0..999) {
            context!!.getString(R.string.test_string, "你好哈哈哈哈")

        }
        Logs.t(time)
        time = System.currentTimeMillis()
        //拼接字符串用这种
        for (i in 0..999) {
            STextUtils.spliceText("哈哈", "你好哈哈哈哈")

        }
        Logs.t(time)

    }

    public override fun loadData() {

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.fragment_textview
    }

    override fun onClick(v: View?) {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

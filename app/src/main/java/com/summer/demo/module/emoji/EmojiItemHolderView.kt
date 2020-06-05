package com.summer.demo.module.emoji

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.summer.demo.R
import com.summer.demo.module.base.viewpager.Holder
import com.summer.helper.utils.SUtils
import com.summer.helper.view.RoundAngleImageView

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/11 10:19
 */
class EmojiItemHolderView : Holder<EmojiInfo?> {
    private var imageView: RoundAngleImageView? = null
    private var tvTitle: TextView? = null
    private var tvSubTitle: TextView? = null
    override fun createView(context: Context): View {
        val view = LayoutInflater.from(context).inflate(R.layout.view_emoji_banner, null)
        //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
        imageView = view.findViewById<View>(R.id.iv_nav) as RoundAngleImageView
        imageView!!.isShowTouchAnim = false
        tvTitle = view.findViewById<View>(R.id.tv_title) as TextView
        tvSubTitle = view.findViewById<View>(R.id.tv_subtitle) as TextView
        imageView!!.scaleType = ImageView.ScaleType.CENTER_CROP
        return view
    }

    override fun UpdateUI(context: Context?, position: Int, data: EmojiInfo?) {
        SUtils.setPicWithHolder(imageView, data!!.img, R.drawable.default_icon_linear)
        SUtils.setNotEmptText(tvTitle, data!!.title)
        SUtils.setNotEmptText(tvSubTitle, data!!.subTitle)
    }
}
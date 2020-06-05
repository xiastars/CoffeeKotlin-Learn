package com.summer.demo.module.emoji

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import com.summer.demo.R
import com.summer.demo.module.base.viewpager.CBViewHolderCreator
import com.summer.demo.module.base.viewpager.ConvenientBanner
import com.summer.demo.module.emoji.source.EmojiSource
import com.summer.demo.module.emoji.source.Source
import com.summer.helper.listener.OnReturnObjectClickListener

/**
 * 表情主页面
 * Created by xiaqiliang on 2017/9/7.
 */
class MyEmojiView(private val context: Context, type: Int, listener: OnReturnObjectClickListener) {
    private var viewPager: ConvenientBanner<*>? = null
    private var emojiSource: EmojiSource? = null
    private var emojiService: MyEmojiService? = null
    private val activity: Activity
    private var emojiLayout: View? = null
    private val type //如果type==0，为非聊天页面，隐藏下面菜单与其它贴图
            : Int
    private val listener: OnReturnObjectClickListener
    private val clickIndex = 0
    private val layout: RelativeLayout? = null
    var holderCreator: CBViewHolderCreator<*>? = null
    private fun init() {
        emojiLayout = activity.findViewById(R.id.emoji_layout)
        if (emojiLayout == null) {
            return
        }
        emojiService = MyEmojiService.Companion.getInstance(context.applicationContext)
        emojiSource = EmojiSource.getInstance(context.applicationContext)
        findViewById()
        setIconAdapter(emojiSource!!.getSourceList())
    }

    private fun findViewById() {
        viewPager = emojiLayout!!.findViewById<View>(R.id.viewFlow) as ConvenientBanner<*>
        viewPager!!.isCanLoop = true
        holderCreator = CBViewHolderCreator { EmojiItemHolderView() }
    }

    /**
     * 获取表情界面可见不可见
     *
     * @return
     */
    /**
     * 设置表情界面 是否可见
     *
     * @param visible
     */
    var visible: Boolean
        get() = if (emojiLayout == null) false else emojiLayout!!.visibility == View.VISIBLE
        set(visible) {
            if (emojiLayout == null) return
            if (visible) {
                emojiLayout!!.visibility = View.VISIBLE
            } else {
                emojiLayout!!.visibility = View.GONE
            }
        }

    /**
     * @param sources
     */
    private fun setIconAdapter(sources: List<Source>) {
        val creator: CBViewHolderCreator<*> = CBViewHolderCreator {
            EmojiHolderView(OnReturnObjectClickListener { `object` ->
                val iconEntity = `object` as IconEntity
                val s = emojiService!!.replaceEmoji(iconEntity.name, 14).toString()
                iconEntity.emojiText = s
                listener.onClick(iconEntity)
            })
        }
        //????????????????????
        //viewPager!!.setPages(creator, sources).setPageIndicator(intArrayOf(R.drawable.so_greycc_oval, R.drawable.so_grey33_oval))
    }

    init {
        activity = context as Activity
        this.listener = listener
        this.type = type
        init()
    }
}
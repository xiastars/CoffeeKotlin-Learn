package com.summer.demo.view

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.summer.demo.R
import com.summer.demo.bean.DanmakuInfo
import com.summer.demo.module.emoji.EmojiUtil
import com.summer.helper.listener.OnSimpleClickListener
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SAnimUtils
import com.summer.helper.utils.SUtils
import java.lang.ref.WeakReference
import java.util.*


/**
 * Created by xiastars on 2017/7/21.
 */

class SimpleDanmakuView : RelativeLayout {
    internal var comments: MutableMap<Int, MutableList<DanmakuInfo>>? = null
    internal lateinit var listener: OnSimpleClickListener
    internal lateinit var context: Context
    internal lateinit var myHandler: MyHandler

    internal var pageIndex: Int = 0
    internal var fromId: String? = null
    internal lateinit var eventId: String
    internal var isEnd: Boolean = false//数量没有更多了
    internal lateinit var danmakuRunnable: Runnable

    internal var enableDannmaku = true
    internal lateinit var CONTEXT_TAG: String

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    fun init(context: Context) {
        this.context = context
        myHandler = MyHandler(this)
        CONTEXT_TAG = context.javaClass.simpleName
        Logs.i("CONTEXT_TAG:$CONTEXT_TAG")
    }

    fun setEventId(eventID: String) {
        this.eventId = eventID
    }

    fun addDatas(infos: MutableList<DanmakuInfo>?, pos: Int) {
        if (!enableDannmaku) {
            return
        }
        if (comments == null) {
            comments = HashMap()
        }
        if (infos != null && infos.isNotEmpty()) comments!![pos] = infos
        if (infos!!.size < 20) {
            isEnd = true
        }
        startDanmaku(pos)
    }

    private fun startDanmaku(pos: Int) {
        var pos = pos
        if (!enableDannmaku || comments == null) {
            return
        }
        pageIndex++
        if (pos >= comments!!.size) {
            pos = 0
        }

        val items = comments!![pos] ?: return
        val count = items.size
        for (i in 0 until count) {
            val info = items[i]
            getDanmakuView(info, i)
        }
        if (isEnd) {
            myHandler.sendEmptyMessageDelayed(1, (15 * 1000).toLong())
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun getDanmakuView(info: DanmakuInfo, position: Int): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_dannmaku, null)
        view.visibility = View.GONE
        val ivIcon = view.findViewById<View>(R.id.iv_icon) as ImageView
        SUtils.setPicWithHolder(ivIcon, info.img, R.drawable.default_icon_triangle)
        val tvTitle = view.findViewById<View>(R.id.tv_title) as TextView
        tvTitle.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        EmojiUtil.setEmojiText(tvTitle, info.name)
        this.addView(view)
        danmakuRunnable = Runnable {
            SAnimUtils.showPropertyAnim(false, view, View.VISIBLE, "translationX", SUtils.screenWidth.toFloat(), (-SUtils.getDip(view.context, 330)).toFloat(), (-SUtils.getDip(view.context, 330)).toFloat(), 15000) {
                this@SimpleDanmakuView.removeView(view)
                if (isEnd && this@SimpleDanmakuView.childCount < 1) {
                    startDanmaku(pageIndex)
                }
            }
            if (position == 15 && !isEnd) {
                requestCommentData(eventId)
            }
        }
        myHandler.postDelayed(danmakuRunnable, (position * 1000).toLong())
        val topPadding = Random().nextInt(6) * SUtils.getDip(getContext(), 20)
        (view.layoutParams as RelativeLayout.LayoutParams).topMargin = topPadding
        return view
    }

    /**
     * 停止播放
     */
    fun stopPlay() {
        enableDannmaku = false
        this.removeAllViews()
        myHandler.removeMessages(0)
        myHandler.removeMessages(1)
        myHandler.removeCallbacks(danmakuRunnable)
    }

    /**
     * 开始播放
     */
    fun startPlay() {
        enableDannmaku = true
        Logs.i("继续播放$isEnd")
        if (isEnd) {
            startDanmaku(0)
        } else {
            startDanmaku(pageIndex)
            requestCommentData(eventId)
        }
    }

    fun addSimpleListener(listener: OnSimpleClickListener) {
        this.listener = listener
    }

    fun requestOrResume(eventId: String) {
        enableDannmaku = true
        if (isEnd) {
            startPlay()
        } else {
            requestCommentData(eventId)
        }
    }

    /**
     * 网络请求数据
     *
     * @param eventID
     */
    fun requestCommentData(eventID: String) {

    }

    /**
     * 添加一条新的弹幕
     *
     * @param info
     */
    fun addNewComment(info: DanmakuInfo) {
        var isEmpty = false
        if (comments == null) {
            comments = HashMap()
            isEmpty = true
        }
        var items: MutableList<DanmakuInfo>? = comments!![pageIndex]
        if (items == null) {
            items = ArrayList()
            comments!![pageIndex] = items
            if (pageIndex == 0) {
                isEmpty = true
            }
        }
        items.add(info)
        if (isEmpty) {
            startDanmaku(0)
        }
    }

    class MyHandler(activity: SimpleDanmakuView) : Handler() {
        private val mActivity: WeakReference<SimpleDanmakuView>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity = mActivity.get()
            if (null != activity) {
                when (msg.what) {
                    0,

                    1 -> activity.startDanmaku(activity.pageIndex)
                }
            }
        }
    }

}



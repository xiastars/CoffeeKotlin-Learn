package com.summer.demo.ui.module.comment

import android.app.Activity
import android.graphics.PointF
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.summer.demo.R
import com.summer.demo.module.base.BaseActivity
import com.summer.demo.module.base.BaseFragment
import com.summer.demo.module.base.BaseFragmentActivity
import com.summer.demo.module.base.CommonHelper
import com.summer.demo.module.emoji.EmojiHelper
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SUtils
import com.summer.helper.view.NRecycleView
import com.summer.helper.view.SRecycleView
import com.summer.helper.view.ScrollableLayout

/**
 * 处理评论与回复
 * Created by xiaqiliang on 2017/4/15.
 */

class CommentHelper(activity: View) : CommonHelper(activity.context), View.OnClickListener {
    internal var edtContent: EditText
    internal var btnSend: Button? = null
    private val rlCommentCount: RelativeLayout
    private val rlEditParent: RelativeLayout
    private val tvCommentCount: TextView?
    private val ivEmoji: ImageView
    internal lateinit var  comments: List<StarCommentInfo>

    private var mOnCommentCountViewClickedListener: OnCommentCountViewClickedListener? = null

    internal var sendEnabled = false
    internal var replyId: Long = 0
    internal lateinit var replyName: String
    internal var preTop: Int = 0

    //是否是最热类型
    var isHotType: Boolean = false

    internal var fromId: Long = 0

    internal var sRecycleView: SRecycleView? = null
    internal var emojiHelper: EmojiHelper
    internal var nvContainser: NRecycleView? = null
    internal var activity: Activity? = null
    internal var baseFragment: BaseFragment? = null

    internal var isReresh: Boolean = false

    internal val REQUEST_SEND = 0
    internal val REQUEST_NEW_COMMENT = 1

    val pagerIndex: Int
        get() {
            if (activity != null) {
                if (activity is BaseFragmentActivity) {

                    val ba = activity as BaseFragmentActivity?
                    return ba!!.pageIndex
                } else if (activity is BaseActivity) {
                    val b = activity as BaseActivity?
                    return b!!.pageIndex
                }
            } else if (baseFragment != null) {
                return baseFragment!!.pageIndex
            }
            return 0
        }

    init {
        this.activity = activity.context as Activity
        emojiHelper = EmojiHelper()
        emojiHelper.initEmojiView(activity)
        ivEmoji = activity.findViewById<View>(R.id.iv_emoji) as ImageView
        this.edtContent = activity.findViewById<View>(R.id.edt_comment) as EditText
        this.btnSend = activity.findViewById<View>(R.id.btn_send) as Button
        rlCommentCount = activity.findViewById<View>(R.id.rl_comment_count) as RelativeLayout
        rlEditParent = activity.findViewById<View>(R.id.rl_edit_parent) as RelativeLayout
        rlCommentCount.setOnClickListener(this)
        tvCommentCount = activity.findViewById<View>(R.id.tv_comment_count) as TextView
        init()
        val parent = activity.findViewById<View>(R.id.ll_parent).parent as View
        parent?.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (preTop == 0) {
                preTop = top
            } else if (preTop < top && !emojiHelper.isEmojiView && TextUtils.isEmpty(edtContent.text.toString())) {
                replyId = 0
                replyName = ""
                edtContent.hint = "请输入评论:"
                emojiHelper.setEmojiLayoutInvisible(false)
            } else {
                preTop = top
            }
        }

    }

    private fun init() {
        edtContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                changeSendStyle(s.toString())
            }
        })

        btnSend!!.setOnClickListener { sendComment() }
    }


    /**
     * 发布评论
     */
    fun sendComment() {
        if (!sendEnabled) {
            SUtils.makeToast(context, context.getString(R.string.hint_empty_comment))
            return
        }
    }


    /**
     * 改变发送的颜色
     *
     * @param s
     */
    private fun changeSendStyle(s: String) {
        if (TextUtils.isEmpty(s)) {
            sendEnabled = false
            btnSend!!.setBackgroundResource(R.drawable.so_greyd8_5)
        } else {
            sendEnabled = true
            btnSend!!.setBackgroundResource(R.drawable.so_redd4_5)
        }
    }

    override fun handleMsg(position: Int, `object`: Any) {

    }

    override fun dealDatas(requestCode: Int, obj: Any) {
        when (requestCode) {
            REQUEST_SEND -> {
                fromId = 0

                edtContent.setText("")
                SUtils.hideSoftInpuFromWindow(edtContent)
                edtContent.hint = "请输入评论:"
            }
            REQUEST_NEW_COMMENT -> {
                val items = obj as List<StarCommentInfo>
                if (isHotType) {
                    comments = items
                }

                val size = items.size

                if (size == 10) {
                    fromId = items[size - 1].id

                }
                Logs.i("是否是重新刷新模块:$isReresh")
                if (activity != null) {
                    if (activity is BaseFragmentActivity) {

                        val ba = activity as BaseFragmentActivity?
                        ba!!.baseHelper!!.loadCount = 10
                        if (isReresh) {
                            ba.pageIndex = 0
                        }
                        if (nvContainser != null) {
                            //ba.handleViewData(obj, nvContainser!!)
                        } else if (sRecycleView != null) {
                            //ba.handleViewData(obj)
                        }
                    } else if (activity is BaseActivity) {
                        val b = activity as BaseActivity?
                        b!!.baseHelper!!.loadCount = 10
                        if (isReresh) {
                            b.pageIndex = 0
                        }
                        if (sRecycleView != null) {
                            b.handleViewData(obj)
                        }
                    }
                } else if (baseFragment != null) {
                    baseFragment!!.baseHelper!!.loadCount = 10
                    if (isReresh) {
                        baseFragment!!.pageIndex = 0
                    }
                    if (sRecycleView != null) {
                        baseFragment!!.handleViewData(obj)
                    }

                }
                notifyCommentCountView()
            }
        }//handleCommentScroll();
    }

    private fun notifyCommentCountView() {
        if (tvCommentCount != null) {
            val totalCount = 100
            Logs.i("totalCount:$totalCount")
            if (totalCount == 0) {
                tvCommentCount.visibility = View.GONE
            } else {
                tvCommentCount.text = if (totalCount > 999) "999+" else totalCount.toString() + ""
                tvCommentCount.visibility = View.VISIBLE
            }
        }
    }

    private fun setLoadMore(loadMore: Boolean) {
        if (activity != null) {
            if (activity is BaseFragmentActivity) {

                val ba = activity as BaseFragmentActivity?
                ba!!.setLoadMore(loadMore)
            } else if (activity is BaseActivity) {
                val b = activity as BaseActivity?
                //b!!.setLoadMore(loadMore)
                Logs.i("xxxxxxxxxxxxxxxxxxxxxx")
            }
        } else if (baseFragment != null) {
            //baseFragment!!.setLoadMore(loadMore)
        }
    }

    override fun dealErrors(requstCode: Int, requestType: String, errString: String, requestCode: Boolean) {

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.rl_comment_count -> handleCommentScroll()//让评论列表nvContainser滚动到顶部
        }
    }

    fun handleCommentScroll() {
        val parent = getScrollParentView(nvContainser!!)
        var handle = false
        if (parent != null) {
            val y = getViewLocationInParentView(nvContainser!!, parent).y.toInt()
            if (parent.scrollY < y) {
                handle = true
                if (parent is ScrollView) {
                    parent.smoothScrollTo(parent.scrollX, y)
                } else {
                    parent.scrollTo(parent.scrollX, y)//有时间再补个动画
                }
            }
        }
        if (mOnCommentCountViewClickedListener != null)
            mOnCommentCountViewClickedListener!!.OnCommentCountViewClicked(handle)
    }

    private fun getScrollParentView(view: View): ViewGroup? {
        val parent = view.parent
        if (parent !is ViewGroup)
            return null
        val viewGroup = parent ?: return null
//如果是自定义的滚动父view，不是继承于安卓自带的可滚动View，加上（|| instanceof）判断
        return if (viewGroup.isScrollContainer || viewGroup is ScrollableLayout) {
            viewGroup
        } else {
            getScrollParentView(viewGroup)
        }
    }

    private fun getViewLocationInParentView(childView: View, parentView: View): PointF {
        val childX = childView.x
        val childY = childView.y
        val point = PointF(childX, childY)
        var parent: View? = childView.parent as View
        while (parent != null && parent !== parentView) {
            point.x += parent.x
            point.y += parent.y
            parent = parent.parent as View
        }
        if (parent == null) {
            //根本不是父子view
            point.x = childX
            point.y = childY
        }
        return point
    }

    fun setOnCommentCountViewClickedListener(listener: OnCommentCountViewClickedListener) {
        mOnCommentCountViewClickedListener = listener
    }

    interface OnCommentCountViewClickedListener {
        /**
         * @param handleScrollSuccess 是否成功处理nvContainser的滚动事件
         */
        fun OnCommentCountViewClicked(handleScrollSuccess: Boolean)
    }
}

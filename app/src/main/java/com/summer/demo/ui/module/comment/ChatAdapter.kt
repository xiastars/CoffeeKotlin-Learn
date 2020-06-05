package com.summer.demo.ui.module.comment

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.summer.demo.R
import com.summer.demo.bean.UserInfo
import com.summer.demo.dialog.BaseTipsDialog
import com.summer.helper.adapter.SRecycleAdapter
import com.summer.helper.adapter.SRecycleMoreAdapter
import com.summer.helper.utils.Logs
import com.summer.helper.utils.STextUtils
import com.summer.helper.utils.STimeUtils
import com.summer.helper.utils.SUtils
import com.summer.helper.view.RoundAngleImageView

class ChatAdapter(context: Context, internal var selfInfo: UserInfo, internal var talkerInfo: UserInfo) : SRecycleAdapter(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_SELF) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_chat_self, parent, false)
            return SelfHolder(view)
        } else if (viewType == VIEW_TYPE_OTHER) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_chat_other, parent, false)
            return OtherHolder(view)
        }

        return super.onCreateViewHolder(parent, viewType)
    }

    override fun notifyDataChanged(comments: List<*>) {
        this.items = comments
        val infos = items as List<ChatInfo>
        var time: Long = 0
        for (info in infos) {
            val curTime = info.send_time
            if (curTime - time > 60 * 2) {
                info.isShowTime = true
            }
            Logs.i("()" + (curTime - time) + ",,," + info.isShowTime)
            time = curTime
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val info = items[position] as ChatInfo
        if (holder is SelfHolder) {
            setUserInfo(holder.ivAvatar, selfInfo)
            setContentView(holder.tvContent, info)
            setSendStatus(holder.pbLoading, holder.ivError, info)
            showTimeView(holder.tvTime, info)
        } else if (holder is OtherHolder) {
            setUserInfo(holder.ivAvatar, talkerInfo)
            setContentView(holder.tvContent, info)
            setSendStatus(holder.pbLoading, holder.ivError, info)
            showTimeView(holder.tvTime, info)
        }
    }

    private fun showTimeView(tvTime: TextView?, info: ChatInfo) {
        if (info.isShowTime) {
            tvTime!!.visibility = View.VISIBLE
            val time = STimeUtils.parseChatTime(info.send_time)
            tvTime.text = time
        } else {
            tvTime!!.visibility = View.GONE
        }
    }

    private fun setSendStatus(pbLoading: ProgressBar?, ivError: ImageView?, info: ChatInfo) {
        val requestCode = info.requestCode
        if (requestCode > 0) {
            pbLoading!!.visibility = View.VISIBLE
        } else {
            pbLoading!!.visibility = View.INVISIBLE
        }
        Logs.i("isEndError" + info.isSendError)
        ivError!!.visibility = if (info.isSendError) View.VISIBLE else View.GONE
        ivError.setOnClickListener {
            val baseTipsDialog = BaseTipsDialog(context, "重发该消息", object : BaseTipsDialog.DialogAfterClickListener {
                override fun onSure() {
                    items.remove(info)
                    //((ChatActivity) context).sendChatMsg(info.getText());
                }

                override fun onCancel() {}
            })
            baseTipsDialog.hideTitle()
            baseTipsDialog.setOkContent("重发")
            baseTipsDialog.show()
        }
    }

    private fun setContentView(tvContent: TextView?, info: ChatInfo) {
        STextUtils.setNotEmptText(tvContent, info.text)
    }

    private fun setUserInfo(ivAvatar: RoundAngleImageView?, info: UserInfo) {
        SUtils.setPic(ivAvatar, info.avatar)
        ivAvatar!!.setOnClickListener { }
    }


    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        if (item is ChatInfo) {
            val userInfo = item.user
            return if (userInfo.id != null && userInfo.id == selfInfo.id) {
                VIEW_TYPE_SELF
            } else {
                VIEW_TYPE_OTHER
            }
        }
        return SRecycleMoreAdapter.ViewType.TYPE_CONTENT
    }

    internal class SelfHolder(view: View) : RecyclerView.ViewHolder(view) {
        @BindView(R.id.iv_avatar)
        var ivAvatar: RoundAngleImageView? = null
        @BindView(R.id.tv_content)
        var tvContent: TextView? = null
        @BindView(R.id.pb_loading)
        var pbLoading: ProgressBar? = null
        @BindView(R.id.iv_error)
        var ivError: ImageView? = null
        @BindView(R.id.tv_time)
        var tvTime: TextView? = null

        init {
            ButterKnife.bind(this, view)
        }
    }

    internal class OtherHolder(view: View) : RecyclerView.ViewHolder(view) {
        @BindView(R.id.iv_avatar)
        var ivAvatar: RoundAngleImageView? = null
        @BindView(R.id.tv_content)
        var tvContent: TextView? = null
        @BindView(R.id.pb_loading)
        var pbLoading: ProgressBar? = null
        @BindView(R.id.iv_error)
        var ivError: ImageView? = null
        @BindView(R.id.rl_left)
        var rlLeft: RelativeLayout? = null
        @BindView(R.id.tv_time)
        var tvTime: TextView? = null

        init {
            ButterKnife.bind(this, view)
        }
    }

    companion object {

        private val VIEW_TYPE_SELF = SRecycleMoreAdapter.ViewType.INSERT_TYPE2//自己
        private val VIEW_TYPE_OTHER = SRecycleMoreAdapter.ViewType.INSERT_TYPE3//别人
    }
}

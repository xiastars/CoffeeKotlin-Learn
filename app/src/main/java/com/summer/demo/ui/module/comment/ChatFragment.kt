package com.summer.demo.ui.module.comment

import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import com.summer.demo.R
import com.summer.demo.bean.UserInfo
import com.summer.demo.module.base.BaseFragment
import com.summer.demo.view.CommonSureView5
import com.summer.helper.server.PostData
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SUtils
import com.summer.helper.view.NRecycleView
import java.util.*

/**
 * @Description: 聊天界面
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/11 14:15
 */
class ChatFragment : BaseFragment(), View.OnClickListener {

    internal val svContainer: NRecycleView by Bind(R.id.sv_container)
    internal val btnSend: CommonSureView5 by Bind(R.id.sv_container,true)
    internal val edtComment: EditText by Bind(R.id.sv_container)
    internal val rlEditParent: RelativeLayout by Bind(R.id.sv_container)
    internal val llParent: RelativeLayout by Bind(R.id.sv_container)
    internal val rlCommentParent: RelativeLayout by Bind(R.id.sv_container)
    internal var userId = ""
    internal lateinit var groupId: String

    internal var chatAdapter: ChatAdapter? = null

    internal val REQUEST_CHAT = 0

    internal var preTop: Int = 0

    /**
     * 发布私聊
     *
     * @param s
     */
    internal var reqeustCommentCode = 10

    override fun setContentView(): Int {
        return R.layout.fragment_chat
    }

    override fun initView(view: View) {
        userId = "1001"
        groupId = "1002"
        Logs.i("---------")
        setRecycleView()
    }

    fun setRecycleView() {
        svContainer!!.setList()
        // 上拉自动加载
        svContainer!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(nvContainerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(nvContainerView, dx, dy)

                val manager = nvContainerView.layoutManager as LinearLayoutManager?
                // last >= totalCount - 2表示剩余2个item是自动加载，可自己设置
                // dy>0表示向下滑动
                if (!ViewCompat.canScrollVertically(svContainer, -1) && dy < 0) {
                    pageIndex++
                    loadData()
                }
            }
        })
        initEditView()
        loadData()
    }

    private fun initEditView() {
        edtComment!!.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (preTop == 0) {
                preTop = top

            } else if (preTop < top) {

            } else {
                scollToBottom()
            }
        }
        edtComment!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                val content = s.toString()
                btnSend!!.changeStyle(content.trim { it <= ' ' }.length > 0)
            }
        })
        myHandlder.postDelayed({ SUtils.showSoftInpuFromWindow(edtComment) }, 300)
    }

    public override fun loadData() {
        Logs.i("lastID:$lastId")
        if (pageIndex > 0 && TextUtils.isEmpty(lastId)) {
            return
        }
        val parameter = PostData.getPostParameters(context)
        parameter.put("group_id", groupId)
        parameter.put("limit", 20)
        parameter.put("message_id", lastId)
        parameter.putLog("聊天")
        parameter.setShowVirtualData()
        getDataTwo(REQUEST_CHAT, ChatTopInfo::class.java, parameter, "dynamics/chats/users/$userId")
    }

    override fun dealDatas(requestCode: Int, obj: Any) {
        when (requestCode) {
            REQUEST_CHAT -> {
                val chatTopInfo = obj as ChatTopInfo
                Logs.i("----------$chatTopInfo")
                if (chatAdapter == null) {
                    val receiver = chatTopInfo.receiver
                    if (receiver != null) {
                        chatAdapter = ChatAdapter(context!!, chatTopInfo.sender, chatTopInfo.receiver)
                        svContainer!!.adapter = chatAdapter
                    }
                }
                val newDatas = chatTopInfo.list

                if (!SUtils.isEmptyArrays(newDatas) && newDatas!!.size <= 20) {
                    lastId = newDatas[0].id
                }
                val size = newDatas!!.size
                if (pageIndex > 0) {
                    var infos: MutableList<ChatInfo>? = chatAdapter!!.items as MutableList<ChatInfo>
                    if (infos == null) {
                        infos = ArrayList()
                    }
                    if (newDatas != null && pageIndex > 0) {
                        for (i in newDatas.indices) {
                            infos.add(0, newDatas[newDatas.size - i - 1])
                        }
                        val manager = svContainer!!.layoutManager as LinearLayoutManager?
                        val last = manager!!.findLastCompletelyVisibleItemPosition()
                        chatAdapter!!.notifyDataChanged(infos)
                        svContainer!!.scrollToPosition(size + last)
                    }

                } else {
                    chatAdapter!!.notifyDataChanged(newDatas)
                }
            }
        }
        if (requestCode >= 10) {
            //cancelLoading();
            val info = findChatInfoByCode(requestCode)
            info!!.requestCode = 0
            info.isSendError = false
            chatAdapter!!.notifyDataSetChanged()
        }
        if (pageIndex == 0) {
            scollToBottom()
        }
    }

    override fun dealErrors(requstCode: Int, requestType: String, errString: String) {
        super.dealErrors(requstCode, requestType!!, errString)
        //cancelLoading();
        if (requestType != null && requestType == "40306") {

            return
        }
        if (requstCode >= 10) {
            val info = findChatInfoByCode(requstCode)
            info!!.isSendError = true
            info.requestCode = 0
            chatAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_send -> {
                sendChatMsg(edtComment!!.text.toString())
                edtComment!!.setText("")
                btnSend!!.changeStyle(false)
            }
        }//SUtils.hideSoftInpuFromWindow(edtComment);
        //showLoading();
    }

    private fun findChatInfoByCode(requestCode: Int): ChatInfo? {
        val infos = chatAdapter!!.items as List<ChatInfo> ?: return null
        for (info in infos) {
            if (info.requestCode == requestCode) {
                return info
            }
        }
        return null

    }

    fun sendChatMsg(s: String) {
        if (chatAdapter == null) {
            SUtils.makeToast(context, "初始化失败")
            return
        }
        reqeustCommentCode++
        val chatInfo = ChatInfo()
        chatInfo.text = s
        chatInfo.type = 1
        val UserInfo = UserInfo()
        UserInfo.id = "mine"
        chatInfo.user = UserInfo
        chatInfo.send_time = System.currentTimeMillis() / 1000
        chatInfo.requestCode = reqeustCommentCode
        var infos: MutableList<ChatInfo>? = chatAdapter!!.items as MutableList<ChatInfo>
        if (infos == null) {
            infos = ArrayList()
        }
        infos.add(chatInfo)
        chatAdapter!!.notifyDataSetChanged()
        scollToBottom()
        val parameter = PostData.getPostParameters(context)
        parameter.put("flag", System.currentTimeMillis())
        parameter.put("text", s)
        parameter.put("send_time", System.currentTimeMillis())
        parameter.put("group_id", groupId)
        parameter.putLog("发送聊天")
        postDataTwo(reqeustCommentCode, ChatTopInfo::class.java, parameter, "dynamics/chats/users/$userId")
    }

    private fun scollToBottom(position: Int) {
        if (chatAdapter == null || chatAdapter!!.items == null) {
            return
        }
        var infos: List<ChatInfo>? = chatAdapter!!.items as List<ChatInfo>
        if (infos == null) {
            infos = ArrayList()
        }
        svContainer!!.scrollToPosition(infos.size - 20 * pageIndex)

    }

    private fun scollToBottom() {
        if (chatAdapter == null || chatAdapter!!.items == null) {
            return
        }
        var infos: List<ChatInfo>? = chatAdapter!!.items as List<ChatInfo>
        if (infos == null) {
            infos = ArrayList()
        }
        svContainer!!.scrollToPosition(infos.size - 1)

    }
}

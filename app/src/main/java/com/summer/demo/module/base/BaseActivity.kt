package com.summer.demo.module.base

import android.content.Intent
import com.summer.helper.adapter.SRecycleMoreAdapter
import com.summer.helper.recycle.SmartRecyclerView
import com.summer.helper.utils.ReceiverUtils
import com.summer.helper.view.SRecycleView

/**
 * Created by xiaqiliang on 2017/3/24.
 */
abstract class BaseActivity : BaseRequestActivity() {
    protected var sRecycleView: SmartRecyclerView? = null

    internal var receiverUtils: ReceiverUtils? = null

    val handleTime: Long
        get() = baseHelper!!.handleTime


    override fun initPresenter() {

    }

    fun setSRecyleView(svContainer: SRecycleView) {
        this.sRecycleView = svContainer
        svContainer.setList()

        svContainer.setOnRefreshListener { refreshLayout ->
            fromId = 0
            pageIndex = 0
            lastId = null
            isRefresh = true
            loadData()

        }
        svContainer.setOnLoadMoreListener { refreshLayout ->
                pageIndex++
                isRefresh = true
                loadData()
        }

    }

    fun refreshData() {
        pageIndex = 0
        lastId = null
        loadData()
    }

    protected fun setsRecycleViewAdapter(adapter: SRecycleMoreAdapter) {
        sRecycleView!!.adapter = adapter
    }

    fun handleViewData(obj: Any) {
        if (sRecycleView == null) {
            return
        }
        baseHelper!!.handleViewData(obj, sRecycleView, pageIndex)
    }


    fun showLoadingDialogWithRequest(show: Boolean) {
        baseHelper!!.isShowLoading = show
    }

    /**
     * 注册广播
     *
     * @param action
     */
    protected fun initBroadcast(vararg action: String) {
        if (receiverUtils != null) {
            receiverUtils!!.unRegisterReceiver()
        }
        receiverUtils = ReceiverUtils(this)
        receiverUtils!!.setActionsAndRegister(*action)
        receiverUtils!!.setOnReceiverListener(ReceiverUtils.ReceiverListener { action, intent ->
            if (context == null) {
                return@ReceiverListener
            }
            onMsgReceiver(action, intent)
        })
    }

    protected fun sendBroadcast(action: String) {
        val intent = Intent()
        intent.action = action
        sendBroadcast(intent)
    }

    protected fun onMsgReceiver(action: String, intent: Intent) {
        if (context == null) {
            return
        }
    }

    override fun loadData() {}

    override fun finishLoad() {
        if (sRecycleView != null) {
            finishLoad(sRecycleView)
        }

    }

    protected fun finishLoad(scrollView: SmartRecyclerView?) {
        isRefresh = false
        if (scrollView != null) {
            if (pageIndex == 0) {
                scrollView.finishRefresh()
            } else {
                scrollView.finishLoadMore()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        context = null
        if (receiverUtils != null) {
            receiverUtils!!.unRegisterReceiver()
        }
    }
}
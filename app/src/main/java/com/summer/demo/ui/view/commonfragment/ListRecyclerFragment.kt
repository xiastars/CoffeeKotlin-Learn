package com.summer.demo.ui.view.commonfragment

import android.view.View
import android.widget.Toast
import com.summer.demo.R
import com.summer.demo.adapter.CommonAdapter
import com.summer.demo.module.base.BaseFragment
import com.summer.helper.recycle.SmartRecyclerView
import com.summer.helper.utils.Logs
import java.util.*

/**
 * @Description: ListView
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/17 11:16
 */
class ListRecyclerFragment : BaseFragment() {
    private val nvContainer: SmartRecyclerView by Bind(R.id.nv_container)

    internal lateinit var commonAdapter: CommonAdapter

    override fun initView(view: View) {
        //设置为List样式
        nvContainer!!.setList()
        commonAdapter = CommonAdapter(context!!)
        nvContainer!!.adapter = commonAdapter
        //开启自动加载功能（非必须）
        nvContainer!!.setEnableAutoLoadMore(true)
        nvContainer!!.setOnRefreshListener { refreshLayout ->
            Logs.i("---------")
            refreshLayout.layout.postDelayed({
                val datas = ArrayList<String>()
                for (i in 0..29) {
                    datas.add("萍水相逢萍水散，各自天涯各自安。")
                }
                Logs.i("-----")
                commonAdapter.notifyDataChanged(datas)
                refreshLayout.finishRefresh()
                refreshLayout.resetNoMoreData()//setNoMoreData(false);
            }, 2000)
        }
        nvContainer!!.setOnLoadMoreListener { refreshLayout ->
            refreshLayout.layout.postDelayed({
                if (commonAdapter.itemCount > 30) {
                    Toast.makeText(context, "数据全部加载完毕", Toast.LENGTH_SHORT).show()
                    refreshLayout.finishLoadMoreWithNoMoreData()//将不会再次触发加载更多事件
                } else {
                    val datas = ArrayList<String>()
                    for (i in 0..29) {
                        datas.add("萍水相逢萍水散，各自天涯各自安。")
                    }
                    commonAdapter.notifyDataChanged(datas)
                    refreshLayout.finishLoadMore()
                }
            }, 2000)
        }

        //触发自动刷新
        nvContainer!!.autoRefresh()
    }

    public override fun loadData() {

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.fragment_list_rec
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

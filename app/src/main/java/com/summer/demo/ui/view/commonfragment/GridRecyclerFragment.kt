package com.summer.demo.ui.view.commonfragment

import android.view.View
import android.widget.Toast
import com.summer.demo.R
import com.summer.demo.adapter.CommonAdapter
import com.summer.demo.module.base.BaseFragment
import com.summer.helper.recycle.SmartRecyclerView
import java.util.*

/**
 * @Description: ListView
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/17 11:16
 */
class GridRecyclerFragment : BaseFragment() {
    private val nvContainer: SmartRecyclerView by Bind(R.id.nv_container)


    internal lateinit var commonAdapter: CommonAdapter

    override fun initView(view: View) {
        //设置为List样式
        nvContainer.setGridView(3)
        commonAdapter = CommonAdapter(context!!)
        nvContainer.setAdapter(commonAdapter)
        //开启自动加载功能（非必须）
        nvContainer.setEnableAutoLoadMore(false)
        nvContainer.setOnRefreshListener { refreshLayout ->
            refreshLayout.layout.postDelayed({
                commonAdapter.notifyDataChanged(initData())
                refreshLayout.finishRefresh()
                refreshLayout.resetNoMoreData()//setNoMoreData(false);
            }, 2000)
        }
        nvContainer.setOnLoadMoreListener { refreshLayout ->
            refreshLayout.layout.postDelayed({
                if (commonAdapter.itemCount > 30) {
                    Toast.makeText(context, "数据全部加载完毕", Toast.LENGTH_SHORT).show()
                    refreshLayout.finishLoadMoreWithNoMoreData()//将不会再次触发加载更多事件
                } else {

                    refreshLayout.finishLoadMore()
                }
            }, 2000)
        }

        //触发自动刷新
        //nvContainer.autoRefresh();
    }

    private fun initData(): List<Void> {
        for (i in 0..10){

        }
        return Arrays.asList<Void>(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
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

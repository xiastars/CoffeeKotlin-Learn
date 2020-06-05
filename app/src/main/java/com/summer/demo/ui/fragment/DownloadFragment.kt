package com.summer.demo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.summer.demo.R
import com.summer.demo.adapter.DownloadAdapter
import com.summer.demo.bean.BookBean
import com.summer.demo.server.CommonDBType
import com.summer.helper.db.CommonService
import com.summer.helper.server.SummerParameter
import com.summer.helper.view.SRecycleView
import java.util.*

/**
 * Created by xiastars@vip.qq.com on 2016年12月12日 14:08.
 */

class DownloadFragment : BaseSimpleFragment(), View.OnClickListener {
    internal var url = "http://appstore.kidspad.zuoyegou.com/search?qs=a"

    internal lateinit var mGridView: SRecycleView

    internal lateinit var mAdapter: DownloadAdapter
    internal var mService: CommonService? = null

    internal var mBooks: List<BookBean>? = null

    internal lateinit var lasttime: String
    internal var mFreshing: Boolean = false
    internal var pageNum = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_download, null)
        initView(view)
        return view
    }

    fun initView(view: View) {
        mService = CommonService(context)
        mGridView = view.findViewById<View>(R.id.rv_books) as SRecycleView
        mGridView.setList()
        mAdapter = DownloadAdapter(context!!)
        mGridView.adapter = mAdapter

        //下拉刷新与上拉加载回调
        mGridView.setOnRefreshListener { refreshLayout ->
            refresh()

        }
        mGridView.setOnLoadMoreListener { refreshLayout ->
            onListLoad()
        }
        //从数据库获取缓存，如果有，则先刷新界面，然后下载
        mBooks = mService!!.getListData(CommonDBType.DOWNLOAD_DATA) as List<BookBean>
        if (null != mBooks && 0 < mBooks!!.size) {
            mAdapter.notifyDataChanged(mBooks!!)
        }
        requestData()
    }


    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        if (mService != null) {
            mService!!.closeDB()
        }
    }

    private fun refresh() {
        lasttime = ""
        pageNum = 1
        requestData()
    }

    fun notifyData() {
        lasttime = ""
        pageNum = 1
        requestData()
    }

    private fun requestData() {
        val params = SummerParameter()
        params.putLog("请求下载数据")
        /*   EasyHttp.get(context, url, RequestBook.class, params,
                new RequestCallback<RequestBook>() {

                    @Override
                    public void done(RequestBook t) {
                        Logs.i("xia", t + ",,");
                        if (null != t && null != t.getData()) {
                            List<BookBean> infos = t.getData();
                            Logs.i("xia", infos + ",,");
                            if (null != infos && 0 < infos.size()) {
                                if (null != mBooks && !TextUtils.isEmpty(lasttime)) {
                                    mBooks.addAll(infos);
                                } else {
                                    mBooks = infos;
                                }
                                mService.insert(CommonDBType.DOWNLOAD_DATA, mBooks);
                                mFreshing = false;
                                notifyDatas(mBooks);
                            } else {
                                if (pageNum > 1) {
                                    SUtils.makeToast(context, "没有更多数据了");
                                }
                            }
                        } else {
                            notifyDatas(mBooks);
                        }
                        if (pageNum > 1) {
                            mGridView.finishPullUpRefresh();
                        } else {
                            mGridView.finishPullDownRefresh();
                        }
                        pageNum++;
                    }

                    @Override
                    public void onError(int errorCode, String errorStr) {

                    }
                });*/
    }

    fun onListLoad() {
        requestData()
    }

    private fun notifyDatas(mBooks: List<BookBean>?) {
        if (mBooks != null && mBooks.size > 0) {
            mAdapter.notifyDataChanged(mBooks)
        } else {
            mAdapter.notifyDataChanged(ArrayList<BookBean>())
            //            empty.setVisibility(View.VISIBLE);
        }
    }

    fun onRefresh() {
        mBooks = null
        lasttime = ""
        pageNum = 1
        requestData()
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {

        }

    }

}

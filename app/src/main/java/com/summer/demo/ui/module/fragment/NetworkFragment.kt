package com.summer.demo.ui.module.fragment

import android.view.View
import com.summer.demo.R
import com.summer.demo.module.base.BaseFragment
import com.summer.demo.ui.module.adapter.NetworkMovieAdapter
import com.summer.demo.ui.module.bean.MovieResp
import com.summer.helper.server.SummerParameter
import com.summer.helper.view.SRecycleView

/**
 * 网络请求演示
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2020/6/5 9:22
 */
class NetworkFragment : BaseFragment(){
    private val svContainer : SRecycleView by Bind(R.id.sv_container)
    private lateinit var movieAdapter:NetworkMovieAdapter

    val REQUEST_MOVIE :Int = 1//请求电影


    override fun initView(view: View) {
        svContainer.setGridView(3)
        movieAdapter = NetworkMovieAdapter(context!!)
        svContainer.adapter = movieAdapter
        //刷新加载与翻页加载已写在里面
        setSmartRecyclerView(svContainer)
        loadData()
    }

    override fun loadData() {
        requestMovieData()
    }

    fun requestMovieData(){
        val parameter = SummerParameter()
        parameter.put("tag","热门")
        parameter.put("page_limit",50)
        parameter.put("type","movie")
        parameter.put("page_start",pageIndex)
        postData(REQUEST_MOVIE, MovieResp::class.java,parameter,"https://movie.douban.com/j/search_subjects")
    }

    override fun dealDatas(requestType: Int, obj: Any) {

        when(requestType){
            REQUEST_MOVIE ->{
                handleViewData(obj)
            }
        }
    }

    override fun setContentView(): Int {
        return R.layout.view_srecyleview
    }

    override fun onClick(v: View?) {

    }

}
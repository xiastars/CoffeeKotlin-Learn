package com.summer.demo.ui.module.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.summer.demo.R
import com.summer.demo.ui.module.bean.MovieInfo
import com.summer.helper.adapter.SRecycleMoreAdapter
import com.summer.helper.utils.STextUtils
import com.summer.helper.utils.SUtils

/**
 *
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2020/6/5 9:36
 */

class NetworkMovieAdapter(context: Context):SRecycleMoreAdapter(context ){
    override fun setContentView(parent: ViewGroup?): RecyclerView.ViewHolder {
        return ViewHolder(createHolderView(R.layout.item_network_movie,parent))
    }

    override fun bindContentView(holder: RecyclerView.ViewHolder?, position: Int) {
        val hd :ViewHolder = holder as ViewHolder
        val movie:MovieInfo = items[position] as MovieInfo
        SUtils.setPic(hd.ivPic,movie.cover)
        val content:String = movie.title +" " + movie.rate
        STextUtils.setSpannableView(content,hd.tvContent,movie.title.length,content.length,getResourceColor(R.color.yellow_47))
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvContent : TextView = view.findViewById(R.id.tv_title)
        val ivPic : ImageView = view.findViewById(R.id.iv_pic)
    }

}
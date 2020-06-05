package com.summer.demo.module.video

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.summer.demo.R
import com.summer.demo.module.video.util.VideoEditInfo
import java.util.*

/**
 * ================================================
 * 作    者：顾修忠-guxiuzhong@youku.com/gfj19900401@163.com
 * 版    本：
 * 创建日期：2017/3/2-下午7:46
 * 描    述：
 * 修订历史：
 * ================================================
 */

class VideoEditAdapter(private val context: Context, private val itemW: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val lists = ArrayList<VideoEditInfo>()
    private val inflater: LayoutInflater

    init {
        this.inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EditViewHolder(inflater.inflate(R.layout.video_item, parent, false))
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    private inner class EditViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img: ImageView

        init {
            img = itemView.findViewById<View>(R.id.id_image) as ImageView
            val layoutParams = img.layoutParams as LinearLayout.LayoutParams
            layoutParams.width = itemW
            img.layoutParams = layoutParams
        }
    }

    fun addItemVideoInfo(info: VideoEditInfo) {
        lists.add(info)
        notifyItemInserted(lists.size)
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val viewHolder = p0 as EditViewHolder
        Glide.with(context)
                .load("file://" + lists[p1].path!!)
                .into(viewHolder.img)
    }
}

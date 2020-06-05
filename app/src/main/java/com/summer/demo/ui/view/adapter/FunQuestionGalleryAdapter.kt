package com.summer.demo.ui.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.summer.demo.R
import com.summer.demo.bean.BookBean
import com.summer.helper.utils.SUtils
import com.summer.helper.view.RoundAngleImageView

class FunQuestionGalleryAdapter(context: Context) : GalleryAdapter(context) {

    internal var mLibraryInfos: List<BookBean>? = null

    override val createViewHolderId: Int
        get() = R.layout.item_fun_question

    override val itemRealCount: Int
        get() = if (mLibraryInfos == null) 0 else mLibraryInfos!!.size

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, itemView: View?, viewType: Int): ViewHolder {
        return ViewHolder(itemView!!)
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val vh = holder as ViewHolder
        SUtils.setPicWithHolder(vh.ivNav, "http://b.hiphotos.baidu.com/image/pic/item/908fa0ec08fa513db777cf78376d55fbb3fbd9b3.jpg",
                R.drawable.default_icon_triangle)
        vh.itemView.setOnClickListener { }
    }

    fun notifyDataSetChanged(infos: List<*>) {
        mLibraryInfos = infos as List<BookBean>
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var ivNav: RoundAngleImageView = itemView.findViewById(R.id.iv_nav)

    }
}
package com.summer.demo.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.summer.demo.R
import com.summer.demo.bean.ModuleInfo
import com.summer.helper.adapter.SRecycleMoreAdapter
import com.summer.helper.listener.OnSimpleClickListener

/**
 * 为首页的标题设置adapter
 *
 * @编者 xiastars
 */
class CommonGridAdapter : SRecycleMoreAdapter {

    internal var onSimpleClickListener: OnSimpleClickListener? = null

    constructor(context: Context) : super(context) {}


    constructor(context: Context, onSimpleClickListener: OnSimpleClickListener) : super(context) {
        this.onSimpleClickListener = onSimpleClickListener
    }

    override fun setContentView(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(createHolderView(R.layout.item_grid, parent))
    }

    override fun bindContentView(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as ViewHolder
        val moduleInfo = items[position] as ModuleInfo
        vh.content!!.text = moduleInfo.title
        vh.ivBg!!.setBackgroundResource(moduleInfo.res)
        vh.itemView.setOnClickListener {
            if (onSimpleClickListener != null) {
                onSimpleClickListener!!.onClick(moduleInfo.pos)
            }
        }

    }

    internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var content: TextView = view.findViewById(R.id.name)
        var ivBg: ImageView = view.findViewById(R.id.iv_bg)
    }


}
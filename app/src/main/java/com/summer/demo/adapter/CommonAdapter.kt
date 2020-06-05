package com.summer.demo.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.summer.demo.R
import com.summer.helper.adapter.SRecycleMoreAdapter
import com.summer.helper.listener.OnSimpleClickListener

/**
 * 为首页的标题设置adapter
 *
 * @编者 xiastars
 */
class CommonAdapter : SRecycleMoreAdapter {

    internal var onSimpleClickListener: OnSimpleClickListener? = null

    constructor(context: Context) : super(context) {}


    constructor(context: Context, onSimpleClickListener: OnSimpleClickListener) : super(context) {
        this.onSimpleClickListener = onSimpleClickListener
    }

    override fun setContentView(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(createHolderView(R.layout.item_main, parent))
    }

    override fun bindContentView(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as ViewHolder
        vh.content!!.text = (position + 1).toString() + ". " + items[position]
        /*
         * 根据奇偶数设置不同的颜色
         */
        /*        if (position % 2 == 0) {
            vh.content.setBackgroundColor(getResourceColor(R.color.pink));
        } else {
            vh.content.setBackgroundColor(getResourceColor(R.color.green));
        }*/

        vh.itemView.setOnClickListener {
            if (onSimpleClickListener != null) {
                onSimpleClickListener!!.onClick(position)
            }
        }

    }

    internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var content: TextView = view.findViewById(R.id.name)

    }


}
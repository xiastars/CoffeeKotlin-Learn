package com.summer.demo.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.summer.demo.R
import com.summer.demo.constant.TestData
import com.summer.helper.adapter.SRecycleAdapter
import com.summer.helper.listener.OnSimpleClickListener
import com.summer.helper.utils.SUtils

class CommonListAdapter : SRecycleAdapter {

    internal var listener: OnSimpleClickListener? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, listener: OnSimpleClickListener) : super(context) {
        this.listener = listener

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CommonListAdapter.ViewHolder(createHolderView(R.layout.item_view_list, parent))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as CommonListAdapter.ViewHolder

        vh.itemView.setOnClickListener {
            if (listener != null) {
                listener!!.onClick(position)
            }
            SUtils.makeToast(context, "点击了第" + position + "行")
        }
        //静态页面，数据写死
        val data = TestData.imgs[position]
        vh.ivImg!!.setBackgroundResource(data)
        vh.tvContent!!.text = TestData.contents[position]
    }

    //重写数量
    override fun getItemCount(): Int {
        return TestData.imgs.size
    }

    internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var ivImg: ImageView = view.findViewById(R.id.iv_img)
        var tvContent: TextView = view.findViewById(R.id.tv_content)

    }

}

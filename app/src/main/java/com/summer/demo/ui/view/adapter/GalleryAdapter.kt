package com.summer.demo.ui.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.summer.demo.view.GalleryView

abstract class GalleryAdapter(protected var mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected var mInflater: LayoutInflater
    private var mGalleryWidth: Int = 0
    private var mItemRawWidth: Int = 0
    private var galleryView: GalleryView? = null
    private var mStartCalculate: Boolean = false

    /**
     * 获取item布局id
     */
    abstract val createViewHolderId: Int

    /**
     * 获取item数量
     */
    abstract val itemRealCount: Int

    init {
        mInflater = LayoutInflater.from(mContext)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        galleryView = recyclerView as GalleryView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (mGalleryWidth == 0) {
            mGalleryWidth = parent.width
        }
        var view: View? = null
        if (viewType == TYPE_REAL.toInt()) {
            view = mInflater.inflate(createViewHolderId, parent, false)
            return onCreateItemViewHolder(parent, view, viewType)
        } else {
            view = View(mContext)
            view.layoutParams = RecyclerView.LayoutParams(ViewGroup
                    .LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
            return object : RecyclerView.ViewHolder(view) {

            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {

        if (mItemRawWidth == 0 && getItemViewType(position) == TYPE_REAL.toInt() && !mStartCalculate) {
            mStartCalculate = true
            holder.itemView.post {
                val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
                mItemRawWidth = params.width + params.leftMargin + params.rightMargin
                notifyItemChanged(0)
                notifyItemChanged(itemCount - 1)
            }
        }
        if (getItemViewType(position) == TYPE_REAL.toInt()) {
            onBindItemViewHolder(holder, position - 1)
        } else {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            if (params.width <= 0 && mGalleryWidth > 0 && mItemRawWidth > 0) {
                params.width = (mGalleryWidth - mItemRawWidth) / 2
                holder.itemView.layoutParams = params
            }
            /*int w = (mGalleryWidth - mItemRawWidth)/2;
            if (params.width != w){
                params.width = w;
                holder.itemView.setLayoutParams(params);
            }*/
        }
    }

    override fun getItemViewType(position: Int): Int {
        return (if (position == 0 || position == itemCount - 1) TYPE_SPAN else TYPE_REAL).toInt()
    }

    override fun getItemCount(): Int {
        return itemRealCount + 2
    }

    abstract fun onCreateItemViewHolder(parent: ViewGroup, itemView: View?, viewType: Int): RecyclerView.ViewHolder

    abstract fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int)

    companion object {

        private val TYPE_REAL: Short = 0
        private val TYPE_SPAN: Short = 1
    }
}
package com.summer.demo.module.album.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.ToggleButton
import butterknife.BindView
import butterknife.ButterKnife
import com.summer.demo.R
import com.summer.demo.module.album.util.ImageItem
import com.summer.helper.adapter.SRecycleAdapter
import com.summer.helper.utils.SUtils
import java.util.*


/**
 * 这个是显示一个文件夹里面的所有图片时用的适配器
 *
 * @author zhangqian
 */
class AlbumGridViewAdapter(private val mContext: Context, private var dataList: ArrayList<ImageItem>?, private val selectedDataList: ArrayList<ImageItem>) : SRecycleAdapter(mContext) {
    internal val TAG = javaClass.simpleName

    private var mOnItemClickListener: OnItemClickListener? = null

    override fun getItemCount(): Int {
        return if (dataList != null) dataList!!.size else 0
    }

    fun notifyDatas(dataList: ArrayList<ImageItem>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.plugin_camera_select_imageview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        val path: String?
        if (dataList != null && dataList!!.size > position)
            path = dataList!![position].imagePath
        else
            path = "camera_default"
        val item = dataList!![position]
        if (path != null) {
            if (path.contains("camera_default")) {
                viewHolder.imageView!!.setImageResource(R.drawable.plugin_camera_no_pictures)
            } else {
                SUtils.setPic(viewHolder.imageView, if (item.thumbnailPath != null) item.thumbnailPath else item.imagePath)
            }
        }

        viewHolder.toggleButton!!.tag = position
        viewHolder.choosedbt!!.tag = position
        viewHolder.toggleButton!!.setOnClickListener(ToggleClickListener(viewHolder.choosedbt!!))
        if (selectedDataList.contains(dataList!![position])) {
            viewHolder.toggleButton!!.isChecked = true
            viewHolder.choosedbt!!.visibility = View.VISIBLE
        } else {
            viewHolder.toggleButton!!.isChecked = false
            viewHolder.choosedbt!!.visibility = View.GONE
        }
    }

    private inner class ToggleClickListener(internal var chooseBt: ImageView) : OnClickListener {

        override fun onClick(view: View) {
            if (view is ToggleButton) {
                val position = view.tag as Int
                if (dataList != null && mOnItemClickListener != null && position < dataList!!.size) {
                    mOnItemClickListener!!.onItemClick(view, position, view.isChecked, chooseBt)
                }
            }
        }
    }

    fun setOnItemClickListener(l: OnItemClickListener) {
        mOnItemClickListener = l
    }

    interface OnItemClickListener {
        fun onItemClick(view: ToggleButton, position: Int, isChecked: Boolean, chooseBt: ImageView)
    }

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @BindView(R.id.image_view)
        var imageView: ImageView? = null
        @BindView(R.id.toggle_button)
        var toggleButton: ToggleButton? = null
        @BindView(R.id.choosedbt)
        var choosedbt: ImageView? = null
        @BindView(R.id.toggle)
        var toggle: RelativeLayout? = null
        @BindView(R.id.rl_parent)
        var rlParent: RelativeLayout? = null

        init {
            ButterKnife.bind(this, view)
            val params = imageView!!.layoutParams as RelativeLayout.LayoutParams
            params.width = (SUtils.screenWidth - SUtils.getDip(mContext, 12)) / 3
            params.height = params.width
        }
    }
}

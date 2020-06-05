package com.summer.demo.module.album.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.summer.demo.R
import com.summer.demo.module.album.AlbumActivity
import com.summer.demo.module.album.ShowAllPhotoActivity
import com.summer.demo.module.album.util.ImageItem
import com.summer.helper.adapter.SRecycleMoreAdapter
import com.summer.helper.utils.JumpTo
import com.summer.helper.utils.SUtils
import java.util.*

/**
 * Created by xiaqiliang on 2017/3/22.
 */
class FolersAdapter(context: Context, internal var tempSelectBitmap: ArrayList<ImageItem>) : SRecycleMoreAdapter(context) {

    override fun setContentView(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_area_exclusive, parent, false)
        return TabViewHolder(view)
    }

    override fun bindContentView(holder: RecyclerView.ViewHolder, position: Int) {
        val hd = holder as TabViewHolder
        hd.rlParent.setOnClickListener {
            (context as AlbumActivity).hideFolderView()
            ShowAllPhotoActivity.dataList = (AlbumActivity.contentList[position].imageList as ArrayList<ImageItem>?)!!
            val intent = Intent()
            val folderName = AlbumActivity.contentList[position].bucketName
            intent.putExtra("folderName", folderName)
            intent.putExtra(JumpTo.TYPE_OBJECT, tempSelectBitmap)
            intent.setClass(context, ShowAllPhotoActivity::class.java)
            (context as Activity).startActivityForResult(intent, 5)
        }
        val path: String
        if (AlbumActivity.contentList[position].imageList != null) {
            path = AlbumActivity.contentList[position].imageList!![0].imagePath
            hd.tvTitle.text = AlbumActivity.contentList[position].bucketName
            hd.tvJoin.text = "" + AlbumActivity.contentList[position].count

        } else {
            path = "android_hybrid_camera_default"
        }
        if (path.contains("android_hybrid_camera_default"))
            hd.ivIcon.setImageResource(R.drawable.plugin_camera_no_pictures)
        else {
            val item = AlbumActivity.contentList[position].imageList!![0]
            SUtils.setPicWithHolder(hd.ivIcon, item.imagePath, R.drawable.default_icon_triangle)
        }
    }

    private inner class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivIcon: ImageView
        val tvTitle: TextView
        val tvJoin: TextView
        val rlParent: LinearLayout

        init {
            ivIcon = itemView.findViewById<View>(R.id.iv_nav) as ImageView
            tvTitle = itemView.findViewById<View>(R.id.tv_title) as TextView
            tvJoin = itemView.findViewById<View>(R.id.tv_join) as TextView
            rlParent = itemView.findViewById<View>(R.id.rl_parent) as LinearLayout
        }
    }

}

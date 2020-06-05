package com.summer.demo.module.album.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.summer.demo.R
import com.summer.demo.module.album.AlbumActivity
import com.summer.demo.module.album.ShowAllPhotoActivity
import com.summer.demo.module.album.util.ImageItem
import com.summer.helper.utils.JumpTo
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SUtils
import java.util.*

/**
 * 这个是显示所有包含图片的文件夹的适配器
 *
 * @author zhangqian
 */
class FolderAdapter(c: Context, internal var tempSelectBitmap: ArrayList<ImageItem>) : BaseAdapter() {

    private var mContext: Context? = null
    private var mIntent: Intent? = null
    private var dm: DisplayMetrics? = null
    internal val TAG = javaClass.simpleName
    lateinit var holder: ViewHolder

    init {
        init(c)
    }

    // 初始化
    fun init(c: Context) {
        mContext = c
        mIntent = (mContext as Activity).intent
        dm = DisplayMetrics()
        (mContext as Activity).windowManager.defaultDisplay.getMetrics(dm)
    }

    override fun getCount(): Int {
        return AlbumActivity.contentList.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolder {
        //
        var backImage: ImageView? = null
        // 封面
        var imageView: ImageView? = null
        var choose_back: ImageView? = null
        // 文件夹名称
        var folderName: TextView? = null
        // 文件夹里面的图片数量
        var fileNum: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.plugin_camera_select_folder, null)
            holder = ViewHolder()
            holder!!.backImage = convertView!!.findViewById<View>(R.id.file_back) as ImageView
            holder!!.imageView = convertView.findViewById<View>(R.id.file_image) as ImageView
            holder!!.choose_back = convertView.findViewById<View>(R.id.choose_back) as ImageView
            holder!!.folderName = convertView.findViewById<View>(R.id.name) as TextView
            holder!!.fileNum = convertView.findViewById<View>(R.id.filenum) as TextView
            holder!!.imageView!!.adjustViewBounds = true
            //			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,dipToPx(65));
            //			lp.setMargins(50, 0, 50,0);
            //			holder.imageView.setLayoutParams(lp);
            holder!!.imageView!!.scaleType = ImageView.ScaleType.FIT_XY
            convertView.tag = holder
        } else
            holder = convertView.tag as ViewHolder
        val path: String
        if (AlbumActivity.contentList[position].imageList != null) {
            //path = photoAbsolutePathList.get(position);
            //封面图片路径
            path = AlbumActivity.contentList[position].imageList!![0].imagePath
            // 给folderName设置值为文件夹名称
            //holder.folderName.setText(fileNameList.get(position));
            holder!!.folderName!!.text = AlbumActivity.contentList[position].bucketName

            // 给fileNum设置文件夹内图片数量
            //holder.fileNum.setText("" + fileNum.get(position));
            holder!!.fileNum!!.text = "" + AlbumActivity.contentList[position].count

        } else
            path = "android_hybrid_camera_default"

        if (path.contains("android_hybrid_camera_default"))
            holder!!.imageView!!.setImageResource(R.drawable.plugin_camera_no_pictures)
        else {
            //			holder.imageView.setImageBitmap( AlbumActivity.contentList.get(position).imageList.get(0).getBitmap());
            val item = AlbumActivity.contentList[position].imageList!![0]
            holder!!.imageView!!.tag = item.imagePath
            Logs.i("............." + item.imagePath + ",,,," + item.thumbnailPath)
            SUtils.setPicWithHolder(holder!!.imageView, item.imagePath, R.drawable.default_icon_triangle)
            //cache.displayBmp(holder.imageView, item.thumbnailPath, item.imagePath,callback);
        }
        // 为封面添加监听
        holder!!.imageView!!.setOnClickListener(ImageViewClickListener(position, mIntent!!, holder!!.choose_back!!))

        return convertView
    }

    // 为每一个文件夹构建的监听器
    private inner class ImageViewClickListener(private val position: Int, private val intent: Intent, private val choose_back: ImageView) : OnClickListener {

        override fun onClick(v: View) {
            ShowAllPhotoActivity.dataList = (AlbumActivity.contentList[position].imageList as ArrayList<ImageItem>?)!!
            val intent = Intent()
            val folderName = AlbumActivity.contentList[position].bucketName
            intent.putExtra("folderName", folderName)
            intent.putExtra(JumpTo.TYPE_OBJECT, tempSelectBitmap)
            intent.setClass(mContext!!, ShowAllPhotoActivity::class.java)
            (mContext as Activity).startActivityForResult(intent, 5)
            //			choose_back.setVisibility(v.VISIBLE);//注释掉是为了解决封面错乱

        }
    }

}

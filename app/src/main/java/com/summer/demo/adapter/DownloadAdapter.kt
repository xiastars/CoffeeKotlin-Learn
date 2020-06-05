package com.summer.demo.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView

import com.summer.demo.R
import com.summer.demo.bean.BookBean
import com.summer.demo.utils.AppBeansHelper
import com.summer.helper.db.CommonService
import com.summer.helper.downloader.DownloadManager
import com.summer.helper.utils.SUtils
import com.summer.helper.utils.TipDialog
import com.summer.helper.view.LoadingDialog

class DownloadAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mItems: List<BookBean>? = null
    private val mService: CommonService
    internal var mLoading: LoadingDialog? = null
    internal var downloadManager: DownloadManager

    init {
        this.mService = CommonService(context)
        //	mLoading = new LoadingDialog(context);
        downloadManager = DownloadManager.getInstance(context)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHodler = holder as MyViewHolder
        val book = mItems!![position]
        SUtils.setPic(viewHodler.ivNav, book.icon, 143, 202, R.drawable.trans, true)
        SUtils.setNotEmptText(viewHodler.tvName, book.name)
        viewHodler.helper.setEntity(book)
        viewHodler.tvGrade.text = "开发商:" + book.developer
        viewHodler.tvSize.text = "大小:" + book.size
        viewHodler.tvTime.text = "更新时间:" + book.published_at
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var ivNav: ImageView
        internal var tvName: TextView
        internal var tvGrade: TextView
        internal var tvSize: TextView
        internal var tvTime: TextView
        internal var tvLoad: TextView
        internal var pbLoad: ProgressBar
        internal var rlDownload: RelativeLayout
        internal lateinit var helper: AppBeansHelper

        init {
            ivNav = view.findViewById<View>(R.id.iv_book_icon) as ImageView
            tvName = view.findViewById<View>(R.id.tv_book_name) as TextView
            tvGrade = view.findViewById<View>(R.id.tv_grade) as TextView
            tvSize = view.findViewById<View>(R.id.tv_size) as TextView
            tvTime = view.findViewById<View>(R.id.tv_time) as TextView
            tvLoad = view.findViewById<View>(R.id.load_text) as TextView
            pbLoad = view.findViewById<View>(R.id.load_pb) as ProgressBar
            rlDownload = view.findViewById<View>(R.id.rl_download) as RelativeLayout
            SUtils.clickTransColor(rlDownload)

            var listener = object : AppBeansHelper.BookDownloadedListener {
                override fun onCallback(bean: BookBean) {
                    helper.setEntity(bean)
                }
            }
            helper = AppBeansHelper(context, pbLoad, tvLoad, listener)
            helper.setCommonService(mService)
            helper.setDownloadManager(downloadManager)
            rlDownload.setOnClickListener {
                val state = SUtils.getNetWorkType(context)
                if (state == SUtils.NetState.WIFI) {
                    helper.startDownload()
                } else if (state == SUtils.NetState.MOBILE) {
                    TipDialog(context, "当前处于数据网络情况下,继续下载吗?", object : TipDialog.DialogAfterClickListener {
                        override fun onSure() {
                            helper.startDownload()
                        }

                        override fun onCancel() {

                        }
                    }).show()
                } else {
                    SUtils.makeToast(context, R.string.network_error)
                }
            }
        }
    }

    interface SourceSelectedListener {
        fun afterClick(position: BookBean)
    }

    fun notifyDataChanged(arrsList: List<*>) {
        this.mItems = arrsList as List<BookBean>
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (mItems != null) mItems!!.size else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, arg1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_download, parent,
                false))
    }

}

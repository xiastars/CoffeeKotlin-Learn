package com.summer.demo.module.album

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import com.summer.demo.R
import com.summer.demo.constant.BroadConst
import com.summer.demo.module.album.util.ImageItem
import com.summer.demo.module.base.BaseActivity
import com.summer.demo.view.SupportScrollEventWebView
import com.summer.helper.dialog.BottomListDialog
import com.summer.helper.downloader.DownloadStatus
import com.summer.helper.downloader.DownloadTask
import com.summer.helper.downloader.DownloadTaskListener
import com.summer.helper.listener.OnSimpleClickListener
import com.summer.helper.server.EasyHttp
import com.summer.helper.server.PostData
import com.summer.helper.utils.JumpTo
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SFileUtils
import com.summer.helper.utils.SUtils
import java.io.Serializable
import java.util.*

/**
 * 帖子里的照片打开后的横向浏览页面
 *
 * @编者 夏起亮
 */
class ViewBigPhotoActivity : BaseActivity(), OnClickListener {
    @BindView(R.id.count)
    var tvItemCount: TextView? = null
    protected var albumItem: MutableList<ImageItem>? = null
    private var pager: ViewPager? = null
    private var imageView: SupportScrollEventWebView? = null
    private val views = SparseArray<View>()
    private var photo: ImageItem? = null

    internal var isMine: Boolean = false//是不是我的

    /**
     * 获取当前对象
     * @return
     */
    protected val curItem: ImageItem?
        get() = if (albumItem == null) {
            null
        } else albumItem!![pager!!.currentItem]

    override fun dealDatas(requestCode: Int, obj: Any) {
        if (requestCode == 0) {
            SUtils.makeToast(context, "删除成功")
            val intent = Intent(BroadConst.NOTIFY_ALBUM_DELETE)
            intent.putExtra(JumpTo.TYPE_OBJECT, albumItem as Serializable?)
            context!!.sendBroadcast(intent)
            if (albumItem == null || albumItem!!.size == 0) {
                finish()
            }
        }
    }

    override fun setTitleId(): Int {
        return 0
    }

    override fun setContentView(): Int {
        return R.layout.activity_view_photos
    }

    override fun initData() {
        isMine = JumpTo.getBoolean(this)
        removeViewTitle()
        initView()
        context = this@ViewBigPhotoActivity
    }

    protected fun removeViewTitle() {
        changeHeaderStyleTrans(context!!.resources.getColor(R.color.half_grey))
        setLayoutFullscreen(true)
        removeTitle()
    }

    private fun initView() {
        albumItem = JumpTo.getObject(this) as MutableList<ImageItem>
        if (albumItem == null) {
            val path = JumpTo.getString(this)
            if (!TextUtils.isEmpty(path)) {
                albumItem = ArrayList()
                val item = ImageItem()
                item.imagePath = path
                albumItem!!.add(item)
            }
        }

        pager = findViewById(R.id.pager) as ViewPager
        pager!!.offscreenPageLimit = 3
        Logs.i("xx:" + JumpTo.getInteger(this))
        pager!!.adapter = ImagePagerAdapter()
        pager!!.currentItem = JumpTo.getInteger(this)
    }

    @SuppressLint("MissingSuperCall")
    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_POSITION, pager!!.currentItem)
    }

    private inner class ImagePagerAdapter : PagerAdapter() {

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container
                    .removeView(views.get(pager!!.currentItem))
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun notifyDataSetChanged() {
            super.notifyDataSetChanged()

        }

        override fun getCount(): Int {
            return if (albumItem != null) albumItem!!.size else 0
        }

        override fun instantiateItem(view: ViewGroup, position: Int): Any {
            val imageLayout = layoutInflater.inflate(
                    R.layout.big_photo_item_page_image, view, false)
            imageView = imageLayout
                    .findViewById<View>(R.id.btn_drag_view) as SupportScrollEventWebView
            val loading = imageLayout
                    .findViewById<View>(R.id.loading) as RelativeLayout
            imageView!!.setBackgroundColor(0) // 设置背景色
            imageView!!.background.alpha = 0 // 设置填充透明度 范围：0-255
            setWebViewSetings(loading)
            bindCloseClickListener(imageView)
            photo = albumItem!![position]
            var path: String? = photo!!.imagePath
            if (path != null && path.startsWith("//")) {
                path = PostData.OOSHEAD + ":" + path
            }
            val data: String
            if (path!!.startsWith("http")) {
                data = "<table width='100%' height='100%'><tr><td  style='vertical-align:middle'><img src='" + path + "'" +
                        " width='100%'/></td></tr></table>"
            } else {
                data = "<table width='100%' height='100%'><tr><td  style='vertical-align:middle'><img src='" + "file://" + path + "'" +
                        " width='100%'/></td></tr></table>"
            }
            Logs.i("path:::$data")
            imageView!!.loadData(data, "text/html", "utf-8")

            val finalPath = path
            imageView!!.setOnLongClickListener {
                val selectTypeDialog = BottomListDialog(context!!)
                val titles = arrayOf("保存", "取消")
                selectTypeDialog.setDatas(titles)
                selectTypeDialog.setStringType()
                selectTypeDialog.showTopContent(View.GONE)
                selectTypeDialog.showBottomContent(View.GONE)
                selectTypeDialog.show()
                selectTypeDialog.setListener { position ->
                    if (position == 0) {
                        downloadImg(finalPath)
                    }
                    selectTypeDialog.cancelDialog()
                }

                true
            }

            /*      Logs.i("path:"+path);
            String data = "<table width='100%' height='100%'><tr><td  style='vertical-align:middle'><img src='" + path+ "'" +
                    " width='100%'/></td></tr></table>";
            if(path.endsWith("mp4")){
                data = "<table width='100%' height='100%'><tr><td  style='vertical-align:middle'><video src='" + path+ "'" +
                        " width='100%' autoplay='autoplay'></video>" + "</td></tr></table>";
            }
            imageView.requestRankData(data, "text/html", "utf-8");*/
            view.addView(imageLayout, 0)
            views.append(position, imageView)
            //bindWebViewOnTouchListener(imageView);
            return imageLayout
        }

        private fun downloadImg(path: String) {
            val fileName = System.currentTimeMillis().toString() + "_hxq.png"
            EasyHttp.download(context, path, SFileUtils.getImageViewDirectory(), fileName, object : DownloadTaskListener {
                override fun onDownloading(downloadTask: DownloadTask) {
                    Logs.i(downloadTask.percent.toString() + ",,")
                    if (downloadTask.downloadStatus == DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                        this@ViewBigPhotoActivity.runOnUiThread { SUtils.makeToast(context, "保存成功，位置：" + SFileUtils.getImageViewDirectory() + fileName) }

                    }
                }

                override fun onPause(downloadTask: DownloadTask) {

                }

                override fun onError(downloadTask: DownloadTask, errorCode: Int) {

                }
            })
        }

        private fun bindWebViewOnTouchListener(webView: WebView) {
            webView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    finish()
                }
                false
            }
        }

        private fun setWebViewSetings(loading: RelativeLayout) {
            val webSettings = imageView!!.settings
            webSettings.javaScriptEnabled = true
            webSettings.useWideViewPort = true
            webSettings.loadWithOverviewMode = true
            webSettings.builtInZoomControls = true
            webSettings.setSupportZoom(true)
            imageView!!.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                    view.loadUrl(url)
                    return true
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    loading.visibility = View.GONE
                }
            }
        }

        private fun bindCloseClickListener(view: SupportScrollEventWebView?) {
            if (view == null)
                return
            view.setOnSingleTabListener(OnSimpleClickListener { finish() })
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

        override fun finishUpdate(container: View) {
            super.finishUpdate(container)
            // 显示页码
            if (albumItem != null && albumItem!!.size > 0) {
                tvItemCount!!.text = ((pager!!.currentItem + 1).toString() + "/"
                        + albumItem!!.size)
            }
        }

        override fun saveState(): Parcelable? {
            return null
        }

    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_cancel -> finish()
        }

    }

    fun deleteItem(item: ImageItem) {
        if (albumItem == null) {
            return
        }
        val index = pager!!.currentItem
        albumItem!!.remove(item)
        pager!!.adapter = ImagePagerAdapter()
        pager!!.currentItem = if (index > 0) index - 1 else 0
        //do delete
    }

    companion object {
        private val STATE_POSITION = "STATE_POSITION"

        /**
         * 跳转
         * @param items
         * @param curPos 显示的位置
         */
        fun show(context: Context, items: MutableList<ImageItem>, curPos: Int) {
            val intent = Intent(context, ViewBigPhotoActivity::class.java)
            intent.putExtra(JumpTo.TYPE_INT, curPos)
            intent.putExtra(JumpTo.TYPE_OBJECT, items as Serializable)
            context.startActivity(intent)
        }
    }

}
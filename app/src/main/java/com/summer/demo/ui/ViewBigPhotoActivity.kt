package com.summer.demo.ui

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import android.widget.TextView
import com.summer.demo.R
import com.summer.demo.constant.BroadConst
import com.summer.demo.module.base.BaseActivity
import com.summer.demo.view.SupportScrollEventWebView
import com.summer.helper.listener.OnSimpleClickListener
import com.summer.helper.utils.DownloadPhotoHelper
import com.summer.helper.utils.JumpTo
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SUtils
import java.io.Serializable
import java.util.*

/**
 * 帖子里的照片打开后的横向浏览页面
 *
 * @编者 xiastars
 */
class ViewBigPhotoActivity : BaseActivity(), View.OnClickListener {
    val tvItemCount: TextView by Bind(R.id.count)
    protected var albumItem: MutableList<String>? = null
    private var pager: ViewPager? = null
    private var imageView: SupportScrollEventWebView? = null
    private val views = SparseArray<View>()
    private var photo: String? = null

    internal var isMine: Boolean = false//是不是我的

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

    override fun initPresenter() {

    }

    override fun initData() {
        isMine = JumpTo.getBoolean(this)
        removeViewTitle()
        initView()
        context = this@ViewBigPhotoActivity
    }

    protected fun removeViewTitle() {
        removeTitle()
        changeHeaderStyleTrans(context!!.resources.getColor(R.color.half_grey))
        setLayoutFullscreen()
    }

    private fun initView() {
        albumItem = JumpTo.getObject(this)  as? MutableList<String>
        if(albumItem == null){
            val path = JumpTo.getString(this)
            if (!TextUtils.isEmpty(path)) {
                albumItem = ArrayList()
                albumItem!!.add(path)
            }
        }

        pager = findViewById(R.id.pager) as ViewPager
        pager!!.offscreenPageLimit = 3
        Logs.i("xx:" + JumpTo.getString(this))
        pager!!.adapter = ImagePagerAdapter()
        pager!!.currentItem = JumpTo.getInteger(this)

    }

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
            val path = photo
            val data: String
            if (path != null && path.startsWith("http")) {
                data = "<table width='100%' height='100%'><tr><td  style='vertical-align:middle'><img src='" + path + "'" +
                        " width='100%'/></td></tr></table>"
            } else {
                data = "<table width='100%' height='100%'><tr><td  style='vertical-align:middle'><img src='" + "file://" + path + "'" +
                        " width='100%'/></td></tr></table>"
            }
            Logs.i("path:::$data")
            imageView!!.loadData(data, "text/html", "utf-8")

            imageView!!.setOnLongClickListener {
                SUtils.makeToast(context, "正在保存中")
                DownloadPhotoHelper.getInstance().download(this@ViewBigPhotoActivity, path)

                true
            }
            view.addView(imageLayout, 0)
            views.append(position, imageView)
            //bindWebViewOnTouchListener(imageView);
            return imageLayout
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
            var listener = object : OnSimpleClickListener {
                override fun onClick(position: Int) {
                    finish()
                }

            }
            view.setOnSingleTabListener(listener)
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

    companion object {
        private val STATE_POSITION = "STATE_POSITION"
    }


}
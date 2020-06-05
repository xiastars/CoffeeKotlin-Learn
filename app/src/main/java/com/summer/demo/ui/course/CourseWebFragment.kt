package com.summer.demo.ui.course

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import com.summer.demo.R
import com.summer.demo.module.base.BaseFragment
import com.summer.helper.web.CustomWebView

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/12 14:26
 */
class CourseWebFragment : BaseFragment() {

    internal val webviewContainer: CustomWebView by Bind(R.id.webview_container)
    private var loadPageUrl: String? = null


    private val lastTime = System.currentTimeMillis()


    override fun initView(view: View) {
        initializeCurrentWebView()
        setData()
    }

    /**
     * 其它应用跳转监听
     */
    private fun setData() {
        loadPageUrl = arguments!!.getString("url")
        navigateToUrl(loadPageUrl)

    }

    /**
     * 设置跳转Url显示路径
     */
    fun navigateToUrl(url: String?) {
        if (url != null && url.length > 0) {
            webviewContainer!!.loadUrl(url)

        }
    }

    /**
     * 初始化当前WebView
     */
    fun initializeCurrentWebView() {
        webviewContainer!!.webViewClient = WebViewClient()
        webviewContainer!!.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength -> }
    }


    public override fun loadData() {

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.activity_webview
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        fun show(url: String): CourseWebFragment {
            val markDownFragment = CourseWebFragment()
            val bundle = Bundle()
            bundle.putString("url", url)
            markDownFragment.arguments = bundle
            return markDownFragment
        }
    }
}

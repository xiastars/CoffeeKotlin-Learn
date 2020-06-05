package com.summer.demo.ui.module.fragment

import android.view.View
import android.webkit.WebView
import android.widget.TextView

import com.summer.demo.R
import com.summer.demo.module.album.MediaHandleJavascriptInterface
import com.summer.demo.module.base.BaseFragment
import com.summer.helper.utils.JumpTo
import com.summer.helper.web.CustomWebView
import com.summer.helper.web.SWebviewClient
import com.summer.helper.web.WebContainerActivity

/**
 * @Description: Webview访问测试
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/10 11:29
 */
class WebLeanFragment : BaseFragment(), View.OnClickListener {
    private val customWebView: CustomWebView by Bind(R.id.webview)
    private val tvLocal: TextView by Bind(R.id.tv_local,true)
    private val tvUrl: TextView by Bind(R.id.tv_url,true)

    override fun initView(view: View) {
        val data = "<p>31231111111</p>\\n<table style=\\\"border-collapse: collapse; width: 100%;\\\" border=\\\"1\\\">\\n<tbody>\\n<tr>\\n<td style=\\\"width: 14.2857%;\\\">" +
                "&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"" +
                "width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\"" +
                ">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n</tr>\\n<tr>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>" +
                "\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n</tr>\\n<tr>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n</tr>\\n<tr>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n</tr>\\n<tr>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">" +
                "<img src='static/js/plugins/tinymce4.7.5/plugins/emoticons/img/smiley-innocent.gif' alt=\\\"innocent\\\" /></td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n<td style=\\\"width: 14.2857%;\\\">&nbsp;</td>\\n</tr>\\n</tbody>\\n</table>"
        setWebViewContent(customWebView, data)
    }

    /**
     * 设置统一富文本显示
     *
     * @param wbContainer
     * @param content
     */
    fun setWebViewContent(wbContainer: WebView?, content: String) {
        wbContainer!!.addJavascriptInterface(MediaHandleJavascriptInterface(context!!), "imagelistner")
        wbContainer.webViewClient = SWebviewClient(wbContainer, true, null)
        val finalContent = "<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" +
                "<meta name=\"viewport\" content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\">" +
                "<style>img{max-width:100%;height:auto;};video{width:100%;};</style> " + content.replace("(\r\n|\r|\n|\n\r)".toRegex(), "<br>") + "</html>"
        wbContainer.loadDataWithBaseURL("http://120.79.56.152:8010/", finalContent, "text/html", "utf-8", "")
    }

    public override fun loadData() {

    }


    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.fragment_learn_web
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_url -> {
                val testUrl = "http://www.polayoutu.com/collections"
                JumpTo.getInstance().commonJump(context, WebContainerActivity::class.java, testUrl)
            }
            R.id.tv_local -> {

                val url = "file:///android_asset/home.html"
                JumpTo.getInstance().commonJump(context, WebContainerActivity::class.java, url)
            }
        }
    }
}

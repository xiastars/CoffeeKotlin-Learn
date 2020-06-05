package com.summer.helper.web;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.summer.helper.listener.OnPageLoadedListener;

/**
 * Created by xiastars on 2017/8/26.
 */

public class SWebviewClient extends WebViewClient {
    OnPageLoadedListener listener;
    boolean handleVideo;
    WebView webView;

    public SWebviewClient(WebView webView, boolean handleVideo, OnPageLoadedListener listener) {
        this.listener = listener;
        this.handleVideo = handleVideo;
        this.webView = webView;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        view.getSettings().setJavaScriptEnabled(true);
        super.onPageFinished(view, url);
        if(listener != null){
            listener.loaded();
        }
        // html加载完成之后，添加监听图片的点击js函数
        if (handleVideo) {
            addImageClickListner();
            addVideoClickListner();
            addUrlClickListner();
        }
    }

    private void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，
        // 函数的功能是在图片点击的时候调用本地java接口并传递url过去
        webView.loadUrl("javascript:(function(){"
                + "var objs = document.getElementsByTagName(\"img\"); "
                + "for(var i=0;i<objs.length;i++)  "
                + "{"
                + "    objs[i].onclick=function()  "
                + "    {  "
                + "        window.imagelistner.openImage(this.src);  "
                + "    }  "
                + "}"
                + "})()");
    }

    private void addUrlClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，
        // 函数的功能是在图片点击的时候调用本地java接口并传递url过去
        webView.loadUrl("javascript:(function(){"
                + "var objs = document.getElementsByTagName(\"a\"); "
                + "for(var i=0;i<objs.length;i++)  "
                + "{"
                + "    objs[i].onclick=function(event)  "
                + "    {  "
                + "        var url = this.getAttribute(\"href\"); "
                + "        window.imagelistner.openUrl(url);  "
                + "        event.preventDefault(); "
                + "    }  "
                + "}"
                + "})()");
    }

    private void addVideoClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，
        // 函数的功能是在图片点击的时候调用本地java接口并传递url过去
        webView.loadUrl("javascript:(function(){"
                + "var objs = document.getElementsByTagName(\"video\"); "
                + "for(var i=0;i<objs.length;i++)  "
                + "{"
                + "    objs[i].onclick=function(event)  "
                + "    {  "
                + "        event.preventDefault();  "
                + "        window.imagelistner.openVideo(this.src);  "
                + "    }  "
                + "}"
                + "})()");
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        view.getSettings().setJavaScriptEnabled(true);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }
}

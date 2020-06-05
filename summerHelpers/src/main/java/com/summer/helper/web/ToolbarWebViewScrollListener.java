package com.summer.helper.web;

import android.view.View;

/*
* This class is a ScrollListener for ToolbarWebView that allows to show/hide
* views when list is scrolled. It assumes that you have added a header
* to your webview. @see com.janrone.lib.ToolbarWebView.ScrollInterface
* */
public abstract class ToolbarWebViewScrollListener implements CustomWebView.ScrollInterface {

    private static final int HIDE_THRESHOLD = 200;

    private int mScrolledDistance = 0;
    private boolean mControlsVisible = true;

    @Override
    public void onSChanged(CustomWebView mWebView, View toolBarView,int l, int t, int oldl, int oldt){
    	     //WebView的总高度
		    float webViewContentHeight = mWebView.getContentHeight() * mWebView.getScale();
		    //WebView的现高度
		    float webViewCurrentHeight = (mWebView.getHeight() + mWebView.getScrollY());
//		    Logs.i("=================webViewContentHeight>", webViewContentHeight + "---------" + webViewCurrentHeight
//		    		+"----" + t);
            if (t == 0) {
                if(!mControlsVisible) {
                    onShow();
                    mControlsVisible = true;
                }
            } else {
                if (mScrolledDistance > HIDE_THRESHOLD && mControlsVisible && (webViewContentHeight-webViewCurrentHeight) > 1000) {
                    onHide();
//                    Logs.i("=================>", "onHide==>" + mScrolledDistance);
                    mControlsVisible = false;
                    mScrolledDistance = 0;
                } else if (mScrolledDistance < -HIDE_THRESHOLD && !mControlsVisible && (webViewContentHeight-webViewCurrentHeight) > 1000) {
                    onShow();
//                    Logs.i("=================mScrolledDistance>", "onShow==>"+ mScrolledDistance);
                    mControlsVisible = true;
                    mScrolledDistance = 0;
                }
            }

            if((mControlsVisible && t-oldt>0) || (!mControlsVisible && t-oldt<0)) {
                mScrolledDistance += (t-oldt);
            }
    }

    public abstract void onHide();
    public abstract void onShow();
}

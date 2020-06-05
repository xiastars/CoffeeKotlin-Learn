/*
 * Zirco Browser for Android
 *
 * Copyright (C) 2010 J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.summer.helper.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.summer.helper.utils.Logs;

import java.util.HashMap;
import java.util.Map;

/**
 * A convenient extension of WebView.
 */
public class CustomWebView extends WebView {

    private static final String TAG = "CustomWebView";

    private Context mContext;

    private int mProgress = 100;

    private boolean mIsLoading = false;

    private String mLoadedUrl;

    private String gameIcon;

    private String originalTitle;

    private CookieManager cookieManager = CookieManager.getInstance();

    public ScrollInterface mScrollInterface;
    private View toolBarView;

    /**
     * Constructor.
     *
     * @param context The current context.
     */
    public CustomWebView(Context context) {
        super(context);

        mContext = context;

        initializeOptions();

    }

    /**
     * Constructor.
     *
     * @param context The current context.
     * @param attrs   The attribute set.
     */
    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        initializeOptions();
    }

    /**
     * Initialize the WebView with the options set by the user through
     * preferences.
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void initializeOptions() {
        WebSettings settings = getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setSaveFormData(true);
        CookieManager.getInstance().setAcceptCookie(true);
        settings.setSupportZoom(false);
        settings.setSupportMultipleWindows(false);
        settings.setBuiltInZoomControls(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setGeolocationEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//		if(SUtils.isNetworkAvailable(mContext)){
//			settings.setCacheMode(WebSettings.LOAD_DEFAULT);
//		}else{
//			settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//		}
//		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setAppCachePath(mContext.getDir("appcaches", 0).getPath());
        settings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        setLongClickable(true);
        setScrollbarFadingEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");//文本编码
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        setDrawingCacheEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
    }

    public void synCookies(Context context, String url, String cookies) {
        CookieSyncManager.createInstance(context).startSync();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, cookies);// 指定要修改的cookies
        CookieSyncManager.getInstance().sync();
    }

    public static void removeCookie() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
    }

    @Override
    public void loadUrl(String url) {
        Logs.i("xia", "loadURL:" + url);
        try {
            // 所有网页请求都加入请求头
            Map<String, String> additionalHttpHeaders = new HashMap<String, String>();
            additionalHttpHeaders.put("encoding", "utf-8");
            super.loadUrl(url, additionalHttpHeaders);
        } catch (NullPointerException e) {
        }
    }

    /**
     * Set the current loading progress of this view.
     *
     * @param progress The current loading progress.
     */
    public void setProgress(int progress) {
        mProgress = progress;
    }

    /**
     * Get the current loading progress of the view.
     *
     * @return The current loading progress of the view.
     */
    public int getProgress() {
        return mProgress;
    }

    /**
     * Triggered when a new page loading is requested.
     */
    public void notifyPageStarted() {
        mIsLoading = true;
    }

    /**
     * Triggered when the page has finished loading.
     */
    public void notifyPageFinished() {
        mProgress = 100;
        mIsLoading = false;
    }

    /**
     * Check if the view is currently loading.
     *
     * @return True if the view is currently loading.
     */
    public boolean isLoading() {
        return mIsLoading;
    }

    /**
     * Get the loaded url, e.g. the one asked by the user, without redirections.
     *
     * @return The loaded url.
     */
    public String getLoadedUrl() {
        return mLoadedUrl;
    }

    public void setLoadedUrl(String url) {
        this.mLoadedUrl = url;
    }

    /**
     * Reset the loaded url.
     */
    public void resetLoadedUrl() {
        mLoadedUrl = null;
    }

    public boolean isSameUrl(String url) {
        if (url != null) {
            return url.equalsIgnoreCase(this.getUrl());
        }

        return false;
    }

    public void destoryWebView() {
        onPause();
        setVisibility(View.GONE);
    }

    public void destoryCurrentWebView() {
        onPause();
        destroy();
        removeAllViews();
    }

    public String getGameIcon() {
        return gameIcon;
    }

    public void setGameIcon(String gameIcon) {
        this.gameIcon = gameIcon;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (toolBarView != null && mScrollInterface != null)
            mScrollInterface.onSChanged(this, toolBarView, l, t, oldl, oldt);
    }

    public void setOnCustomScroolChangeListener(View toolBarView, ScrollInterface scrollInterface) {
        this.mScrollInterface = scrollInterface;
        this.toolBarView = toolBarView;
    }

    public interface ScrollInterface {
        public void onSChanged(CustomWebView mWebView, View toolBarView, int l, int t,
                               int oldl, int oldt);
    }

}

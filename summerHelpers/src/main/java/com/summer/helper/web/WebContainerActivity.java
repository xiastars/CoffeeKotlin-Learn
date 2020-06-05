package com.summer.helper.web;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.malata.summer.helper.R;
import com.summer.helper.utils.JumpTo;
import com.summer.helper.utils.Logs;

public class WebContainerActivity extends Activity {
	
	private WebContainerActivity INSTANCE;
	FrameLayout llContainerLayout;
	
	private String loadPageUrl;
	public String title;
	public int isFromMain = 0;
	private boolean isHomeKey = false;
	public WebTopBarManager webTopBarManager;
	ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview_container);
		INSTANCE = WebContainerActivity.this;
		WebContainerJumpView.initLocalActivityManager(this, savedInstanceState);
		initView();
		webTopBarManager = new WebTopBarManager(this);
		initData();
	}
	
	private void initView() {
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
		llContainerLayout = (FrameLayout) findViewById(R.id.container_layout);
	}

	public ProgressBar getProgressBar(){
		return mProgressBar;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Logs.i("xia","ONRESUME");
		initData();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		if(isHomeKey && isFromMain == 1){
			isHomeKey = false;
			finish();
		}
	}
	
	private void initData(){
		loadPageUrl = JumpTo.getString(this);
		WebContainerJumpView.jumpToView(llContainerLayout, this,loadPageUrl);
	}

	class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {

		static final String SYSTEM_REASON = "reason";
		static final String SYSTEM_HOME_KEY = "homekey";// home key
		static final String SYSTEM_RECENT_APPS = "recentapps";// long home key

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (reason != null) {
					//Home键清除webView
					if (reason.equals(SYSTEM_HOME_KEY)) {
						isHomeKey = true;
					} else if (reason.equals(SYSTEM_RECENT_APPS)) {
						// long home key处理点
					}
				}
			}
		}
	}
	
//	
	private void navigatePrevious() {
		if (webTopBarManager.mCurrentWebView.canGoBack()) {
			webTopBarManager.mCurrentWebView.goBack();
		} else {
			ActivitysManager.finish("WebContainerActivity");
			finish();
			isFromMain = 0;
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getRepeatCount() == 0) {
			navigatePrevious();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
}

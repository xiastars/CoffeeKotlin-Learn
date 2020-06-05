package com.summer.helper.web;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.malata.summer.helper.R;
import com.summer.helper.utils.JumpTo;
import com.summer.helper.utils.JumpTo.ShortcutJump;
import com.summer.helper.utils.Logs;
import com.summer.helper.view.RoundAngleImageView;

import java.io.IOException;
import java.io.InputStream;

public class WebViewActivity extends Activity {
	
	private WebViewActivity INSTANCE;
	private static final int OPEN_FILE_CHOOSER_ACTIVITY = 0;
	
	protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT);
	
	RoundAngleImageView ivLoadingIcon;
	CustomWebView mCurrentWebView;
	LinearLayout loadingContainer;
	
	private FullscreenHolder mFullscreenContainer;
	private View mVideoProgressView = null;
	private Bitmap mDefaultVideoPoster = null;
	protected LayoutInflater mInflater = null;
	private CustomViewCallback mCustomViewCallback;
	private View mCustomView; 
	public WebContainerActivity activity;
	
	boolean isPreviousEnable = false;
	private String loadPageUrl;
	private String navUrl;
	private String iconUrl;
	private String title;
	private Dialog mDialog;
	private boolean isLoadingEnd = false;
	private int urlType;
	private int commentCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		INSTANCE = WebViewActivity.this;
		activity = (WebContainerActivity)getParent();
		initView();
		initializeCurrentWebView();
		setData();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void initView() {
		loadingContainer = (LinearLayout) findViewById(R.id.loading_container);
		mCurrentWebView = (CustomWebView) findViewById(R.id.webview_container);
		ivLoadingIcon = (RoundAngleImageView) findViewById(R.id.iv_loading_icon);
		activity.webTopBarManager.setCurrentWebView(mCurrentWebView);
	}

	/**
	 * 其它应用跳转监听
	 * 
	 */
	private void setData() {
		Intent i = getIntent();
		title = i.getStringExtra(ShortcutJump.TYPE_NAME);
		title = getIntent().getStringExtra("key_title");
		int tag = getIntent().getIntExtra(JumpTo.TYPE_INT, 0);
		loadPageUrl = JumpTo.getString(INSTANCE);
		navigateToUrl(loadPageUrl);
		if(!TextUtils.isEmpty(loadPageUrl)){
			getCurrentWebView().setLoadedUrl(loadPageUrl);
		}
		ActivitysManager.Add("WebViewActivity_"+loadPageUrl, WebViewActivity.this);
		if(!TextUtils.isEmpty(title)){
			this.getCurrentWebView().setOriginalTitle(title);
		}
		if(!TextUtils.isEmpty(iconUrl)){
			this.getCurrentWebView().setGameIcon(iconUrl);
		}

	}

	/**
	 * 设置跳转Url显示路径
	 * 
	 */
	public void navigateToUrl(String url) {
		if ((url != null) && (url.length() > 0)) {
//			if (UrlUtils.isUrl(url)) {
//				url = UrlUtils.checkUrl(url);
//			} else {
//				url = UrlUtils.getSearchUrl(this, url);
//			}
			try {
				InputStream inputStream = this.getAssets().open("home.html");
				//inputStream.
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(url.startsWith("http")){
				mCurrentWebView.loadUrl(url);
			}else{
				//mCurrentWebView.loadDataWithBaseURL(null,url, "text/html", "utf-8",null);
			}
			mCurrentWebView.loadUrl(url);

		}
	}
	
	public void onPageStarted(String url) {
		this.loadPageUrl = url;
		activity.getProgressBar().setVisibility(View.VISIBLE);
	}
	
	public void onPageFinished(String url) {
//		mSwipeLayout.setRefreshing(false);
		this.loadPageUrl = url;
		mHandler.removeMessages(0);
		mHandler.sendMessageDelayed(mHandler.obtainMessage(0), 700);
		activity.getProgressBar().setVisibility(View.GONE);
		setLoadingGone();
	}
	
	private void setLoadingVisible() {
		if(isLoadingEnd)return;
		loadingContainer.setVisibility(View.VISIBLE);
		ivLoadingIcon.clearAnimation();
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			requestUrlComment(loadPageUrl);
		}
	};
	
//	class LoadTarget implements Target{
//
//		@Override
//		public void onBitmapFailed(Drawable arg0) {
//		}
//
//		@Override
//		public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
//			if(bitmap != null){
//				ivLoadingIcon.setImageBitmap(bitmap);
//				int color = bitmap.getPixel(20,20);
//				loadingContainer.setBackgroundColor(Color.argb(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color)));
//			}
//		}
//
//		@Override
//		public void onPrepareLoad(Drawable arg0) {
//			// TODO Auto-generated method stub
//		}
//	}
	
	public void setToolBarVisible(boolean isVisible){
		mCurrentWebView.setVisibility(View.VISIBLE);
	}

	
	@SuppressLint("NewApi")
	private void setLoadingGone() {
//		activity.webTopBarManager.getIconLayout().setVisibility(View.VISIBLE);
		ivLoadingIcon.clearAnimation();
//		SUtils.setPic(ivImage, mDefaultIcon, true, new SimpleTarget() {
//
//			@Override
//			public void onResourceReady(Object arg0, GlideAnimation arg1) {
//				if (arg0 != null && arg0 instanceof GlideBitmapDrawable) {
//					Bitmap bitmap = ((GlideBitmapDrawable) arg0).getBitmap();
//					if (!bitmap.isRecycled()) {
//						ivImage.setImageBitmap(bitmap);
//						invalidate();
//						requestLayout();
//					}
//				}
//			}
//		});
		ivLoadingIcon.animate().scaleX(2.0f).scaleY(2.0f).setListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}
			@Override
			public void onAnimationRepeat(Animator animation) {
			}
			@Override
			public void onAnimationEnd(Animator animation) {
				loadingContainer.setVisibility(View.GONE);
				ivLoadingIcon.clearAnimation();
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
			}
		}).start();
		isLoadingEnd = true;
	}
	
	/**
	 * 初始化当前WebView
	 */
	public void initializeCurrentWebView() {
		mCurrentWebView.setWebViewClient(new CustomWebViewClient(INSTANCE));
		activity.webTopBarManager.setCurrentWebView(mCurrentWebView);
		mCurrentWebView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(final String url,
					final String userAgent, final String contentDisposition,
					final String mimetype, final long contentLength) {
			
			}

		});

//		mCurrentWebView.setWebChromeClient(new WebChromeClient() {
//
//			@SuppressWarnings("unused")
//			// This is an undocumented method, it _is_ used, whatever Eclipse
//			// may think :)
//			// Used to show a file chooser dialog.
//			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
////				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
////				i.addCategory(Intent.CATEGORY_OPENABLE);
////				i.setType("*/*");
////				INSTANCE.startActivityForResult(Intent.createChooser(
////						i, INSTANCE
////								.getString(R.string.Main_FileChooserPrompt)),
////						OPEN_FILE_CHOOSER_ACTIVITY);
//			}
//
//			@Override
//			public Bitmap getDefaultVideoPoster() {
////				if (mDefaultVideoPoster == null) {
////					mDefaultVideoPoster = BitmapFactory.decodeResource(
////							INSTANCE.getResources(),
////							R.drawable.default_video_poster);
////				}
//
//				return mDefaultVideoPoster;
//			}
//
//			@Override
//			public View getVideoLoadingProgressView() {
////				if (mVideoProgressView == null) {
////					LayoutInflater inflater = LayoutInflater
////							.from(INSTANCE);
////					mVideoProgressView = inflater.inflate(
////							R.layout.video_loading_progress, null);
////				}
//
//				return mVideoProgressView;
//			}
//
//			public void onShowCustomView(View view, CustomViewCallback callback) {
//				showCustomView(view, callback);
//			}
//
//			@Override
//			public void onHideCustomView() {
//				hideCustomView();
//			}
//
//			@Override
//			public void onProgressChanged(WebView view, int newProgress) {
////				if (newProgress == 100) {
////					 activity.webTopBarManager.getProgressBar().setProgress(newProgress);
////					 activity.webTopBarManager.getProgressBar().setVisibility(View.GONE);
////					 setLoadingGone();
////		        } else {
////	                if (activity.webTopBarManager.getProgressBar().getVisibility() == View.GONE){
////	                	activity.webTopBarManager.getProgressBar().setVisibility(View.VISIBLE);
////	                	setLoadingVisible();
////	                }
////	                if(newProgress < 15){
////	                	activity.webTopBarManager.getProgressBar().setProgress(15);
////	                }else{
////	                	activity.webTopBarManager.getProgressBar().setProgress(newProgress);
////	                	if(newProgress > 60){
////	                		activity.webTopBarManager.getProgressBar().setVisibility(View.GONE);
////	                		setLoadingGone();
////	                	}
////	                }
////		         }
//				 super.onProgressChanged(view, newProgress);
//			}
//
//			@Override
//			public void onReceivedIcon(WebView view, Bitmap icon) {
////				new Thread(new FaviconUpdaterRunnable(INSTANCE, view
////						.getUrl(), view.getOriginalUrl(), icon)).start();
////				updateFavIcon();
//
//				super.onReceivedIcon(view, icon);
//			}
//
//			@Override
//			public void onReceivedTitle(WebView view, String title) {
////				setTitle(String.format(
////						getResources().getString(R.string.ApplicationNameUrl),
////						title));
//
//				super.onReceivedTitle(view, title);
//			}
//
//			@Override
//			public boolean onJsAlert(WebView view, String url, String message,
//					final JsResult result) {
////				new AlertDialog.Builder(activity)
////						.setTitle(R.string.Commons_JavaScriptDialog)
////						.setMessage(message)
////						.setPositiveButton(android.R.string.ok,
////								new AlertDialog.OnClickListener() {
////									public void onClick(DialogInterface dialog,
////											int which) {
////										result.confirm();
////									}
////								}).setCancelable(false).create().show();
//
//				return true;
//			}
//
//			@Override
//			public boolean onJsConfirm(WebView view, String url,
//					String message, final JsResult result) {
////				new AlertDialog.Builder(INSTANCE)
////						.setTitle(R.string.Commons_JavaScriptDialog)
////						.setMessage(message)
////						.setPositiveButton(android.R.string.ok,
////								new DialogInterface.OnClickListener() {
////									public void onClick(DialogInterface dialog,
////											int which) {
////										result.confirm();
////									}
////								})
////						.setNegativeButton(android.R.string.cancel,
////								new DialogInterface.OnClickListener() {
////									public void onClick(DialogInterface dialog,
////											int which) {
////										result.cancel();
////									}
////								}).create().show();
//
//				return true;
//			}
//			
//			@Override
//			public boolean onJsPrompt(WebView view, String url, String message,
//					String defaultValue, final JsPromptResult result) {
//
////				final LayoutInflater factory = LayoutInflater
////						.from(INSTANCE);
////				final View v = factory.inflate(
////						R.layout.javascript_prompt_dialog, null);
////				((TextView) v.findViewById(R.id.JavaScriptPromptMessage))
////						.setText(message);
////				((EditText) v.findViewById(R.id.JavaScriptPromptInput))
////						.setText(defaultValue);
////
////				new AlertDialog.Builder(INSTANCE)
////						.setTitle(R.string.Commons_JavaScriptDialog)
////						.setView(v)
////						.setPositiveButton(android.R.string.ok,
////								new DialogInterface.OnClickListener() {
////									public void onClick(DialogInterface dialog,
////											int whichButton) {
////										String value = ((EditText) v
////												.findViewById(R.id.JavaScriptPromptInput))
////												.getText().toString();
////										result.confirm(value);
////									}
////								})
////						.setNegativeButton(android.R.string.cancel,
////								new DialogInterface.OnClickListener() {
////									public void onClick(DialogInterface dialog,
////											int whichButton) {
////										result.cancel();
////									}
////								})
////						.setOnCancelListener(
////								new DialogInterface.OnCancelListener() {
////									public void onCancel(DialogInterface dialog) {
////										result.cancel();
////									}
////								}).show();
//
//				return true;
//
//			}
//		});
//		
		activity.webTopBarManager.setCurrentWebView(mCurrentWebView);
	}
	
	private void doNetworkDownload(final String url, final String userAgent,
			final String contentDisposition, final String mimetype, final long contentLength, final int networkType){
//		String info = String.format(getResources().getString(R.string.hint_unwifi), DownloadItem.getFileSize(contentLength));
//		DialogUtils.startConfirm(info, INSTANCE, new DialogClickListener() {
//			
//			@Override
//			public void doPositive() {
//				doDownloadStart(url, userAgent, contentDisposition,
//						mimetype, contentLength);
//			}
//			
//			@Override
//			public void doNegative() {
//			}
//		});
	}

	/**
	 * 初始化软件下载
	 * 
	 * @param url
	 * @param userAgent
	 * @param contentDisposition
	 * @param mimetype
	 * @param contentLength
	 */
	private void doDownloadStart(String url, String userAgent,
			String contentDisposition, String mimetype, long contentLength) {
//		if (ApplicationUtils.checkCardState(this, true)) {
////			DownloadItem item = new DownloadItem(this, url, contentLength);
////			Controller.getInstance().addToDownload(item);
////			item.startDownload();
////			Toasts.toastMessage(R.string.Main_DownloadStartedMsg, INSTANCE);
//			Intent intent = new Intent();
//			intent.setAction("android.intent.action.VIEW");
//			Uri content_url = Uri.parse(url);
//			intent.setData(content_url);
//			startActivity(intent);
//		}
	}

	private void showCustomView(View view, CustomViewCallback callback) {
		// if a view already exists then immediately terminate the new one
		if (mCustomView != null) {
			callback.onCustomViewHidden();
			return;
		}
		INSTANCE.getWindow().getDecorView();

		FrameLayout decor = (FrameLayout) getWindow().getDecorView();
		mFullscreenContainer = new FullscreenHolder(INSTANCE);
		mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
		decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
		mCustomView = view;
		// setStatusBarVisibility(false);
		mCustomViewCallback = callback;
	}

	private void hideCustomView() {
		if (mCustomView == null)
			return;

		FrameLayout decor = (FrameLayout) getWindow().getDecorView();
		decor.removeView(mFullscreenContainer);
		mFullscreenContainer = null;
		mCustomView = null;
		mCustomViewCallback.onCustomViewHidden();
	}
	
	class FullscreenHolder extends FrameLayout {

		public FullscreenHolder(Context ctx) {
			super(ctx);
			setBackgroundColor(ctx.getResources().getColor(
					android.R.color.black));
		}

		@Override
		public boolean onTouchEvent(MotionEvent evt) {
			return true;
		}

	}
	
	private void navigatePrevious() {
		activity.webTopBarManager.getProgressBar().setVisibility(View.GONE);
		if (mCurrentWebView.canGoBack()) {
			mCurrentWebView.goBack();
		}
	}

//	@Override
//	public void onDownloadEvent(String event, Object data) {
//		if (event.equals(EventConstants.EVT_DOWNLOAD_ON_FINISHED)) {
//			DownloadItem item = (DownloadItem) data;
//			if (item.getErrorMessage() == null) {
//				Toasts.toastMessage(getString(R.string.Main_DownloadFinishedMsg), INSTANCE);
//			} else {
//				Toasts.toastMessage(
//						getString(R.string.Main_DownloadErrorMsg,
//								item.getErrorMessage()), INSTANCE);
//			}
//		}
//	}
	
//	@Override
//	public void hideToolbars() {
//		activity.webTopBarManager.setToolbarsVisibility(false);
//	}

	public CustomWebView getCurrentWebView() {
		return mCurrentWebView;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		destroyWebView();
//		setVoiceFloatGone();
	}
	
//	/**
//	 * Gesture listener implementation.
//	 */
//	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
//
//		@Override
//		public boolean onDoubleTap(MotionEvent e) {
//			mCurrentWebView.zoomIn();
//			return super.onDoubleTap(e);
//		}
//
//		@Override
//		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//				float velocityY) {
//			// if (isSwitchTabsByFlingEnabled()) {
//			if (e2.getEventTime() - e1.getEventTime() <= FLIP_TIME_THRESHOLD) {
//				if (e2.getY() > (e1.getY() + FLIP_PIXEL_THRESHOLD)) {
//					if (activity.webTopBarManager.getTopBar().getVisibility() == View.GONE)
//						activity.webTopBarManager.setToolbarsVisibility(true);
//					return false;
//				}
//
//				// going forwards: pushing stuff to the left
//				if (e2.getY() < (e1.getY() - FLIP_PIXEL_THRESHOLD)) {
//					if (activity.webTopBarManager.getTopBar().getVisibility() == View.VISIBLE)hideToolbars();
//					return false;
//				}
//			}
//
//			return super.onFling(e1, e2, velocityX, velocityY);
//		}
//
//	}

	public void onExternalApplicationUrl(String url) {
		Logs.i("xia","----------------------离线");
	}

	public void onMailTo(String url) {
		
	}

	public void onUrlLoading(String url) {
		// TODO Auto-generated method stub
		
	}
	
	public void setUrlTag(String newUrl) {
	}
	
	private long lastTime = System.currentTimeMillis();

	public void requestUrlComment(String newUrl) {
		if(TextUtils.isEmpty(newUrl))return;
		long currentTime = System.currentTimeMillis();
		if(currentTime - lastTime > 500){
			activity.webTopBarManager.getCurrentWebView().setLoadedUrl(newUrl);
		}
		lastTime = currentTime;
	}

	public void setHttpAuthUsernamePassword(String host, String realm,
			String nm, String pw) {
	}
}

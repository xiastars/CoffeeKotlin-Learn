package com.summer.helper.web;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class WebTopBarManager{
	private LinearLayout llIconLayout;
	private TextView headerTitle;
	private ImageView ivUrlIcon;
	private ProgressBar mProgressBar;
	private RelativeLayout mTopBar;
	private ImageView ivBackIcon;
	private RelativeLayout rightLayout;
	private ImageButton mMoreButton;
	public CustomWebView mCurrentWebView;
	private WebContainerActivity activity;
	public boolean isFromMainJump = false;
	public boolean isMenuVisible = false;
	private RelativeLayout rlCommented;
	
	public WebTopBarManager(WebContainerActivity activity){
		this.activity = activity;
	}
	
	public ProgressBar getProgressBar(){
		return mProgressBar;
	}
	
	public RelativeLayout getTopBar(){
		return mTopBar;
	}
	
	public RelativeLayout.LayoutParams getTopBarLayoutParams(){
		return (LayoutParams) mTopBar.getLayoutParams();
	}
	
	public TextView getTitleView(){
		return headerTitle;
	}
	
	public RelativeLayout getCommented(){
		return rlCommented;
	}
	public LinearLayout getIconLayout(){
		return llIconLayout;
	}
	
	public ImageView getUrlIcon(){
		return ivUrlIcon;
	}
	
	public RelativeLayout getRightLayout(){
		return rightLayout;
	}
	
	public ImageView getBackIcon(){
		return ivBackIcon;
	}
	
	public ImageButton getMoreButton(){
		return mMoreButton;
	}
	
	public CustomWebView getCurrentWebView() {
		return mCurrentWebView;
	}

	public void setCurrentWebView(CustomWebView mCurrentWebView) {
		this.mCurrentWebView = mCurrentWebView;
		showViews();
		mCurrentWebView.setOnCustomScroolChangeListener(activity.webTopBarManager.getTopBar(), new ToolbarWebViewScrollListener(){
            @Override
            public void onHide() {
            }

			@Override
            public void onShow() {
            }
        });
	}
	
	private static final long ANIMATION_DURATION = 400;
	private Animation animation;
	private int headerTop;
	
	private void hideViews() {
	    animateHeader(headerTop, -getTopBar().getHeight());
	}

	public void showViews() {
 	    float webcontent = mCurrentWebView.getContentHeight()*mCurrentWebView.getScale();//webview的高度                
	    float webnow = mCurrentWebView.getHeight()+ mCurrentWebView.getScrollY();//当前webview的高度 
		if(webcontent - webnow == 0)return;
		animateHeader(-getTopBar().getHeight(), 0);
		headerTop = 0;
	}
	
	/**
     * Animates the marginTop property of the header between two specified values.
     * @param startTop Initial value for the marginTop property.
     * @param endTop End value for the marginTop property.
     */
    private void animateHeader(final float startTop, float endTop) {
        cancelAnimation();
        final float deltaTop = endTop - startTop;
        animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                headerTop = (int) (startTop + deltaTop * interpolatedTime);
                getTopBarLayoutParams().topMargin = headerTop;
                getTopBar().setLayoutParams(getTopBarLayoutParams());
            }
        };
        long duration = (long) (deltaTop / (float) getTopBar().getHeight() * ANIMATION_DURATION);
        animation.setDuration(Math.abs(duration));
        getTopBar().startAnimation(animation);
    }

    private void cancelAnimation() {
        if (animation != null) {
        	getTopBar().clearAnimation();
            animation = null;
        }
    }
	
	public void refreshing(){
		mCurrentWebView.reload();
	}
	
	private void backAct() {
		activity.finish();
	}
}

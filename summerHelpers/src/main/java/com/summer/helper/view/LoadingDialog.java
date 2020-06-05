package com.summer.helper.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malata.summer.helper.R;
import com.summer.helper.utils.Logs;
/**
 * 加载进度条
 * @author xiastars@vip.qq.com
 *
 */
public class LoadingDialog {
	Context mMainActivity;
	TextView loadingText;
	RelativeLayout rlLoading;
	int mLoadingIndex = 0;
	String loadingWord = "加载中";
	Dialog dialog ;
	
	public LoadingDialog(Context context){
		dialog = new Dialog(context, R.style.TagFullScreenDialog);
		dialog.setContentView(R.layout.dialog_loading);
		rlLoading = (RelativeLayout) dialog.findViewById(R.id.rl_loading);
		loadingText = (TextView) dialog.findViewById(R.id.tv_loading);
		startLoading();
	}
	
	public void cancelLoading(){
		if(dialog != null){
			dialog.cancel();
		}
		rlLoading.setVisibility(View.GONE);
	}
	
	public void startLoading(){
		loadingWord = "加载中";
		rlLoading.setVisibility(View.VISIBLE);
		circleTextLoading();
		if(dialog != null){
			dialog.show();
		}
	}
	
	public void startLoading(String text){
		loadingWord = text;
		rlLoading.setVisibility(View.VISIBLE);
		circleTextLoading();
	}
	
	public int getVisibility(){
		return rlLoading.getVisibility();
	}
	
	/**
	 * 设置loading的文字变动 
	 */
	private void circleTextLoading(){
		
		final String sPot = ".";
		new Handler().postDelayed(new Runnable() {
			public void run() {
				if(mLoadingIndex == 3){
					mLoadingIndex = 0;
				}
				if(mLoadingIndex == 0){
					loadingText.setText(loadingWord+sPot);
				}else if(mLoadingIndex == 1){
					Logs.i("text:"+loadingWord+sPot+sPot);
					loadingText.setText(loadingWord+sPot+sPot);
				}else if(mLoadingIndex == 2){
					loadingText.setText(loadingWord+sPot+sPot+sPot);
				}
				mLoadingIndex ++;
				if(rlLoading.getVisibility() == View.VISIBLE){
					circleTextLoading();	
				}
			}
		}, 1000);
	}
	
	private void setText(){
	}

}

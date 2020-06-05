package com.summer.demo.dialog;

import android.app.Dialog;
import android.content.Context;

import com.summer.demo.R;

/**
 * 加载进度条
 * @author xiastars@vip.qq.com
 *
 */
public class LoadingDialog {
	Dialog dialog ;

	public LoadingDialog(Context context){
		dialog = new Dialog(context, R.style.TagFullScreenDialog);
		dialog.setContentView(R.layout.dialog_loading1);
		//是否点击空白处关掉Dialog,一般设置为true
		dialog.setCanceledOnTouchOutside(true);
	}

	public void cancelLoading(){
		if(dialog != null){
			dialog.cancel();
		}
	}

	public void startLoading(){
		if(dialog != null){
			dialog.show();
		}
	}

}

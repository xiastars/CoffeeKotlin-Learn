package com.summer.helper.utils;

import com.malata.summer.helper.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
/**
 * @author malata_xiaqiliang
 * @time 2016年6月22日
 */
public class TipDialog extends Dialog{
	
	private DialogAfterClickListener listener;
	private String name;
	private int layoutid;
	
	public TipDialog(Context context,String name,DialogAfterClickListener listener)
	{
		super(context, R.style.TagFullScreenDialog);
		this.listener = listener;
		this.name = name;
		layoutid = R.layout.dialog_tips;
	}
	
	public TipDialog(Context context,int layoutid,DialogAfterClickListener listener)
	{
		super(context, R.style.TagFullScreenDialog);
		this.listener = listener;
		this.layoutid = layoutid;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(layoutid);
		
		initView();
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void initView(){
		TextView tips_tv = (TextView) findViewById(R.id.tips_tv);
		if(!TextUtils.isEmpty(name)){
			tips_tv.setText(name);
		}
		TextView tvOK = (TextView) findViewById(R.id.tips_ok_tv);
		SUtils.clickTransColor(tvOK);
		tvOK.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(null != listener){
					listener.onSure();
				}
				TipDialog.this.cancel();
			}

		});
		TextView tvCancel = (TextView) findViewById(R.id.tips_cancel_tv);
		SUtils.clickTransColor(tvCancel);
		tvCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
                if(null != listener){
                	listener.onCancel();
				}
				TipDialog.this.cancel();
			}
		});
		
	}
	
	public interface DialogAfterClickListener{
		void onSure();
		void onCancel();
	}
	
}

package com.summer.helper.view;

import com.malata.summer.helper.R;

import android.content.Context;
import android.graphics.Color;
import android.widget.ScrollView;

public class SScrollView extends ScrollView{

	public SScrollView(Context context) {
		super(context);
	}
	
	public void setViewSelect(boolean select){
		if(select){
			this.setBackgroundColor(Color.parseColor("#662B795D"));
		}else{
			this.setBackgroundResource(R.drawable.trans);
		}
	}

}

package com.summer.helper.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.malata.summer.helper.R;
import com.summer.helper.view.DatePicker;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class TimePickerDialog {
	Context context;
	
	String disireContent = "";
	
	String formatContent = "yyyy-MM-dd";
	
	public TimePickerDialog(Context context){
		this.context = context;
	}
	
	public void setDisireContent(String content){
		this.disireContent = content;
	}
	
	public void setFormatContent(String content){
		this.formatContent = content;
	}
	
	/**
	 * 显示时间选择器
	 */
	public void show(final OnTimePickerListener listener){
		final Dialog mDialog = new Dialog(context,R.style.AiTheme);
		mDialog.show();
		mDialog.setContentView(R.layout.dialog_timepicker);	
		final DatePicker datePicker = (DatePicker) mDialog.findViewById(R.id.datePicker);
		final TextView timeView = (TextView) mDialog.findViewById(R.id.tv_time);
		/*
		 * 取出用户之前设置的生日，将之添充在日历上，避免每次打开日历显示当前年月日
		 */
		if(!TextUtils.isEmpty(disireContent)){
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR,Integer.parseInt(disireContent.substring(0,4)));
			c.set(Calendar.MONTH, Integer.parseInt(disireContent.substring(5,7))-1);
			c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(disireContent.substring(8,10)));
			String time = SUtils.getDays(c.getTime());
			timeView.setText(time);
			datePicker.setCalendar(c);
		}else{
			timeView.setText(SUtils.getDays(new Date()));	
		}

		datePicker.receiveListener(new DatePicker.OnDateChangedListener() {
			
			@Override
			public void getTime() {
				Calendar c = setCalendar(datePicker);
				String time = SUtils.getDays(c.getTime());
				timeView.setText(time);
			}
		});
		
		TextView tvFinished = (TextView)mDialog.findViewById(R.id.tv_finish);
		tvFinished.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				/*
				 * 判断，当用户设置年月日大于当天时
				 */
				Calendar c = setCalendar(datePicker);
//				if(System.currentTimeMillis() - c.getTimeInMillis() < 0){
//					SUtils.makeToast(context, "请勿选择无效时间!");
//				}else{
//					
//				}	
				disireContent = getCDays(c.getTime());
				listener.receiveContent(disireContent);
				mDialog.cancel();
			}
		});

	}
	
	/**
	 * 转换时间，格式--
	 * @param date
	 * @return
	 */
	private String getCDays(Date date){
		SimpleDateFormat format = new SimpleDateFormat(formatContent,Locale.CHINA);
		String s = format.format(date);
		return s;
	}
	
	/**
	 * 设置日历的时间
	 * @param year
	 * @param month
	 * @param day
	 * @param timeView
	 */
	private Calendar setCalendar(DatePicker datePicker){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR,datePicker.getYear());
		c.set(Calendar.MONTH, datePicker.getMonth());
		c.set(Calendar.DAY_OF_MONTH, datePicker.getDay());
		return c;
	}
	
	public interface OnTimePickerListener{
		void receiveContent(String content);
	}

}

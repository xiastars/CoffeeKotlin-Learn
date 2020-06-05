package com.summer.helper.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 广播封装
 */
public class ReceiverUtils {
	private Activity activity;
	private HomeKeyEventBroadCastReceiver receiver;
	private String[] actions;
	private ReceiverListener listener;

	public ReceiverUtils(Activity activity) {
		this.activity = activity;
	}

	public void setActionsAndRegister(String... strings) {
		this.actions = strings;
		registerReceiver();
	}

	public void setOnReceiverListener(ReceiverListener listener) {
		this.listener = listener;
	}

	/**
	 * 注册Home按键广播
	 */
	private void registerReceiver() {
		if (receiver != null) return;
		receiver = new HomeKeyEventBroadCastReceiver();
		IntentFilter filter = new IntentFilter();
		for (int i = 0; i < actions.length; i++) {
			filter.addAction(actions[i]);
		}
		activity.registerReceiver(receiver, filter);
	}

	public void unRegisterReceiver() {
		if (receiver != null) {
			activity.unregisterReceiver(receiver);
			receiver = null;
		}
	}

	class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			for (int i = 0; i < actions.length; i++) {
				if (action.equals(actions[i])) {
					listener.doSomething(action, intent);
				}
			}
		}
	}

	public interface ReceiverListener {
		void doSomething(String action, Intent intent);
	}


}

package com.summer.helper.bt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.summer.helper.utils.SUtils;

public class AudioStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1);
		String stateStr = "未知";
		switch (state) {
		case BluetoothHeadset.STATE_AUDIO_CONNECTED:
			stateStr = "已连接";
			System.out.println("BluetoothHeadset.STATE_CONNECTED");
			break;
		case BluetoothHeadset.STATE_AUDIO_CONNECTING:
			System.out.println("BluetoothHeadset.STATE_AUDO_CONNECTING");
			break;
		case BluetoothHeadset.STATE_AUDIO_DISCONNECTED:
			stateStr = "已关闭";
			System.out.println("BluetoothHeadset.STATE_AUDIO_DISCONNECTED");
			break;
		}
		BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		Bundle bundle = intent.getExtras();
        SUtils.makeToast(context,bundle+","+device.getName() + " : " + stateStr);
	}
}

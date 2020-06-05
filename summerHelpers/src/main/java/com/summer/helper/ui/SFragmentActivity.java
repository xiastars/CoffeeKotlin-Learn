package com.shenqijiazu.helper.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class SFragmentActivity extends FragmentActivity {
	protected Context context;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		context = this;
	}

}

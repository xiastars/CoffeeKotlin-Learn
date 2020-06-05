package com.summer.demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.summer.demo.R;

/**
 * Created by xiaqiliang on 2016年12月29日 17:38.
 */

public class DialogWeixin extends Dialog {
    Context context;

    public DialogWeixin(Context context) {
        super(context, R.style.TagFullScreenDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_weixin);
    }
}
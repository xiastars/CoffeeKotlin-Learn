package com.summer.demo.dialog;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.summer.demo.R;
import com.summer.helper.utils.SUtils;

/**
 * @author xiastars@vip.qq.com
 */
public class TipDialog extends Dialog {

    private DialogAfterClickListener listener;
    private String name;
    private int layoutid;
    private String mLoadUrl;

    TextView tvOK;
    TextView tvCancel;

    public TipDialog(Context context, String name, DialogAfterClickListener listener) {
        super(context, R.style.TagFullScreenDialog);
        this.listener = listener;
        this.name = name;
        layoutid = R.layout.dialog_tips;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutid);

        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        TextView tips_tv = (TextView) findViewById(R.id.tips_tv);
        if (!TextUtils.isEmpty(name)) {
            tips_tv.setText(name);
        }
        tvOK = (TextView) findViewById(R.id.tips_ok_tv);
        SUtils.clickTransColor(tvOK);
        tvOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onSure();
                }
                TipDialog.this.cancel();
            }

        });
        tvCancel = (TextView) findViewById(R.id.tips_cancel_tv);
        SUtils.clickTransColor(tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onCancel();
                }
                TipDialog.this.cancel();
            }
        });

        setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                if (null != listener) {
                    listener.onCancel();
                }
            }
        });

    }

    /**
     * 自定义左边按钮的文字
     *
     * @param id
     */
    public void setOkText(int id) {
        tvOK.setText(getContext().getResources().getString(id));
    }

    /**
     * 自定义右边按钮的文字
     *
     * @param id
     */
    public void setCancelText(int id) {
        tvCancel.setText(getContext().getResources().getString(id));
    }

    public interface DialogAfterClickListener {
        void onSure();

        void onCancel();
    }

}

package com.summer.demo.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.summer.demo.R;
import com.summer.helper.dialog.BaseCenterDialog;
import com.summer.helper.utils.SUtils;

/**
 * 基础的提示
 */
public class BaseTipsDialog extends BaseCenterDialog {

    private DialogAfterClickListener listener;

    private int layoutid;
    private TextView tvContent;
    private TextView tvTitle;
    private static BaseTipsDialog tipDialog = null;

    String name;
    String okContent;
    String cancelContent;
    String content;
    String title;

    int cancelRes;
    boolean showCancelView;//显示X

    boolean showTitle = true;

    public static BaseTipsDialog getInstance(Context context, String name, DialogAfterClickListener listener) {
        if (tipDialog != null) {
            tipDialog.cancel();
            tipDialog = null;
        }
        tipDialog = new BaseTipsDialog(context, name, listener);
        return tipDialog;
    }

    public BaseTipsDialog(Context context, String name, DialogAfterClickListener listener) {
        super(context, R.style.TagFullScreenDialog);
        this.listener = listener;
        this.name = name;
        layoutid = R.layout.dialog_nf;
    }

    public BaseTipsDialog(Context context, int layoutid, DialogAfterClickListener listener) {
        super(context, R.style.TagFullScreenDialog);
        this.listener = listener;
        this.layoutid = layoutid;
    }

    @Override
    public int setContainerView() {
        return layoutid;
    }

    @Override
    public void initView(View view) {
        tvContent = (TextView) view.findViewById(R.id.tv_content);

        if (tvContent != null) {
            if (!TextUtils.isEmpty(name)) {
                SUtils.setHtmlText(name, tvContent);
            }
            if (!TextUtils.isEmpty(content)) {
                SUtils.setHtmlText(content, tvContent);
            }
        }
        TextView tvOK = (TextView) view.findViewById(R.id.tips_ok_tv);
        if(tvOK != null){
            if (!TextUtils.isEmpty(okContent)) {
                tvOK.setText(okContent);
            }
            SUtils.clickTransColor(tvOK);
            tvOK.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (null != listener) {
                        listener.onSure();
                    }
                    cancelDialog();
                }

            });
        }

        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        if (title != null) {
            tvTitle.setText(title);
        }
        tvTitle.setVisibility(showTitle ? View.VISIBLE : View.GONE);
        TextView tvCancel = (TextView) view.findViewById(R.id.tips_cancel_tv);
        if (tvCancel != null) {
            if (!TextUtils.isEmpty(cancelContent)) {
                tvCancel.setText(cancelContent);
            }
            if(cancelRes != 0){
                tvCancel.setTextColor(cancelRes);
            }
            SUtils.clickTransColor(tvCancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (null != listener) {
                        listener.onCancel();
                    }
                    cancelDialog();
                }
            });
        }
        ImageView ivCancel = (ImageView) view.findViewById(R.id.iv_cancel);
        if(ivCancel !=null){
            if(showCancelView){
                ivCancel.setVisibility(View.VISIBLE);
            }
            ivCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelDialog();
                }
            });
        }

    }

    public void hideTitle(){
        showTitle = false;
    }

    public void setOkContent(String content) {
        this.okContent = content;
    }

    public void setCancelContent(String content) {
        this.cancelContent = content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
        if (tvTitle != null)
            tvTitle.setText(title);
    }

    public int getCancelRes() {
        return cancelRes;
    }

    public void setCancelRes(int cancelRes) {
        this.cancelRes = cancelRes;
    }

    public boolean isShowCancelView() {
        return showCancelView;
    }

    public void setShowCancelView(boolean showCancelView) {
        this.showCancelView = showCancelView;
    }

    public interface DialogAfterClickListener {
        void onSure();

        void onCancel();
    }

}
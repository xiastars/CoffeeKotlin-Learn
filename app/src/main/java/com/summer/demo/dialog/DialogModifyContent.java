package com.summer.demo.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.summer.demo.R;
import com.summer.demo.listener.OnModifyContentListener;
import com.summer.helper.dialog.BaseCenterDialog;
import com.summer.helper.utils.STextUtils;
import com.summer.helper.utils.SUtils;

import butterknife.BindView;

/**
 * 通用修改内容
 */
public class DialogModifyContent extends BaseCenterDialog {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.tv_content)
    EditText tvContent;
    @BindView(R.id.ll_layout)
    LinearLayout llLayout;
    @BindView(R.id.view)
    View view;
    @BindView(R.id.ll_cancel)
    LinearLayout llCancel;
    @BindView(R.id.ll_sure)
    LinearLayout llSure;
    @BindView(R.id.iamfather)
    RelativeLayout iamfather;
    @BindView(R.id.tv_sure)
    TextView tvSure;

    boolean isSureable;

    String titleContent;
    int maxTextLength = 1;
    String defaultContent;
    String defaultHint;

    String okContent;

    int inputType;

    OnModifyContentListener onReturnObjectClickListener;

    public DialogModifyContent(@NonNull Context context, OnModifyContentListener listener) {
        super(context);
        this.onReturnObjectClickListener = listener;
    }

    @Override
    public int setContainerView() {
        return R.layout.dialog_edit_content;
    }

    @Override
    public void initView(View view) {
        tvContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isSureable = s.length() > 0;
                tvSure.setTextColor(isSureable ? getResourceColor(R.color.yellow_ff9) : getResourceColor(R.color.grey_c5));
            }
        });
        if (!TextUtils.isEmpty(titleContent)) {
            tvTitle.setText(titleContent);
        }
        if (!TextUtils.isEmpty(defaultHint)) {
            tvContent.setHint(defaultHint);
        }
        if(!TextUtils.isEmpty(okContent)){
            tvSure.setText(okContent);
        }
        if(inputType != 0){
            tvTitle.setInputType(inputType);
        }

        tvContent.setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(maxTextLength)});
        llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog();
            }
        });
        if (!TextUtils.isEmpty(defaultContent)) {
            tvContent.setText(defaultContent);
            SUtils.setSelection(tvContent);
        }
        llSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSureable) {
                    return;
                }
                String content = tvContent.getText().toString();
                if (STextUtils.isEmpty(content)) {
                    SUtils.makeToast(context, R.string.toast_no_empty);
                    return;
                }
                cancelDialog();
                onReturnObjectClickListener.returnContent(content);
            }
        });
        tvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                SUtils.showSoftInpuFromWindow(tvContent);
            }
        },300);
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }

    public void setOkContent(String okContent) {
        this.okContent = okContent;
    }

    public void setDefaultHint(String defaultHint) {
        this.defaultHint = defaultHint;
    }

    public void setMaxTextLength(int maxTextLength) {
        this.maxTextLength = maxTextLength;
    }

    public String getDefaultContent() {
        return defaultContent;
    }

    public void setDefaultContent(String defaultContent) {
        this.defaultContent = defaultContent;
    }

    public void setTitleContent(String titleContent) {
        this.titleContent = titleContent;
    }

    public void setOnReturnObjectClickListener(OnModifyContentListener onReturnObjectClickListener) {
        this.onReturnObjectClickListener = onReturnObjectClickListener;
    }
}

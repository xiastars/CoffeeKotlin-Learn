package com.summer.helper.dialog;

import android.content.Context;
import android.support.annotation.NonNull;

import com.malata.summer.helper.R;


/**
 * 中间弹出框基本样式
 * Created by xiastars on 2017/6/20.
 */

public abstract class BaseCenterDialog extends BaseDialog {

    public BaseCenterDialog(@NonNull Context context) {
        super(context, R.style.TagFullScreenDialog);
        this.context = context;
        setCanceledOnTouchOutside(true);
    }

    public BaseCenterDialog(@NonNull Context context, int style) {
        super(context, style);
        this.context = context;
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected int showEnterAnim() {
        return R.anim.dialog_center_enter;
    }

    @Override
    protected int showQuitAnim() {
        return R.anim.dialog_center_quit;
    }

}

package com.summer.helper.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malata.summer.helper.R;
import com.summer.helper.dialog.adapter.SelectIntegralAdapter;
import com.summer.helper.dialog.adapter.VotingValueAdapter;
import com.summer.helper.listener.OnSimpleClickListener;
import com.summer.helper.utils.SUtils;
import com.summer.helper.view.NRecycleView;

/**
 * 底部列表Dialog
 * Created by xiaqiliang on 2017/6/22.
 */

public class BottomListDialog extends BaseBottomDialog {

    TextView tvTopTitle;
    TextView tvFinish;
    protected NRecycleView nvContainer;
    TextView tvBottomContent;
    RelativeLayout rlTop;
    TextView tvCancel;

    int showFinishView;
    int showBottomContent;
    int showTopContent;
    boolean showCancelView;
    String topTitle;
    String bottomContent;
    String[] datas = {"50", "200", "500"};
    int selectPosition;
    boolean stringType = false;

    OnSimpleClickListener listener;
    SpannableString spannbleString;
    RecyclerView.Adapter adapter;

    public BottomListDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public int setContainerView() {
        return R.layout.strirup_dialog_in;
    }

    public void setTopTitle(String title) {
        this.topTitle = title;
    }

    public void setListener(OnSimpleClickListener listener) {
        this.listener = listener;
    }

    public void showBottomContent(int visible) {
        this.showBottomContent = visible;
    }

    public void showFinishContent(int visible) {
        this.showFinishView = visible;
    }

    public void setBottomContent(String content) {
        bottomContent = content;
    }

    public void setSpannableString(SpannableString content) {
        this.spannbleString = content;
    }

    public void setDatas(String[] datas) {
        this.datas = datas;
    }

    /**
     * 纯数字类型
     */
    public void setStringType() {
        this.stringType = true;
    }

    @Override
    public void initView(View view) {
        tvTopTitle = view.findViewById(R.id.tv_top_title);
        tvFinish = view.findViewById(R.id.tv_finish);
        init();
        setCanceledOnTouchOutside(true);
        if (!TextUtils.isEmpty(topTitle)) {
            tvTopTitle.setText(topTitle);
        }
        if (!TextUtils.isEmpty(bottomContent)) {
            tvBottomContent.setText(bottomContent);
        }
        if (spannbleString != null) {
            tvBottomContent.setText(spannbleString);
        }
        rlTop.setVisibility(showTopContent);
        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFinishClick();
                cancelDialog();
            }
        });
        tvFinish.setVisibility(showFinishView);
        tvBottomContent.setVisibility(showBottomContent);
        tvCancel.setVisibility(showCancelView ? View.VISIBLE : View.GONE);
        setDialogBottom();
        if (stringType) {
            nvContainer.setList();
            nvContainer.setCommonDividerGrey(SUtils.getDip(context, 10), SUtils.getDip(context, 10));

            setAdapter(new SelectIntegralAdapter(context, datas, new OnSimpleClickListener() {
                @Override
                public void onClick(int position) {
                    if (listener != null) {
                        listener.onClick(position);
                    }
                }
            }));
        } else {
            nvContainer.setGridView(3);
            setAdapter(new VotingValueAdapter(context, datas, new OnSimpleClickListener() {
                @Override
                public void onClick(int position) {
                    selectPosition = position;
                    if (listener != null) {
                        listener.onClick(position);
                    }
                }
            }));
        }

    }

    /**
     * 点击完成键
     */
    protected void onFinishClick(){

    }

    /**
     * 留给子类配置
     */
    protected void init() {

    }

    protected void setAdapter(RecyclerView.Adapter adapter) {
        nvContainer.setAdapter(adapter);
    }

    public void setShowCancelView(boolean showCancelView){
        this.showCancelView = showCancelView;
    }

    @Override
    protected int showEnterAnim() {
        return R.anim.slide_up;
    }

    @Override
    protected int showQuitAnim() {
        return R.anim.slide_bottom;
    }

    public void showTopContent(int gone) {
        this.showTopContent = gone;
    }
}
package com.summer.demo.module.emoji;

import android.app.Activity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.summer.demo.R;
import com.summer.helper.listener.OnReturnObjectClickListener;
import com.summer.helper.utils.Logs;
import com.summer.helper.utils.SUtils;

/**
 * Created by xiastars on 2017/9/7.
 */

public class EmojiHelper {
    Activity activity;
    EditText edtComment;
    RelativeLayout rlEmojiLayout;
    ImageView ivComment;

    //是否正在显示表情
    boolean isEmojiView;
    //是否处理编辑模式
    boolean isEditMsg;

    /**
     * 初始化表情View
     */
    public void initEmojiView(final Activity activity) {
        this.activity = activity;
        edtComment = (EditText) activity.findViewById(R.id.edt_comment);
        ivComment = (ImageView) activity.findViewById(R.id.iv_emoji);
        SUtils.setPicResource(ivComment,R.drawable.ic_biaoqing);
        rlEmojiLayout = (RelativeLayout) activity.findViewById(R.id.emoji_layout);
        // 评论布局
        new MyEmojiView(activity, 0, new OnReturnObjectClickListener() {
            @Override
            public void onClick(Object object) {
                IconEntity iconEntity = (IconEntity) object;
                String name = iconEntity.getName();
                if (!TextUtils.isEmpty(name)) {
                    if (!name.equals("[]")) {
                        int index = edtComment.getSelectionStart();
                        Editable editable = edtComment.getEditableText();
                        String emojiText = iconEntity.getEmojiText();
                        if(emojiText != null){
                            int maxLength = edtComment.getText().toString().length();
                            if(maxLength + name.length() > 140){
                                return;
                            }
                            //直接将表情显示在EditTextView上
                            SpannableString text = MyEmojiService.getInstance(activity).replaceEmoji(emojiText, (int) edtComment.getTextSize());
                            editable.insert(index, text);
                        }
                    } else {
                        //动作按下
                        int action = KeyEvent.ACTION_DOWN;
                        //code:删除，其他code也可以，例如 code = 0
                        int code = KeyEvent.KEYCODE_DEL;
                        KeyEvent event = new KeyEvent(action, code);
                        edtComment.onKeyDown(KeyEvent.KEYCODE_DEL, event); //抛给系统处理了
                    }
                }
            }
        });
        initView();
    }

    /**
     * 初始化表情View
     */
    public void initEmojiView(final View view) {
        this.activity = (Activity) view.getContext();
        edtComment = (EditText) view.findViewById(R.id.edt_comment);
        ivComment = (ImageView) view.findViewById(R.id.iv_emoji);
        SUtils.setPicResource(ivComment,R.drawable.ic_biaoqing);
        rlEmojiLayout = (RelativeLayout) view.findViewById(R.id.emoji_layout);
        // 评论布局
        new MyEmojiView(activity, 0, new OnReturnObjectClickListener() {
            @Override
            public void onClick(Object object) {
                IconEntity iconEntity = (IconEntity) object;
                String name = iconEntity.getName();
                if (!TextUtils.isEmpty(name)) {
                    if (!name.equals("[]")) {
                        int index = edtComment.getSelectionStart();
                        Editable editable = edtComment.getEditableText();
                        String emojiText = iconEntity.getEmojiText();
                        if(emojiText != null){
                            int maxLength = edtComment.getText().toString().length();
                            if(maxLength + name.length() > 140){
                                return;
                            }
                            //直接将表情显示在EditTextView上
                            SpannableString text = MyEmojiService.getInstance(activity).replaceEmoji(emojiText, (int) edtComment.getTextSize());
                            editable.insert(index, text);
                        }
                    } else {
                        //动作按下
                        int action = KeyEvent.ACTION_DOWN;
                        //code:删除，其他code也可以，例如 code = 0
                        int code = KeyEvent.KEYCODE_DEL;
                        KeyEvent event = new KeyEvent(action, code);
                        edtComment.onKeyDown(KeyEvent.KEYCODE_DEL, event); //抛给系统处理了
                    }
                }
            }
        });
        initView();
    }

    private void initView() {
        /* 当表情视图可见时,点击输入框,表情视图与键盘同时存在,这里处理,当点击时,表情视图隐藏*/
        if(edtComment == null){
            return;
        }
        edtComment.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (rlEmojiLayout != null && rlEmojiLayout.getVisibility() == View.VISIBLE) {
                        rlEmojiLayout.setVisibility(View.GONE);
                        isEmojiView = false;
                    }
                }
                return false;
            }
        });

        ivComment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isEmojiView == false) {
                    setEmojiLayoutVisible();
                } else {
                    setEmojiLayoutInvisible(true);
                }
            }
        });
    }

    /**
     * 隐藏表情发送界面
     */
    public void setEmojiLayoutInvisible(boolean showSoft) {
        if (rlEmojiLayout != null) rlEmojiLayout.setVisibility(View.GONE);
        isEditMsg = false;
        isEmojiView = false;
        Logs.i("隐藏表情");
        SUtils.setPicResource(ivComment,R.drawable.ic_biaoqing);
        if(showSoft){
            SUtils.showSoftInpuFromWindow(edtComment);
        }
    }

    /**
     * 显示表情选择页面
     */
    private void setEmojiLayoutVisible() {
        isEmojiView = true;
        SUtils.hideSoftInpuFromWindow(edtComment);
        ivComment.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rlEmojiLayout != null) rlEmojiLayout.setVisibility(View.VISIBLE);
                SUtils.setPicResource(ivComment,R.drawable.input_icon_keyboard);

            }
        },100);

    }

    public boolean isEmojiView() {
        return isEmojiView;
    }

    public void setEmojiView(boolean emojiView) {
        isEmojiView = emojiView;
    }
}

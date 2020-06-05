package com.summer.demo.module.emoji

import android.app.Activity
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import com.summer.demo.R
import com.summer.demo.module.emoji.MyEmojiService.Companion.getInstance
import com.summer.helper.listener.OnReturnObjectClickListener
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SUtils

/**
 * Created by xiastars on 2017/9/7.
 */
class EmojiHelper {
    var activity: Activity? = null
    var edtComment: EditText? = null
    var rlEmojiLayout: RelativeLayout? = null
    var ivComment: ImageView? = null
    //是否正在显示表情
    var isEmojiView = false
    //是否处理编辑模式
    var isEditMsg = false

    /**
     * 初始化表情View
     */
    fun initEmojiView(activity: Activity) {
        this.activity = activity
        edtComment = activity.findViewById<View>(R.id.edt_comment) as EditText
        ivComment = activity.findViewById<View>(R.id.iv_emoji) as ImageView
        SUtils.setPicResource(ivComment, R.drawable.ic_biaoqing)
        rlEmojiLayout = activity.findViewById<View>(R.id.emoji_layout) as RelativeLayout
        // 评论布局
        MyEmojiView(activity, 0, OnReturnObjectClickListener { `object` ->
            val iconEntity = `object` as IconEntity
            val name = iconEntity.name
            if (!TextUtils.isEmpty(name)) {
                if (name != "[]") {
                    val index = edtComment!!.selectionStart
                    val editable = edtComment!!.editableText
                    val emojiText = iconEntity.emojiText
                    if (emojiText != null) {
                        val maxLength = edtComment!!.text.toString().length
                        if (maxLength + name!!.length > 140) {
                            return@OnReturnObjectClickListener
                        }
                        //直接将表情显示在EditTextView上
                        val text = getInstance(activity)!!.replaceEmoji(emojiText, edtComment!!.textSize.toInt())
                        editable.insert(index, text)
                    }
                } else { //动作按下
                    val action = KeyEvent.ACTION_DOWN
                    //code:删除，其他code也可以，例如 code = 0
                    val code = KeyEvent.KEYCODE_DEL
                    val event = KeyEvent(action, code)
                    edtComment!!.onKeyDown(KeyEvent.KEYCODE_DEL, event) //抛给系统处理了
                }
            }
        })
        initView()
    }

    /**
     * 初始化表情View
     */
    fun initEmojiView(view: View) {
        activity = view.context as Activity
        edtComment = view.findViewById<View>(R.id.edt_comment) as EditText
        ivComment = view.findViewById<View>(R.id.iv_emoji) as ImageView
        SUtils.setPicResource(ivComment, R.drawable.ic_biaoqing)
        rlEmojiLayout = view.findViewById<View>(R.id.emoji_layout) as RelativeLayout
        // 评论布局
        MyEmojiView(activity!!, 0, OnReturnObjectClickListener { `object` ->
            val iconEntity = `object` as IconEntity
            val name = iconEntity.name
            if (!TextUtils.isEmpty(name)) {
                if (name != "[]") {
                    val index = edtComment!!.selectionStart
                    val editable = edtComment!!.editableText
                    val emojiText = iconEntity.emojiText
                    if (emojiText != null) {
                        val maxLength = edtComment!!.text.toString().length
                        if (maxLength + name!!.length > 140) {
                            return@OnReturnObjectClickListener
                        }
                        //直接将表情显示在EditTextView上
                        val text = getInstance(activity!!)!!.replaceEmoji(emojiText, edtComment!!.textSize.toInt())
                        editable.insert(index, text)
                    }
                } else { //动作按下
                    val action = KeyEvent.ACTION_DOWN
                    //code:删除，其他code也可以，例如 code = 0
                    val code = KeyEvent.KEYCODE_DEL
                    val event = KeyEvent(action, code)
                    edtComment!!.onKeyDown(KeyEvent.KEYCODE_DEL, event) //抛给系统处理了
                }
            }
        })
        initView()
    }

    private fun initView() { /* 当表情视图可见时,点击输入框,表情视图与键盘同时存在,这里处理,当点击时,表情视图隐藏*/
        if (edtComment == null) {
            return
        }
        edtComment!!.setOnTouchListener { v, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (rlEmojiLayout != null && rlEmojiLayout!!.visibility == View.VISIBLE) {
                    rlEmojiLayout!!.visibility = View.GONE
                    isEmojiView = false
                }
            }
            false
        }
        ivComment!!.setOnClickListener {
            if (isEmojiView == false) {
                setEmojiLayoutVisible()
            } else {
                setEmojiLayoutInvisible(true)
            }
        }
    }

    /**
     * 隐藏表情发送界面
     */
    fun setEmojiLayoutInvisible(showSoft: Boolean) {
        if (rlEmojiLayout != null) rlEmojiLayout!!.visibility = View.GONE
        isEditMsg = false
        isEmojiView = false
        Logs.i("隐藏表情")
        SUtils.setPicResource(ivComment, R.drawable.ic_biaoqing)
        if (showSoft) {
            SUtils.showSoftInpuFromWindow(edtComment)
        }
    }

    /**
     * 显示表情选择页面
     */
    private fun setEmojiLayoutVisible() {
        isEmojiView = true
        SUtils.hideSoftInpuFromWindow(edtComment)
        ivComment!!.postDelayed({
            if (rlEmojiLayout != null) rlEmojiLayout!!.visibility = View.VISIBLE
            SUtils.setPicResource(ivComment, R.drawable.input_icon_keyboard)
        }, 100)
    }

}
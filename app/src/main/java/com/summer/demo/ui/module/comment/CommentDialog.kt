package com.summer.demo.ui.module.comment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import com.summer.demo.R
import com.summer.helper.utils.SUtils
import com.summer.helper.view.LoadingDialog
import java.lang.ref.WeakReference

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/11 14:06
 */
class CommentDialog(internal var context: Context, internal var topicId: String, internal var listener: OnCommentedListener) : Dialog(context, R.style.MyDialog) {
    internal var replyName: String? = null
    internal var replyId: Long = 0

    internal var myHandler: MyHandler
    internal var loadingDialog: LoadingDialog? = null


    init {
        myHandler = MyHandler(this)
    }

    fun setReplyInfo(replyName: String, replyId: Long) {
        this.replyName = replyName
        this.replyId = replyId
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_comment)
        val parent = findViewById<View>(R.id.ll_parent) as RelativeLayout
        val window = window
        if (window != null) {
            val lp = window.attributes
            lp.width = SUtils.screenWidth
            lp.gravity = Gravity.BOTTOM
            window.attributes = lp
        }
        val etContent = findViewById<View>(R.id.edt_comment) as EditText
        if (replyName != null) {
            etContent.hint = "回复@" + replyName!!
        }
        parent.setOnClickListener {
            this@CommentDialog.cancel()
            SUtils.hideSoftInpuFromWindow(etContent)
        }
        this.setOnCancelListener {
            SUtils.makeToast(context, "关闭")
            SUtils.hideSoftInpuFromWindow(etContent)
            Handler().postDelayed({ SUtils.hideSoftInpuFromWindow(etContent) }, 150)
        }

        val cancel = findViewById<View>(R.id.btn_send) as Button
        cancel.setOnClickListener { sendComment(replyId, etContent.text.toString()) }
        Handler().postDelayed({ SUtils.showSoftInpuFromWindow(etContent, getContext()) }, 100)
    }

    private fun sendComment(replyId: Long, content: String) {
        if (TextUtils.isEmpty(content)) {
            SUtils.makeToast(context, context.getString(R.string.hint_empty_comment))
            return
        }
        loadingDialog = LoadingDialog(context)
        //发表评论
        /*       SummerParameter params = Const.getPostParameters();
        params.put("topicId", topicId);
        params.put("content", content);
        params.putLog("星战评论");
        if(replyId != 0){
            params.put("replyId", replyId);
        }
        EasyHttp.post(context, Server.STAR_COMMENT_SEND, CommentedResp.class, params, new RequestCallback<CommentedResp>() {
            @Override
            public void done(CommentedResp moveResp) {
                if (moveResp != null) {
                    new CodeRespondUtils(context, moveResp.getCode());
                    StarCommentInfo infos = moveResp.getDatas();
                    if (infos != null) {
                        myHandler.obtainMessage(0,infos).sendToTarget();
                    }
                    myHandler.sendEmptyMessage(1);
                }
            }

            @Override
            public void onError(int errorCode, String errorStr) {

            }
        });*/
    }

    private fun commentSuccess(info: StarCommentInfo) {
        listener.onSucceed(info)
    }

    class MyHandler(activity: CommentDialog) : Handler() {
        private val mActivity = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            val activity = mActivity.get()
            if (null != activity) {
                when (msg.what) {
                    0 -> {
                        val info = msg.obj as StarCommentInfo
                        activity.commentSuccess(info)
                    }
                    1 -> activity.cancelDialog()
                }
            }
        }
    }

    private fun cancelDialog() {
        this.cancel()
        if (loadingDialog != null) {
            loadingDialog!!.cancelLoading()
        }
    }

}

package com.summer.demo.helper

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.view.View
import android.widget.EditText

import com.summer.demo.R

object DialogHelper {
    fun getDialog(context: Context): AlertDialog.Builder {
        return AlertDialog.Builder(context, R.style.AppBaseTheme)
    }

    /**
     * 获取一个普通的消息对话框，没有取消按钮
     */
    @JvmOverloads
    fun getMessageDialog(
            context: Context,
            title: String,
            message: String,
            cancelable: Boolean = true): AlertDialog.Builder {
        return getDialog(context)
                .setCancelable(cancelable)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", null)
    }

    /**
     * 获取一个验证对话框
     */
    fun getWalleryConfirmDialog(
            context: Context, message: String,
            positiveListener: DialogInterface.OnClickListener): AlertDialog.Builder {
        return getDialog(context)
                .setMessage(message)
                .setPositiveButton("确定", positiveListener)
                .setNegativeButton("取消", null)
    }

    /**
     * 获取一个普通的消息对话框，没有取消按钮
     */
    fun getMessageDialog(context: Context, message: String): AlertDialog.Builder {
        return getMessageDialog(context, "", message, true)
    }

    /**
     * 获取一个普通的消息对话框，没有取消按钮
     */
    fun getMessageDialog(
            context: Context,
            title: String,
            message: String,
            positiveText: String): AlertDialog.Builder {
        return getDialog(context)
                .setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, null)
    }

    fun getConfirmDialog(context: Context,
                         title: String,
                         view: View,
                         positiveListener: DialogInterface.OnClickListener): AlertDialog.Builder {
        return getDialog(context)
                .setTitle(title)
                .setView(view)
                .setPositiveButton("确定", positiveListener)
                .setNegativeButton("取消", null)
    }

    fun getConfirmDialog(context: Context,
                         title: String,
                         view: View,
                         positiveText: String,
                         negativeText: String,
                         positiveListener: DialogInterface.OnClickListener): AlertDialog.Builder {
        return getDialog(context)
                .setTitle(title)
                .setView(view)
                .setCancelable(true)
                .setPositiveButton(positiveText, positiveListener)
                .setNegativeButton(negativeText, null)
    }

    /**
     * 获取一个验证对话框
     */
    @JvmOverloads
    fun getConfirmDialog(
            context: Context,
            title: String,
            message: String,
            positiveText: String = "确定",
            negativeText: String = "取消",
            cancelable: Boolean = false,
            positiveListener: DialogInterface.OnClickListener? = null,
            negativeListener: DialogInterface.OnClickListener? = null): AlertDialog.Builder {
        return getDialog(context)
                .setCancelable(cancelable)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, positiveListener)
                .setNegativeButton(negativeText, negativeListener)
    }

    /**
     * 获取一个验证对话框
     */
    fun getConfirmDialog(
            context: Context, message: String,
            positiveListener: DialogInterface.OnClickListener,
            negativeListener: DialogInterface.OnClickListener): AlertDialog.Builder {
        return getDialog(context)
                .setMessage(message)
                .setPositiveButton("确定", positiveListener)
                .setNegativeButton("取消", negativeListener)
    }

    fun getSingleChoiceDialog(
            context: Context,
            title: String,
            arrays: Array<String>,
            selectIndex: Int,
            onClickListener: DialogInterface.OnClickListener): AlertDialog.Builder {
        val builder = getDialog(context)
        builder.setSingleChoiceItems(arrays, selectIndex, onClickListener)
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.setNegativeButton("取消", null)
        return builder
    }

    /**
     * 获取一个验证对话框，没有点击事件
     */
    fun getConfirmDialog(
            context: Context,
            title: String,
            message: String,
            positiveText: String,
            negativeText: String,
            positiveListener: DialogInterface.OnClickListener): AlertDialog.Builder {
        return getConfirmDialog(
                context, title, message, positiveText, negativeText, true, positiveListener, null)
    }

    /**
     * 获取一个验证对话框，没有点击事件
     */
    fun getConfirmDialog(
            context: Context,
            message: String,
            positiveText: String,
            negativeText: String,
            cancelable: Boolean): AlertDialog.Builder {
        return getConfirmDialog(context, "", message, positiveText, negativeText, cancelable, null, null)
    }

    /**
     * 获取一个验证对话框，没有点击事件，取消、确定
     */
    fun getConfirmDialog(
            context: Context,
            title: String,
            message: String,
            cancelable: Boolean): AlertDialog.Builder {
        return getConfirmDialog(context, title, message, "确定", "取消", cancelable, null, null)
    }

    /**
     * 获取一个验证对话框，没有点击事件，取消、确定
     */
    fun getConfirmDialog(
            context: Context,
            message: String,
            cancelable: Boolean,
            positiveListener: DialogInterface.OnClickListener): AlertDialog.Builder {
        return getConfirmDialog(context, "", message, "确定", "取消", cancelable, positiveListener, null)
    }

    /**
     * 获取一个验证对话框，没有点击事件，取消、确定
     */
    fun getConfirmDialog(
            context: Context,
            message: String,
            positiveListener: DialogInterface.OnClickListener): AlertDialog.Builder {
        return getConfirmDialog(context, "", message, "确定", "取消", positiveListener)
    }

    /**
     * 获取一个输入对话框
     */
    @JvmOverloads
    fun getInputDialog(
            context: Context,
            title: String,
            editText: EditText,
            positiveText: String,
            negativeText: String,
            cancelable: Boolean,
            positiveListener: DialogInterface.OnClickListener,
            negativeListener: DialogInterface.OnClickListener? = null): AlertDialog.Builder {
        return getDialog(context)
                .setCancelable(cancelable)
                .setTitle(title)
                .setView(editText)
                .setPositiveButton(positiveText, positiveListener)
                .setNegativeButton(negativeText, negativeListener)
    }

    /**
     * 获取一个输入对话框
     */
    fun getInputDialog(
            context: Context,
            title: String,
            editText: EditText,
            cancelable: Boolean,
            positiveListener: DialogInterface.OnClickListener): AlertDialog.Builder {
        return getInputDialog(context, title, editText, "确定", "取消", cancelable, positiveListener, null)
    }

    /**
     * 获取一个等待对话框
     */
    fun getProgressDialog(context: Context): ProgressDialog {
        return ProgressDialog(context)
    }

    /**
     * 获取一个等待对话框
     */
    fun getProgressDialog(context: Context, cancelable: Boolean): ProgressDialog {
        val dialog = getProgressDialog(context)
        dialog.setCancelable(cancelable)
        return dialog
    }

    /**
     * 获取一个等待对话框
     */
    fun getProgressDialog(context: Context, message: String): ProgressDialog {
        val dialog = getProgressDialog(context)
        dialog.setMessage(message)
        return dialog
    }

    /**
     * 获取一个等待对话框
     */
    fun getProgressDialog(
            context: Context, title: String, message: String, cancelable: Boolean): ProgressDialog {
        val dialog = getProgressDialog(context)
        dialog.setCancelable(cancelable)
        dialog.setTitle(title)
        dialog.setMessage(message)
        return dialog
    }

    /**
     * 获取一个等待对话框
     */
    fun getProgressDialog(
            context: Context, message: String, cancelable: Boolean): ProgressDialog {
        val dialog = getProgressDialog(context)
        dialog.setCancelable(cancelable)
        dialog.setMessage(message)
        return dialog
    }

    fun getSelectDialog(
            context: Context, title: String, items: Array<String>,
            positiveText: String,
            itemListener: DialogInterface.OnClickListener): AlertDialog.Builder {
        return getDialog(context)
                .setTitle(title)
                .setItems(items, itemListener)
                .setPositiveButton(positiveText, null)

    }

    fun getSelectDialog(
            context: Context, items: Array<String>,
            positiveText: String,
            itemListener: DialogInterface.OnClickListener): AlertDialog.Builder {
        return getDialog(context)
                .setItems(items, itemListener)
                .setPositiveButton(positiveText, null)

    }

    fun getSelectDialog(context: Context, view: View, positiveText: String,
                        itemListener: DialogInterface.OnClickListener): AlertDialog.Builder {
        return getDialog(context)
                .setView(view)
                .setPositiveButton(positiveText, null)
    }

    //    public static AlertDialog.Builder getRecyclerViewDialog(Context context, BaseRecyclerAdapter.OnItemClickListener listener) {
    //        RecyclerView recyclerView = new RecyclerView(context);
    //        RecyclerView.LayoutParams params =
    //                new GridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    //        recyclerView.setPadding(Util.dipTopx(context, 16), Util.dipTopx(context, 16),
    //                Util.dipTopx(context, 16), Util.dipTopx(context, 16));
    //        recyclerView.setLayoutParams(params);
    //        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
    //        CommentItemAdapter adapter = new CommentItemAdapter(context);
    //        adapter.setOnItemClickListener(listener);
    //        recyclerView.setAdapter(adapter);
    //        recyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
    //        return getDialog(context)
    //                .setView(recyclerView)
    //                .setPositiveButton(null, null);
    //    }
}
/**
 * 获取一个普通的消息对话框，没有取消按钮
 */
/**
 * 获取一个验证对话框，没有点击事件
 */
/**
 * 获取一个验证对话框，没有点击事件
 */
/**
 * 获取一个验证对话框，没有点击事件，取消、确定
 */
/**
 * 获取一个输入对话框
 */

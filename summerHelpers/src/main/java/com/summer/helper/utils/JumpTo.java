package com.summer.helper.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

/**
 * 跳转方法，不需要传递Bundle使用commonJump(),如果传递内容以下方法不满足，请自定义
 * 所有跳转尽量写在这里
 *
 * @author malata_xiaqiliang
 * @time 2016年6月7日
 */
public class JumpTo {

    private static JumpTo jumpTo = null;
    public static String TYPE_LONG = "tag_long";
    public static String TYPE_INT = "tag_int";
    public static String TYPE_INT2 = "tag_int2";
    public static String TYPE_OBJECT = "key";
    public static String TYPE_STRING = "tag_string";
    public static String TYPE_STRING2 = "tag_string2";
    public static String TYPE_BOOLEAN = "tag_boolean";

    public static synchronized JumpTo getInstance() {
        if (jumpTo == null) {
            jumpTo = new JumpTo();
        }
        return jumpTo;
    }

    public class ShortcutJump {
        public static final String TYPE_URL = "jump_url";
        public static final String TYPE_LOGO = "jump_logo";
        public static final String TYPE_NAME = "jump_name";
    }

    /**
     * 普通的跳转方法，不需要传递数据
     *
     * @param context
     * @param cls
     */
    public void commonJump(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    /**
     * 普通的跳转方法，不需要传递数据
     *
     * @param context
     * @param cls
     */
    public void commonJump(Context context, ComponentName cls) {
        Intent intent = new Intent();
        intent.setClassName(cls.getPackageName(), cls.getClassName());
        context.startActivity(intent);
    }

    /**
     * 普通的跳转方法，不需要传递数据 可回调
     *
     * @param context
     * @param cls
     */
    public void commonResultJump(Context context, Class<?> cls, int requestCode) {
        Intent intent = new Intent(context, cls);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    /**
     * 普通的跳转方法，带一个标识符
     *
     * @param context
     * @param cls
     * @param tag
     */
    public void commonJump(Context context, Class<?> cls, long tag, int tag2) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_INT, tag2);
        intent.putExtra(TYPE_LONG, tag);
        context.startActivity(intent);
    }

    /**
     * 普通的跳转方法，带一个标识符
     *
     * @param context
     * @param cls
     * @param tag
     */
    public void commonJump(Context context, Class<?> cls, long tag, String tag2) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_LONG, tag);
        intent.putExtra(TYPE_STRING, tag2);
        context.startActivity(intent);
    }

    /**
     * 普通的跳转方法，带一个标识符和回调
     *
     * @param context
     * @param cls
     * @param tag
     */
    public void commonResultJump(Context context, Class<?> cls, long tag, int tag2, int requestCode) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_INT, tag2);
        intent.putExtra(TYPE_LONG, tag);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    /**
     * 单个long类型
     *
     * @param context
     * @param cls
     * @param tag
     */
    public void commonJump(Context context, Class<?> cls, long tag) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_LONG, tag);
        context.startActivity(intent);
    }

    /**
     * 单个int类型
     *
     * @param context
     * @param tag2
     */
    public void commonJump(Context context, Class<?> cls, int tag2) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_INT, tag2);
        context.startActivity(intent);
    }

    /**
     * String 类型
     *
     * @param context
     * @param cls
     * @param tag
     */
    public void commonJump(Context context, Class<?> cls, String tag) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_STRING, tag);
        context.startActivity(intent);
    }

    /**
     * String 类型 + Object类型
     *
     * @param context
     * @param cls
     * @param tag
     */
    public void commonJump(Context context, Class<?> cls, String tag, String tag2) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_STRING, tag);
        intent.putExtra(TYPE_STRING2, tag2);
        context.startActivity(intent);
    }

    /**
     * String 类型 + Object类型
     *
     * @param context
     * @param cls
     * @param tag
     */
    public void commonJump(Context context, Class<?> cls, String tag, Object object) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_STRING, tag);
        intent.putExtra(TYPE_OBJECT, (Serializable) object);
        context.startActivity(intent);
    }

	/**
	 * 简单的获取int参数的方法
	 * 
	 * @param context
	 */
	public static int getInteger(Activity context) {
		return context.getIntent().getIntExtra(TYPE_INT, 0);
	}

    public void commonJump(Context context, Class<?> cls, Object object) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_OBJECT, (Serializable) object);
        context.startActivity(intent);
    }

    public void commonJumpService(Context context, Class<?> cls, int index1, int index2, Object object) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_INT, index1);
        intent.putExtra(TYPE_INT2, index2);
        intent.putExtra(TYPE_OBJECT, (Serializable) object);
        context.startService(intent);
    }

    /**
     * 简单的获取Object参数的方法
     *
     * @param context
     */
    public static Object getObject(Activity context) {
        return context.getIntent().getSerializableExtra(TYPE_OBJECT);
    }
    
    /**
     * 简单的获取Long参数的方法
     *
     * @param context
     */
    public static long getLong(Activity context) {
        return context.getIntent().getLongExtra(TYPE_LONG, 0);
    }

    /**
     * 简单的获取String参数的方法
     *
     * @param context
     */
    public static String getString(Activity context) {
        return context.getIntent().getStringExtra(TYPE_STRING);
    }

    /**
     * 简单的获取Boolean参数的方法
     *
     * @param context
     */
    public static boolean getBoolean(Activity context) {
        return context.getIntent().getBooleanExtra(TYPE_BOOLEAN, false);
    }

    /**
     * 简单的获取String参数的方法
     *
     * @param context
     */
    public static String getString2(Activity context) {
        return context.getIntent().getStringExtra(TYPE_STRING2);
    }

}

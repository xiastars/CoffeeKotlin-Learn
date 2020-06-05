package com.summer.helper.utils;

import android.content.Context;

import com.tencent.stat.StatService;

import java.util.HashMap;
import java.util.Properties;

/**
 * 腾讯MTA埋点统计
 * Created by xiaqiliang on 2017/5/15.
 */

public class CUtils {

    public static HashMap<String, String> getMapWithId(String id) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", id);
        return map;
    }

    public static HashMap<String, String> getMapWithId(String id, String userID) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("object_id", id);
        map.put("userId", userID);
        return map;
    }

    /**
     * 数据埋点，TAG+ID
     *
     * @param tag
     */
    public static void onClick(Context context, final String tag) {
        onClick(context, tag, 0);
    }

    /**
     * 数据埋点，TAG+ID
     *
     * @param tag
     */
    public static void onClick(Context context, final String tag, String markKey, String markValue) {
        onClickFinal(context, tag, markKey, markValue);
    }

    /**
     * 数据埋点，TAG+ID
     *
     * @param tag
     * @param id
     */
    public static void onClick(Context context, final String tag, final long id) {
        onClickFinal(context, tag, "click_id", id + "");
    }

    /**
     * 数据埋点，TAG+ID
     *
     * @param tag
     * @param id
     */
    public static void onClick(Context context, final String tag, final String id) {
        onClickFinal(context, tag, "click_id", id + "");
    }

    public static void onClickFinal(final Context context, final String tag, final String mark, final String id) {

        SThread.getIntances().submit(new Runnable() {
            @Override
            public void run() {
                Properties prop = new Properties();
                if (id != null && !id.equals("0")) {
                    prop.setProperty(mark, id);
                }
                StatService.trackCustomKVEvent(context, tag, prop);
            }
        });
    }
}

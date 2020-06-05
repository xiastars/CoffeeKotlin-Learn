package com.summer.demo.utils

import android.content.Context
import com.summer.helper.utils.SThread
import java.util.*

/**
 * 埋点帮助类,示例用的是腾讯的
 * 在这里可以改成别的平台
 * 埋点最禁在单个页面写，因为如果有切换平台的需求就完了
 * Created by xiaqiliang on 2017/5/15.
 */

object CUtils {

    fun getMapWithId(id: String): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["object_id"] = id
        return map
    }

    fun getMapWithId(id: String, userID: String): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["object_id"] = id
        map["userId"] = userID
        return map
    }

    /**
     * 数据埋点，TAG+ID
     *
     * @param tag
     */
    fun onClick(context: Context, tag: String, markKey: String, markValue: String) {
        onClickFinal(context, tag, markKey, markValue)
    }

    /**
     * 数据埋点，TAG+ID
     *
     * @param tag
     * @param id
     */
    @JvmOverloads
    fun onClick(context: Context, tag: String, id: Long = 0) {
        onClickFinal(context, tag, "click_id", id.toString() + "")
    }

    /**
     * 数据埋点，TAG+ID
     *
     * @param tag
     * @param id
     */
    fun onClick(context: Context, tag: String, id: String) {
        onClickFinal(context, tag, "click_id", id + "")
    }

    fun onClickFinal(context: Context, tag: String, mark: String, id: String) {

        SThread.getIntances().submit {
            /*      Properties prop = new Properties();
                if (id != null && !id.equals("0")) {
                    prop.setProperty(mark, id);
                }
                StatService.trackCustomKVEvent(context, tag, prop);*/
        }
    }
}
/**
 * 数据埋点，TAG+ID
 *
 * @param tag
 */

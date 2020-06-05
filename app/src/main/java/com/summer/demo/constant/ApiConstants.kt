package com.summer.demo.constant


import com.summer.demo.AppContext

object ApiConstants {

    var FSXQ_DEV = "dev"

    var baseHost = ""

    var FSXQ_MOVIE_HOST = ""

    var FSXQ_IMG_HOST = ""


    val API_VERSION_1 = "/v1.0/"
    /**
     * 获取对应的host
     *
     * @return host
     */
    val hostVersion2 = "/v1.2/"

    var WEB_URL = ""
    var SHARE_URL = ""

    /**
     * 七牛云的图片拼接前缀
     */
    val FILE_BASE_PATH = ""

    private val FSXQ_SHARE_GROUP_PATH = "/dist/star/index"
    private val FSXQ_SHARE_TOPIC_PATH = "/dist/star/post_detail"


    /**
     * 获取对应的host
     *
     * @return host
     */
    val host: String
        get() = getHost(API_VERSION_1)

    /**
     * 分享有奖协议
     */
    var SHARE_REWRD_PROTOCAL = withWeb("fensixingqiu.com/dist/user/rule?type=share_award_agreement")

    /**
     * 分享有奖规则
     */
    var SHARE_REWRD_RULE = withWeb("fensixingqiu.com/dist/user/rule?type=share_award_rule")


    init {
        var HTTPTYPE: String? = null
        if (AppContext.SERVER_MODE == 0) {
            FSXQ_DEV = "dev"
            HTTPTYPE = "http:"
            WEB_URL = "http://web."
            SHARE_URL = "http://web.fensixingqiu.com"
        } else if (AppContext.SERVER_MODE == 1) {
            FSXQ_DEV = "testa"
            HTTPTYPE = "http:"
            WEB_URL = "http://web."
            SHARE_URL = "https://testw.fensixingqiu.com"
        } else if (AppContext.SERVER_MODE == 2) {
            FSXQ_DEV = "api"
            HTTPTYPE = "https:"
            WEB_URL = "https://w."
            SHARE_URL = "https://w.fensixingqiu.com"
        } else if (AppContext.SERVER_MODE == 3) {
            FSXQ_DEV = "prea"
            HTTPTYPE = "https:"
            WEB_URL = "https://prew2."
            SHARE_URL = "https://prew2.fensixingqiu.com"
        }
        baseHost = "$HTTPTYPE//$FSXQ_DEV.fensixingqiu.com"
        FSXQ_MOVIE_HOST = "https://$FSXQ_DEV.fensixingqiu.com"
        FSXQ_IMG_HOST = "https://$FSXQ_DEV.fensixingqiu.com"
    }


    fun shareFsxqUrl(type: Int): String {
        val buffer = StringBuffer(baseHost)
        if (type == 1) {
            buffer.append(FSXQ_SHARE_GROUP_PATH)
        } else {
            buffer.append(FSXQ_SHARE_TOPIC_PATH)
        }

        return buffer.toString()
    }

    /**
     * 获取对应的host
     *
     * @return host
     */
    fun getHost(version: String?): String {
        val host = baseHost
        var apiVersion = API_VERSION_1
        if (version != null) {
            apiVersion = version
        }
        return host + apiVersion
    }

    fun withWeb(url: String): String {
        return WEB_URL + url
    }

    fun withShare(url: String): String {
        return "$SHARE_URL/$url"
    }

    fun withHost(url: String): String {
        return baseHost + url
    }
}

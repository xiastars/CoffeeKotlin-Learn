package com.summer.helper.server;

/**
 * Created by xiastars on 2017/7/13.
 */

public class ErrorCode {

    /**
     * 请求超时
     */
    public static int ERR_TIMEOUT = -1001;

    /**
     * 无效的JSON
     */
    public static int INVALID_JSON = -1002;

    /**
     * 其它错误
     */
    public static int ERR_OTHER = -1003;

    /**
     * 网络未连接
     */
    public static int ERR_CONENCCT = -1004;

    /**
     * 内存不足
     */
    public static int ERR_LOWMEMORY = -1005;


}

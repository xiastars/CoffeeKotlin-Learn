package com.summer.helper.server;

/**
 * 发起访问请求时所需的回调接口。
 */

public interface RequestListener {

    void onComplete(String response);

    void onErrorException(SummerException e);
}

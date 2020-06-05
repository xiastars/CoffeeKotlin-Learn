package com.summer.demo.ui.module.fragment.socket

/**
 * @Description: socket返回内容监听
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/31 17:38
 */
interface SocketResponseListener {
    fun response(type: Int, status: Int)
}

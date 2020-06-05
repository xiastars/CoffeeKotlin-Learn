package com.summer.demo.ui.module.comment

import com.summer.demo.bean.UserInfo

import java.io.Serializable

class ChatInfo : Serializable {
    var id: String = ""
    var type: Int = 0
    var text: String = ""
    var send_time: Long = 0
    lateinit var user: UserInfo
    var requestCode: Int = 0
    var isSendError: Boolean = false
    var isShowTime: Boolean = false
}

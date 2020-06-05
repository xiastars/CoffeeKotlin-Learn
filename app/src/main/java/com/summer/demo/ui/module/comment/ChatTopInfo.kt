package com.summer.demo.ui.module.comment

import com.summer.demo.bean.UserInfo

import java.io.Serializable

class ChatTopInfo : Serializable {
    var total: Int = 0
    var unread: Int = 0
    lateinit var list: List<ChatInfo>
    lateinit var receiver: UserInfo
    lateinit var sender: UserInfo
}
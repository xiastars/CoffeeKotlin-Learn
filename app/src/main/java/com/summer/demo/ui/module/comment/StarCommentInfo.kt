package com.summer.demo.ui.module.comment

import java.io.Serializable

/**
 * Created by xiaqiliang on 2017/3/28.
 */
class StarCommentInfo : Serializable {

    lateinit var userHeadImg: String
    var createdTime: Long = 0
    var id: Long = 0
    var userId: Long = 0
    lateinit var content: String
    var level: Int = 0
}

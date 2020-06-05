package com.summer.demo.module.emoji

import android.graphics.Bitmap

/**
 * Created by xiaqiliang on 2017/9/7.
 */
class IconEntity {
    var id = 0
    var key: String? = null
    var res = 0
    var version //表情版本号
            : String? = null
    var packageID = 0 //表情包名 = 0
    var emojiPath //取得表情的路径
            : String? = null
    var emojiText //取到EditText的文本
            : String? = null
    var name //中文对应名称
            : String? = null
    var isEmojiPic = false //是否是贴图，默认不是
    var bitmap //贴图
            : Bitmap? = null

    constructor() : super() {}
    constructor(name: String?, res: Int) : super() {
        this.res = res
        this.name = name
    }

    constructor(id: Int, name: String?, emojiPath: String?, version: String?, packageID: Int) : super() {
        this.emojiPath = emojiPath
        this.name = name
        this.version = version
        this.packageID = packageID
    }

    constructor(id: Int, key: String?, res: Int) : super() {
        this.id = id
        this.key = key
        this.res = res
    }

}
package com.summer.demo.module.album.util

import java.io.Serializable


class ImageItem : Serializable {
    lateinit var imageId: String
    lateinit var thumbnailPath: String
    lateinit var imagePath :String
    var isSelected = false
    var videoPath: String? = null
    var duration: Int = 0
    var id: Long = 0
    var img: String? = null

}

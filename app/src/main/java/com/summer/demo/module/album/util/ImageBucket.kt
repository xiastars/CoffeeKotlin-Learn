package com.summer.demo.module.album.util

import java.io.Serializable

class ImageBucket : Serializable {
    var count = 0
    var bucketName: String? = null
    var imageList: MutableList<ImageItem>? = null

}

package com.summer.demo.module.album.bean

import com.summer.demo.module.album.listener.AlbumCallback

import java.util.ArrayList
import java.util.Arrays

class SelectOptions private constructor() {
    var isCrop: Boolean = false
        private set
    var cropWidth: Int = 0
        private set
    var cropHeight: Int = 0
        private set
    var callback: AlbumCallback? = null
        private set
    var isHasCam: Boolean = false
        private set
    var selectCount: Int = 0
        private set
    var selectedImages: List<String>? = null
        private set
    var isVideoMode: Boolean = false
        private set

    class Builder {
        private var isCrop: Boolean = false
        private var cropWidth: Int = 0
        private var cropHeight: Int = 0
        private var callback: AlbumCallback? = null
        private var hasCam: Boolean = false
        private var isVideoMode: Boolean = false
        private var selectCount: Int = 0
        private var selectedImages: MutableList<String>? = null

        init {
            selectCount = 1
            hasCam = true
            selectedImages = ArrayList()
        }

        fun setCrop(cropWidth: Int, cropHeight: Int): Builder {
            if (cropWidth <= 0 || cropHeight <= 0)
                throw IllegalArgumentException("cropWidth or cropHeight mast be greater than 0 ")
            this.isCrop = true
            this.cropWidth = cropWidth
            this.cropHeight = cropHeight
            return this
        }

        fun setCallback(callback: AlbumCallback): Builder {
            this.callback = callback
            return this
        }

        fun setVideoMode(isVideo: Boolean): Builder {
            this.isVideoMode = isVideo
            return this
        }

        fun isVideoMode(): Boolean {
            return isVideoMode
        }

        fun setHasCam(hasCam: Boolean): Builder {
            this.hasCam = hasCam
            return this
        }

        fun setSelectCount(selectCount: Int): Builder {
            if (selectCount <= 0)
                throw IllegalArgumentException("selectCount mast be greater than 0 ")
            this.selectCount = selectCount
            return this
        }

        fun setSelectedImages(selectedImages: List<String>?): Builder {
            if (selectedImages == null || selectedImages.size == 0) return this
            this.selectedImages!!.addAll(selectedImages)
            return this
        }

        fun setSelectedImages(selectedImages: Array<String>?): Builder {
            if (selectedImages == null || selectedImages.size == 0) return this
            if (this.selectedImages == null) this.selectedImages = ArrayList()
            this.selectedImages!!.addAll(Arrays.asList(*selectedImages))
            return this
        }

        fun build(): SelectOptions {
            val options = SelectOptions()
            options.isHasCam = hasCam
            options.isCrop = isCrop
            options.isVideoMode = isVideoMode
            options.cropHeight = cropHeight
            options.cropWidth = cropWidth
            options.callback = callback
            options.selectCount = selectCount
            options.selectedImages = selectedImages
            return options
        }
    }


}

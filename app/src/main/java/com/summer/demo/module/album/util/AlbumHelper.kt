package com.summer.demo.module.album.util

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore.Images.Media
import android.provider.MediaStore.Images.Thumbnails
import com.summer.helper.utils.Logs
import java.util.*

class AlbumHelper private constructor() {
    internal val TAG = javaClass.simpleName
    internal var context: Context? = null
    internal lateinit var cr: ContentResolver

    internal var thumbnailList = HashMap<String, String>()

    internal var bucketList = HashMap<String, ImageBucket>()

    internal var hasBuildImagesBucketList = false

    fun init(context: Context) {
        if (this.context == null) {
            this.context = context
            cr = context.contentResolver
        }
    }

    private fun getThumbnail() {
        val projection = arrayOf(Thumbnails._ID, Thumbnails.IMAGE_ID, Thumbnails.DATA)
        val cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null)
        getThumbnailColumnData(cursor!!)
    }

    private fun getThumbnailColumnData(cur: Cursor) {
        if (cur.moveToFirst()) {
            var _id: Int
            var image_id: Int
            var image_path: String
            val _idColumn = cur.getColumnIndex(Thumbnails._ID)
            val image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID)
            val dataColumn = cur.getColumnIndex(Thumbnails.DATA)

            do {
                // Get the field values
                _id = cur.getInt(_idColumn)
                image_id = cur.getInt(image_idColumn)
                image_path = cur.getString(dataColumn)
                thumbnailList["" + image_id] = image_path
            } while (cur.moveToNext())
        }
    }

    internal fun buildImagesBucketList() {
        val startTime = System.currentTimeMillis()
        getThumbnail()

        Logs.i("xia", "time:" + (System.currentTimeMillis() - startTime))

        val columns = arrayOf(Media._ID, Media.BUCKET_ID, Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME, Media.TITLE, Media.SIZE, Media.BUCKET_DISPLAY_NAME)
        val cur = cr.query(Media.EXTERNAL_CONTENT_URI, columns, IS_LARGE_SIZE + BUCKET_GROUP_BY,
                arrayOf("0"), Media._ID + " desc")
        if (cur != null && cur.moveToFirst()) {
            val photoIDIndex = cur.getColumnIndexOrThrow(Media._ID)
            val photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA)
            val photoNameIndex = cur.getColumnIndexOrThrow(Media.DISPLAY_NAME)
            val photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE)
            val photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE)
            val bucketDisplayNameIndex = cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME)
            val bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID)
            val picasaIdIndex = cur.getColumnIndexOrThrow(Media.PICASA_ID)
            val totalNum = cur.count

            do {
                val _id = cur.getString(photoIDIndex)
                val name = cur.getString(photoNameIndex)
                val path = cur.getString(photoPathIndex)
                val title = cur.getString(photoTitleIndex)
                val size = cur.getString(photoSizeIndex)
                val bucketName = cur.getString(bucketDisplayNameIndex)
                val bucketId = cur.getString(bucketIdIndex)
                val picasaId = cur.getString(picasaIdIndex)

                /*                Logs.i("xia", _id + ", bucketId: " + bucketId + ", picasaId: "
                        + picasaId + " name:" + name + " path:" + path
                        + " title: " + title + " size: " + size + " bucket: "
                        + bucketName + "---");*/

                var bucket = bucketList[bucketId]
                if (bucket == null) {
                    bucket = ImageBucket()
                    bucketList[bucketId] = bucket
                    bucket.imageList = ArrayList()
                    bucket.bucketName = bucketName
                }
                bucket.count++
                val imageItem = ImageItem()
                imageItem.imageId = _id
                imageItem.imagePath = path
                imageItem.thumbnailPath = thumbnailList[_id]!!
                if (imageItem.thumbnailPath == null) {
                    imageItem.thumbnailPath = imageItem.imagePath!!
                }
                bucket.imageList!!.add(imageItem)

            } while (cur.moveToNext())
        }

        hasBuildImagesBucketList = true
    }


    fun getImagesBucketList(refresh: Boolean): List<ImageBucket> {
        if (refresh || !hasBuildImagesBucketList) {
            buildImagesBucketList()
        }
        val tmpList = ArrayList<ImageBucket>()
        val itr = bucketList.entries.iterator()
        while (itr.hasNext()) {
            val entry = itr.next()
            tmpList.add(entry.value)
        }
        return tmpList
    }

    companion object {
        var MAX_SELECT = 0

        private var instance: AlbumHelper? = null

        val helper: AlbumHelper
            get() {
                if (instance == null) {
                    instance = AlbumHelper()
                }
                return instance!!
            }
        private val IS_LARGE_SIZE = " _size > ? or _size is null"
        private val BUCKET_GROUP_BY = ") GROUP BY  1,(2"
    }

}

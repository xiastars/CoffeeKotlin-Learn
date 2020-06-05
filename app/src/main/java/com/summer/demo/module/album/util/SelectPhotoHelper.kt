package com.summer.demo.module.album.util

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.summer.demo.module.album.AlbumActivity
import com.summer.demo.module.album.bean.SelectOptions
import com.summer.demo.module.album.listener.AlbumCallback
import com.summer.helper.listener.OnResponseListener
import com.summer.helper.permission.PermissionUtils
import com.summer.helper.server.PostData
import com.summer.helper.utils.JumpTo
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SFileUtils
import java.io.File
import java.util.*

/**
 * Created by xiastars on 2017/10/23.
 */

class SelectPhotoHelper(internal var context: Context, internal var listener: OnResponseListener?) {

    internal lateinit var fileName: String
    internal lateinit var imgFile: File
    internal lateinit var cropedPath: String//裁剪后的
    internal var FROME_ALBUM = 1002
    internal val CROP_IMG = 1003

    internal var aspectY = 1
    internal var aspectX = 1
    internal lateinit var imageView: ImageView
    internal lateinit var targetView: ImageView
    internal var activity: Activity


    internal var croped: Boolean = false

    internal var options: BitmapFactory.Options? = null

    init {
        activity = context as Activity

    }

    fun setAspectY(x: Int, y: Int) {
        aspectX = x
        aspectY = y
    }

    fun setTargetView(imageView: ImageView, mark: String) {
        this.imageView = imageView
        targetView = imageView
        imageView.setOnClickListener(View.OnClickListener {
            //CUtils.onClick(mark);
            if (!PermissionUtils.checkReadPermission(context)) {
                return@OnClickListener
            }
            showSelectPhotoDialog()
        })
    }

    fun setTargetView(imageView: ImageView, targetView: ImageView, mark: String) {
        this.imageView = imageView
        this.targetView = targetView
        context = imageView.context
        imageView.setOnClickListener(View.OnClickListener {
            //CUtils.onClick(mark);
            //enterToAlbum();
            if (!PermissionUtils.checkReadPermission(context)) {
                return@OnClickListener
            }
            val builder = SelectOptions.Builder()
            val callback = object : AlbumCallback{
                override fun doSelected(images: Array<String>) {
                    if (images != null && images.size > 0) {
                        val imgPaath = images[0]
                        if (croped) {
                            imgFile = File(imgPaath)
                            cropImage()
                            return
                        }

                        if (listener != null) {
                            listener!!.succeed(imgPaath)
                        }
                    }
                }
            }
            builder.setCallback(callback)
            AlbumActivity.show(context, builder.build())
        })
    }

    fun startSelectPhoto() {
        val builder = SelectOptions.Builder()
        val callback = object : AlbumCallback{
            override fun doSelected(images: Array<String>) {
                if (images != null && images.size > 0) {
                    val imgPaath = images[0]
                    if (croped) {
                        imgFile = File(imgPaath)
                        cropImage()
                        return
                    }

                    if (listener != null) {
                        listener!!.succeed(imgPaath)
                    }
                }
            }
        }
        builder.setCallback(callback)
        AlbumActivity.show(context, builder.build())
    }

    fun startTakePhoto() {
        fileName = "tmy_" + System.currentTimeMillis() + ".png"
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)//拍照
        imgFile = File(SFileUtils.getAvatarDirectory() + fileName)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile))
        activity.startActivityForResult(intent, FROME_CAMERA)
    }

    fun setTargetViewDisableTouch(imageView: ImageView, mark: String) {
        this.imageView = imageView
        targetView = imageView
    }

    /**
     * 选择图片
     */
    fun showSelectPhotoDialog() {
        /*
        final BottomListDialog selectTypeDialog = new BottomListDialog(context);
        String[] titles = {"拍照", "从手机相册选择"};
        selectTypeDialog.setDatas(titles);
        selectTypeDialog.setStringType();
        selectTypeDialog.showTopContent(View.GONE);
        selectTypeDialog.showBottomContent(View.GONE);
        selectTypeDialog.show();
        selectTypeDialog.setListener(new OnSimpleClickListener() {
            @Override
            public void onClick(int position) {
                fileName = "nf_" + System.currentTimeMillis() + ".png";
                if (position == 0) {
                    if (!PermissionUtils.checkCameraPermission(context)) {
                        return;
                    }
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//拍照
                    imgFile = new File(SFileUtils.getAvatarDirectory() + fileName);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile));
                    activity.startActivityForResult(intent, FROME_CAMERA);
                } else if (position == 1) {
                    enterToAlbum();
                }
                selectTypeDialog.cancelDialog();
            }
        });*/
    }

    /**
     * 进行摄像头
     */
    fun enterToCamera() {
        fileName = "ec_" + System.currentTimeMillis() + ".png"
        if (!PermissionUtils.checkCameraPermission(context)) {
            return
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)//拍照
        imgFile = File(SFileUtils.getAvatarDirectory() + fileName)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile))
        activity.startActivityForResult(intent, FROME_CAMERA)
    }

    fun enterToAlbum() {
        if (!PermissionUtils.checkReadPermission(context)) {
            return
        }
        val builder = SelectOptions.Builder()
        val callback = object : AlbumCallback{
            override fun doSelected(images: Array<String>) {
                if (images != null && images.size > 0) {
                    val imgPaath = images[0]
                    Logs.i("file::$imgPaath")
                    if (croped) {
                        imgFile = File(imgPaath)
                        cropImage()
                        return
                    }

                    if (listener != null) {
                        listener!!.succeed(imgPaath)
                    }
                }
            }
        }
        builder.setCallback(callback)
        builder.setSelectCount(1)
        AlbumActivity.show(context, builder.build())
    }

    fun handleRequestCode(requestCode: Int, data: Intent) {
        when (requestCode) {
            FROME_CAMERA -> {
                val path = imgFile.path
                Logs.i("..$path")

                if (croped) {
                    cropImage()
                } else {
                    if (listener != null) {
                        listener!!.succeed(path)
                    }
                }
            }
            CROP_IMG -> {
                if (PostData.MODEL == "Meizu") {
                    val imageData = data.getParcelableExtra<Parcelable>("data")
                    if (imageData == null) {
                        cropedPath = data.getStringExtra("filePath")
                        if (!TextUtils.isEmpty(cropedPath)) {
                            // SUtils.setPicWithHolder(targetView, cropedPath, R.drawable.ic_defaul_img);
                        }
                    }
                } else {
                    //SUtils.setPicWithHolder(targetView, cropedPath, R.drawable.ic_defaul_img);
                }
                Logs.i("fielP_ath$cropedPath")
                if (listener != null) {
                    listener!!.succeed(cropedPath)
                }
            }
            AlbumActivity.REQUEST_CODE -> {
                val arrayList = data.getSerializableExtra(JumpTo.TYPE_OBJECT) as ArrayList<ImageItem>
                if (arrayList != null && arrayList.size > 0) {
                    val imageItem = arrayList[0]
                    if (croped) {
                        imgFile = File(imageItem.imagePath!!)
                        cropImage()
                        return
                    }

                    Logs.i(".." + imageItem.imagePath!!)
                    if (listener != null) {
                        listener!!.succeed(imageItem.imagePath)
                    }
                }
            }
        }


    }

    private fun cropImage() {
        if (options == null) {
            options = BitmapFactory.Options()
        }
        val uri = getImageContentUri(activity, imgFile)
        if (uri != null) {
            cropedPath = SFileUtils.getAvatarDirectory() + "nf_" + System.currentTimeMillis() + "_croped.png"
            val intent = Intent("com.android.camera.action.CROP")
            intent.setDataAndType(uri, "image/*")
            intent.putExtra("crop", "true")
            intent.putExtra("aspectX", aspectX)
            intent.putExtra("aspectY", aspectY)
            intent.putExtra("noFaceDetection", true)
            intent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(File(cropedPath)))
            (context as Activity).startActivityForResult(Intent.createChooser(intent, "裁剪图片"), CROP_IMG)
        }
    }

    fun filePath(): String {
        return imgFile.path
    }

    fun setCroped(croped: Boolean) {
        this.croped = croped
    }

    interface OnReturnImgListener {
        fun returnImg(path: String)
    }

    companion object {
        val FROME_CAMERA = 1001

        /**
         * 将URI转为图片的路径
         *
         * @param context
         * @param uri
         * @return
         */
        fun getRealFilePath(context: Context, uri: Uri?): String? {
            if (null == uri)
                return null
            val scheme = uri.scheme
            var data: String? = null
            if (scheme == null)
                data = uri.path
            else if (ContentResolver.SCHEME_FILE == scheme) {
                data = uri.path
            } else if (ContentResolver.SCHEME_CONTENT == scheme) {
                val cursor = context.contentResolver.query(uri,
                        arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                        if (index > -1) {
                            data = cursor.getString(index)
                        }
                    }
                    cursor.close()
                }
            }
            return data
        }

        /**
         * 包成file文件转为URI
         */

        fun getImageContentUri(context: Context, imageFile: File): Uri? {
            val filePath = imageFile.absolutePath
            val cursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Images.Media._ID),
                    MediaStore.Images.Media.DATA + "=? ",
                    arrayOf(filePath), null)

            if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.MediaColumns._ID))
                val baseUri = Uri.parse("content://media/external/images/media")
                return Uri.withAppendedPath(baseUri, "" + id)
            } else {
                if (imageFile.exists()) {
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.DATA, filePath)
                    return context.contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                } else {
                    return null
                }
            }
        }
    }
}

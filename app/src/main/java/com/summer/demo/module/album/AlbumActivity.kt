package com.summer.demo.module.album

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.summer.demo.R
import com.summer.demo.module.album.adapter.AlbumGridViewAdapter
import com.summer.demo.module.album.adapter.FolersAdapter
import com.summer.demo.module.album.bean.SelectOptions
import com.summer.demo.module.album.util.AlbumHelper
import com.summer.demo.module.album.util.ImageBucket
import com.summer.demo.module.album.util.ImageItem
import com.summer.demo.module.album.util.PublicWay
import com.summer.demo.module.base.BaseActivity
import com.summer.demo.view.CommonSureView5
import com.summer.helper.utils.*
import com.summer.helper.view.NRecycleView
import java.io.File
import java.util.*

/**
 * 这个是进入相册显示所有图片的界面
 *
 * @author xiastars
 */
class AlbumActivity : BaseActivity(), OnClickListener {
    @BindView(R.id.myGrid)
    internal var myGrid: NRecycleView? = null
    @BindView(R.id.nv_list)
    internal var nvList: NRecycleView? = null
    @BindView(R.id.rl_list_cover)
    internal var rlListCover: RelativeLayout? = null
    @BindView(R.id.preview)
    internal lateinit var preview: Button
    @BindView(R.id.ok_button)
    internal  lateinit var okButton: Button
    @BindView(R.id.bottom_layout)
    internal var bottomLayout: RelativeLayout? = null
    @BindView(R.id.tv_finish)
    internal lateinit var tvFinish: TextView
    //显示手机里的所有图片的列表控件
    private var gridView: NRecycleView? = null
    //gridView的adapter
    private var gridImageAdapter: AlbumGridViewAdapter? = null
    private var dataList: ArrayList<ImageItem>? = null
    private var helper: AlbumHelper? = null
    internal var tempSelectBitmap: ArrayList<ImageItem>? = ArrayList()

    internal var showVideo = false//是否为纯视频

    internal var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            gridImageAdapter!!.notifyDataSetChanged()

        }
    }


    /**
     * 完成
     */
    fun onSelectItem() {
        if (tempSelectBitmap != null) {
            if (showVideo) {
                val intent = Intent()
                intent.putExtra(JumpTo.TYPE_OBJECT, tempSelectBitmap)
                setResult(Activity.RESULT_OK, intent)
                if (mOption != null) {
                   // mOption!!.callback!!.doSelected(toArray(tempSelectBitmap)!!)
                }
            } else {
                val intent = Intent()
                intent.putExtra(JumpTo.TYPE_OBJECT, tempSelectBitmap)
                setResult(Activity.RESULT_OK, intent)
                if (mOption != null) {
                   // mOption!!.callback!!.doSelected(toArray(tempSelectBitmap)!!)
                }
            }
        }
        for (i in PublicWay.activityList.indices) {
            if (null != PublicWay.activityList[i]) {
                PublicWay.activityList[i].finish()
            }
        }
    }

    @OnClick(R.id.ok_button)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.ok_button -> onSelectItem()
        }
    }

    // 返回按钮监听
    private inner class BackListener : OnClickListener {
        override fun onClick(v: View) {
            if (tempSelectBitmap!!.size > 0) {
                intent!!.putExtra("position", "1")
                intent!!.putExtra(JumpTo.TYPE_OBJECT, tempSelectBitmap)
                intent!!.setClass(this@AlbumActivity, GalleryActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // 初始化，给一些对象赋值
    private fun init() {
        setTitle("最近照片")
        val btnAlbum = findViewById(R.id.btn_right) as CommonSureView5
        btnAlbum.setOnClickListener(BackListener())
        btnAlbum.visibility = View.VISIBLE
        btnAlbum.text = "预览"
        preview = findViewById(R.id.preview) as Button
        preview.setOnClickListener {
            Logs.i(nvList!!.visibility.toString() + ",,,,")
            if (rlListCover!!.visibility == View.VISIBLE) {
                hideFolderView()
            } else {
                rlListCover!!.visibility = View.VISIBLE
                val anim = AnimationUtils.loadAnimation(context,
                        R.anim.slide_up)
                nvList!!.startAnimation(anim)
            }
        }
        intent = getIntent()
        gridView = findViewById(R.id.myGrid) as NRecycleView
        gridView!!.setGridView(3)
        tvFinish = findViewById(R.id.tv_finish) as TextView
        okButton = findViewById(R.id.ok_button) as Button
        okButton.text = getString(R.string.finish) + "(" + tempSelectBitmap!!.size + "/" + PublicWay.MAX_SELECT_COUNT + ")"
        if (showVideo) {
            preview.visibility = View.GONE
            setTitle("最近视频")
            btnAlbum.visibility = View.GONE
            SThread.getIntances().submit { getAllVideoInfos() }
            return
        }
        nvList!!.setList()
        val folderAdapter = FolersAdapter(context!!, tempSelectBitmap!!)
        nvList!!.adapter = folderAdapter


        helper = AlbumHelper.helper
        helper!!.init(this)
        contentList = helper!!.getImagesBucketList(false)
        folderAdapter.notifyDataChanged(contentList)
        dataList = ArrayList()
        gridImageAdapter = AlbumGridViewAdapter(this, dataList, tempSelectBitmap!!)
        gridView!!.adapter = gridImageAdapter
        val contentSize = contentList.size
        for (i in 0 until contentSize) {
            dataList!!.addAll(contentList[i].imageList!!)
            gridImageAdapter!!.notifyDataSetChanged()
        }
        val comparator = Comparator<ImageItem> { s1, s2 ->
            //根据图片的ID进行判断
            if (Integer.parseInt(s1.imageId) != Integer.parseInt(s2.imageId)) {
                Integer.parseInt(s2.imageId) - Integer.parseInt(s1.imageId)
            } else 0
        }
        //这里就会自动根据规则进行排序
        Collections.sort(dataList!!, comparator)
        try {
            val isDraft = this.getIntent().extras!!.getBoolean("isDraft")
            if (isDraft) {
                //过滤之前有保存的稿子图片
                for (i in tempSelectBitmap!!.indices) {
                    for (j in dataList!!.indices) {
                        val imageId = tempSelectBitmap!![i].imageId
                        val item = dataList!![j]
                        if (item.imageId == imageId) {
                            tempSelectBitmap!![i] = item
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
        }

        if (tempSelectBitmap != null) {
            val datas = ArrayList<ImageItem>()
            for (i in tempSelectBitmap!!.indices) {
                for (j in dataList!!.indices) {
                    val imageId = tempSelectBitmap!![i].imageId
                    val item = dataList!![j]
                    if (item.imageId == imageId) {
                        item.isSelected = true
                        datas.add(item)
                        break
                    }
                }
            }
            tempSelectBitmap!!.clear()
            tempSelectBitmap!!.addAll(datas)
        }
        gridImageAdapter!!.notifyDataSetChanged()

        initListener()
    }

    fun hideFolderView() {
        val anim = AnimationUtils.loadAnimation(context,
                R.anim.slide_bottom)
        rlListCover!!.startAnimation(anim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                rlListCover!!.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }

    /**
     * 获取手机中所有视频的信息
     */
    private fun getAllVideoInfos() {
        dataList = ArrayList()
        val mImageUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val proj = arrayOf(MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATE_MODIFIED)
        val mCursor = contentResolver.query(mImageUri,
                proj,
                MediaStore.Video.Media.MIME_TYPE + "=?",
                arrayOf("video/mp4"),
                MediaStore.Video.Media.DATE_MODIFIED + " desc")
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                // 获取视频的路径
                val time = System.currentTimeMillis()
                val videoId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID))
                val path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA))
                val duration = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                //提前生成缩略图，再获取：http://stackoverflow.com/questions/27903264/how-to-get-the-video-thumbnail-path-and-not-the-bitmap
                val thumbPath = getVideoThumbPath(videoId)
                // 获取该视频的父路径名
                val dirPath = File(path).parentFile.absolutePath
                val item = ImageItem()
                item.duration = duration
                item.videoPath = path
                Logs.i("xia", "$dirPath,,$path,,,$thumbPath")
                if (TextUtils.isEmpty(thumbPath)) {

                }
                item.imagePath = thumbPath
                dataList!!.add(item)
                if (dataList!!.size % 3 == 0) {
                    notifyAdapter()
                }
            }
            mCursor.close()
        }
        notifyAdapter()
    }

    private fun getVideoThumbPath(videoId: Int): String {
        var thumbPath = cursorVideoThumbPath(videoId)
        if (TextUtils.isEmpty(thumbPath)) {
            MediaStore.Video.Thumbnails.getThumbnail(contentResolver, videoId.toLong(), MediaStore.Video.Thumbnails.MICRO_KIND, null)
            thumbPath = cursorVideoThumbPath(videoId)
        }
        return thumbPath
    }

    private fun cursorVideoThumbPath(videoId: Int): String {
        val projection = arrayOf(MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA)
        val cursor = contentResolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, projection, MediaStore.Video.Thumbnails.VIDEO_ID + "=?", arrayOf(videoId.toString() + ""), null)
        var thumbPath = ""
        if (cursor != null) {
            while (cursor.moveToNext()) {
                thumbPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA))
            }
            cursor.close()
        }
        return thumbPath
    }

    private fun notifyAdapter() {
        //更新界面
        runOnUiThread {
            if (gridImageAdapter == null) {
                gridImageAdapter = AlbumGridViewAdapter(this@AlbumActivity, dataList, tempSelectBitmap!!)
                gridView!!.adapter = gridImageAdapter
                initListener()
            } else {
                gridImageAdapter!!.notifyDatas(dataList!!)
            }
        }
    }

    private fun initListener() {
        var listener = object : AlbumGridViewAdapter.OnItemClickListener{
            override fun onItemClick(view: ToggleButton, position: Int, isChecked: Boolean, chooseBt: ImageView) {
                if (tempSelectBitmap!!.size >= PublicWay.MAX_SELECT_COUNT) {
                    view.isChecked = false
                    chooseBt.visibility = View.GONE
                    if (!removeOneData(dataList!![position])) {
                        if (PublicWay.MAX_SELECT_COUNT == 1) {
                            tempSelectBitmap!!.clear()
                            gridImageAdapter!!.notifyDataSetChanged()
                            view.isChecked = true
                        } else {
                            Toast.makeText(this@AlbumActivity, getString(R.string.only_choose_num), Toast.LENGTH_SHORT).show()
                            return
                        }

                    }
                }
                if (isChecked) {//如果选中
                    chooseBt.visibility = View.VISIBLE
                    tempSelectBitmap!!.add(dataList!![position])
                    okButton.text = getString(R.string.finish) + "(" + tempSelectBitmap!!.size + "/" + PublicWay.MAX_SELECT_COUNT + ")"
                } else {
                    dataList!![position].isSelected = false
                    tempSelectBitmap!!.remove(dataList!![position])
                    chooseBt.visibility = View.GONE
                    okButton.text = getString(R.string.finish) + "(" + tempSelectBitmap!!.size + "/" + PublicWay.MAX_SELECT_COUNT + ")"
                }
                isShowOkBt()
            }
        }
        gridImageAdapter!!.setOnItemClickListener(listener)
    }

    private fun removeOneData(imageItem: ImageItem): Boolean {
        if (tempSelectBitmap!!.contains(imageItem)) {
            tempSelectBitmap!!.remove(imageItem)
            okButton.text = getString(R.string.finish) + "(" + tempSelectBitmap!!.size + "/" + PublicWay.MAX_SELECT_COUNT + ")"
            return true
        }
        return false
    }

    fun isShowOkBt() {
        if (tempSelectBitmap!!.size > 0) {
            okButton.text = getString(R.string.finish) + "(" + tempSelectBitmap!!.size + "/" + PublicWay.MAX_SELECT_COUNT + ")"
            preview.isPressed = true
            okButton.isPressed = true
            preview.isClickable = true
            okButton.isClickable = true
            okButton.setTextColor(Color.parseColor("#FFFFFF"))
            preview.setTextColor(Color.WHITE)
        } else {
            okButton.text = getString(R.string.finish) + "(" + tempSelectBitmap!!.size + "/" + PublicWay.MAX_SELECT_COUNT + ")"
            preview.isPressed = true
            preview.isClickable = true
            okButton.isPressed = false
            okButton.isClickable = false
            okButton.setTextColor(Color.parseColor("#FFFFFF"))
            preview.setTextColor(Color.parseColor("#E1E0DE"))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //如果是相册列表跳过来的
        if (resultCode == 12) {

            var intent = Intent()
            val datas = data!!.getSerializableExtra(JumpTo.TYPE_OBJECT) as ArrayList<ImageItem>
            if (datas != null) {
                //tempSelectBitmap.addAll(datas);
                tempSelectBitmap = datas
                gridImageAdapter!!.notifyDataSetChanged()
                setResult(Activity.RESULT_OK, intent)
            }
            if (!showVideo && PublicWay.MAX_SELECT_COUNT == 1) {
                intent = Intent()
                intent.putExtra(JumpTo.TYPE_OBJECT, tempSelectBitmap)
                setResult(Activity.RESULT_OK, intent)
                this.finish()
            }
            /* for (int i = 0; i < PublicWay.activityList.size(); i++) {
                if (null != PublicWay.activityList.get(i)) {
                    PublicWay.activityList.get(i).finish();
                }
            }*/
        }
    }

    override fun onResume() {
        super.onResume()
        //isShowOkBt();
        if (gridImageAdapter != null && dataList != null) {
            gridImageAdapter!!.notifyDataSetChanged()
        }
        Logs.i("请求=========", " ==========执行了吗执行了吗执行了吗执行了吗执行了吗执行了吗执行了吗执行了吗执行了吗执行了吗执行了吗执行了吗")
    }

    override fun dealDatas(requestCode: Int, obj: Any) {

    }

    override fun setTitleId(): Int {
        return 0
    }

    override fun setContentView(): Int {
        return R.layout.plugin_camera_album
    }

    override fun initData() {
        ButterKnife.bind(this)
        SUtils.initScreenDisplayMetrics(this)
        //注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
        val filter = IntentFilter("data.broadcast.action")
        registerReceiver(broadcastReceiver, filter)
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plugin_camera_no_pictures)
        val datas = JumpTo.getObject(this) as ArrayList<ImageItem>
        if (datas != null) {
            tempSelectBitmap = datas
        }
        val selectType = JumpTo.getString(this)
        if (selectType != null) {
            showVideo = selectType == SFileUtils.FileType.FILE_MP4
        }
        val count = JumpTo.getInteger(this)
        if (count != 0) {
            PublicWay.MAX_SELECT_COUNT = count
        } else {
            PublicWay.MAX_SELECT_COUNT = 9
        }
        if (mOption != null) {
            showVideo = mOption!!.isVideoMode
            PublicWay.MAX_SELECT_COUNT = mOption!!.selectCount
        }
        if (mOption != null && mOption!!.selectCount != 0) {
            PublicWay.MAX_SELECT_COUNT = mOption!!.selectCount
        }
        init()
        //这个函数主要用来控制预览和完成按钮的状态
        isShowOkBt()
    }

    override fun onDestroy() {
        super.onDestroy()
        mOption = null
        unregisterReceiver(broadcastReceiver)
    }

    companion object {

        //获取图片的code
        val REQUEST_CODE = 12
        lateinit var contentList: List<ImageBucket>
        lateinit var bitmap: Bitmap

        private var mOption: SelectOptions? = null


        fun show(context: Context, options: SelectOptions) {
            mOption = options
            context.startActivity(Intent(context, AlbumActivity::class.java))
        }


        fun toArray(images: ArrayList<ImageItem>?): Array<String?>? {
            images != null && images.isNotEmpty().run {
                val strings = arrayOfNulls<String>(images.size)
                for ((i, image) in images.withIndex()) {
                    strings[i] = image.imagePath
                }
                return strings
            }
            return null

        }
    }
}

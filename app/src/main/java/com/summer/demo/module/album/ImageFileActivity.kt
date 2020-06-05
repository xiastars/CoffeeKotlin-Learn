package com.summer.demo.module.album

import android.content.Intent
import android.view.View
import android.widget.GridView
import com.summer.demo.R
import com.summer.demo.module.album.adapter.FolderAdapter
import com.summer.demo.module.album.util.ImageItem
import com.summer.demo.module.album.util.PublicWay
import com.summer.demo.module.base.BaseActivity
import com.summer.helper.utils.JumpTo
import java.util.*


/**
 * 这个类主要是用来进行显示包含图片的文件夹
 *
 * @author zhangqian
 */
class ImageFileActivity : BaseActivity() {

    private var folderAdapter: FolderAdapter? = null

    override fun dealDatas(requestCode: Int, obj: Any) {

    }

    override fun setTitleId(): Int {
        return R.string.title_photo
    }

    override fun setContentView(): Int {
        return R.layout.plugin_camera_image_file
    }

    override fun initData() {
        PublicWay.activityList.add(this)
        val gridView = findViewById(R.id.fileGridView) as GridView
        var tempSelectBitmap: ArrayList<ImageItem>? = JumpTo.getObject(this) as ArrayList<ImageItem>
        if (tempSelectBitmap == null) {
            tempSelectBitmap = ArrayList()
        }
        folderAdapter = FolderAdapter(this, tempSelectBitmap)
        gridView.adapter = folderAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //这里是从文件夹里面的所有图片时的界面跳过来的
        if (resultCode == 12) {
            val intent = Intent()
            intent.putExtra(JumpTo.TYPE_OBJECT, data!!.getSerializableExtra(JumpTo.TYPE_OBJECT))
            setResult(12, intent)
            finish()
        }

    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

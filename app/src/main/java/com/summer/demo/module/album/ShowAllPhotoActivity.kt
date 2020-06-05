package com.summer.demo.module.album

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import com.summer.demo.R
import com.summer.demo.module.album.adapter.AlbumGridViewAdapter
import com.summer.demo.module.album.util.ImageItem
import com.summer.demo.module.album.util.PublicWay
import com.summer.demo.module.base.BaseActivity
import com.summer.helper.utils.JumpTo
import com.summer.helper.utils.Logs
import com.summer.helper.view.NRecycleView
import java.util.*

/**
 * 这个是显示一个文件夹里面的所有图片时的界面
 *
 * @author zhangqian
 */
class ShowAllPhotoActivity : BaseActivity() {
    private var gridView: NRecycleView? = null
    private var progressBar: ProgressBar? = null
    private var gridImageAdapter: AlbumGridViewAdapter? = null
    // 完成按钮
    private var okButton: Button? = null
    // 预览按钮
    private var preview: Button? = null

    internal lateinit var tempSelectBitmap: ArrayList<ImageItem>

    internal var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            // TODO Auto-generated method stub
            gridImageAdapter!!.notifyDataSetChanged()
        }
    }

    override fun dealDatas(requestCode: Int, obj: Any) {

    }

    override fun setTitleId(): Int {
        return 0
    }

    override fun setContentView(): Int {
        return R.layout.plugin_camera_show_all_photo
    }

    override fun initData() {

        mList.clear()
        tempSelectBitmap = JumpTo.getObject(this) as ArrayList<ImageItem>
        Logs.i("tem:$tempSelectBitmap")
        for (i in tempSelectBitmap.indices) {
            if (mList.size > 0) {
                // 过滤相同的数据
                for (j in mList.indices) {
                    if (mList[j].imageId == tempSelectBitmap[i].imageId) {
                        break
                    } else if (j == mList.size - 1) {
                        mList.add(tempSelectBitmap[i])
                    }
                }
            } else {
                mList.add(tempSelectBitmap[i])
            }
        }

        preview = findViewById(R.id.showallphoto_preview) as Button
        okButton = findViewById(R.id.showallphoto_ok_button) as Button
        this.intent = getIntent()
        var folderName = intent!!.getStringExtra("folderName")
        if (folderName.length > 14) {
            folderName = folderName.substring(0, 15) + "..."
        }
        setTitle(folderName)
        preview!!.setOnClickListener(PreviewListener())
        init()
        initListener()
        isShowOkBt()
    }

    private inner class PreviewListener : OnClickListener {
        override fun onClick(v: View) {
            if (mList.size > 0) {
                intent!!.putExtra("position", "2")
                intent!!.setClass(this@ShowAllPhotoActivity, GalleryActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun init() {
        val filter = IntentFilter("data.broadcast.action")
        registerReceiver(broadcastReceiver, filter)
        progressBar = findViewById(R.id.showallphoto_progressbar) as ProgressBar
        progressBar!!.visibility = View.GONE
        gridView = findViewById(R.id.showallphoto_myGrid) as NRecycleView
        gridView!!.setGridView(3)
        gridImageAdapter = AlbumGridViewAdapter(this, dataList, mList)
        Logs.i("dataList:$dataList")
        gridView!!.adapter = gridImageAdapter
        okButton = findViewById(R.id.showallphoto_ok_button) as Button
    }

    private fun initListener() {
        var onItemClickListener = object : AlbumGridViewAdapter.OnItemClickListener{
            override fun onItemClick(view: ToggleButton, position: Int, isChecked: Boolean, chooseBt: ImageView) {
                println("请求============" + mList.size + "  ,  " + isChecked)

                if (mList.size >= PublicWay.MAX_SELECT_COUNT && isChecked) {
                    chooseBt.visibility = View.GONE
                    view.isChecked = false
                    Toast.makeText(this@ShowAllPhotoActivity, getString(R.string.only_choose_num), Toast.LENGTH_SHORT).show()
                    return
                }


                if (isChecked) {
                    chooseBt.visibility = View.VISIBLE
                    mList.add(dataList[position])
                    tempSelectBitmap.add(dataList[position])
                    okButton!!.text = getString(R.string.finish) + "(" + mList.size + "/" + PublicWay.MAX_SELECT_COUNT + ")"
                } else {
                    chooseBt.visibility = View.GONE
                    mList.remove(dataList[position])
                    tempSelectBitmap.remove(dataList[position])
                    okButton!!.text = getString(R.string.finish) + "(" + mList.size + "/" + PublicWay.MAX_SELECT_COUNT + ")"
                }
                isShowOkBt()
            }
        }
        gridImageAdapter!!.setOnItemClickListener(onItemClickListener)

        okButton!!.setOnClickListener {
            val intent = Intent()
            intent.putExtra(JumpTo.TYPE_OBJECT, tempSelectBitmap)
            setResult(12, intent)
            finish()
        }
    }

    fun isShowOkBt() {
        if (mList.size > 0) {
            okButton!!.text = getString(R.string.finish) + "(" + mList.size + "/" + PublicWay.MAX_SELECT_COUNT + ")"
            preview!!.isPressed = true
            okButton!!.isPressed = true
            preview!!.isClickable = true
            okButton!!.isClickable = true
            okButton!!.setTextColor(Color.WHITE)
            preview!!.setTextColor(Color.WHITE)
        } else {
            okButton!!.text = getString(R.string.finish) + "(" + mList.size + "/" + PublicWay.MAX_SELECT_COUNT + ")"
            preview!!.isPressed = false
            preview!!.isClickable = false
            okButton!!.isPressed = false
            okButton!!.isClickable = false
            okButton!!.setTextColor(Color.parseColor("#E1E0DE"))
            preview!!.setTextColor(Color.parseColor("#E1E0DE"))
        }
    }


    override fun onRestart() {
        // TODO Auto-generated method stub
        isShowOkBt()
        super.onRestart()
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    companion object {
        var dataList = ArrayList<ImageItem>()

        var mList = ArrayList<ImageItem>()
    }
}

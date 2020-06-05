package com.summer.demo.module.album

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import com.summer.demo.R
import com.summer.demo.module.album.util.AlbumHelper
import com.summer.demo.module.album.util.ImageItem
import com.summer.demo.module.album.util.PublicWay
import com.summer.demo.module.base.BaseActivity
import com.summer.helper.utils.JumpTo
import java.util.*

/**
 * 这个是用于进行图片浏览时的界面
 *
 * @author zhangqian
 */
class GalleryActivity : BaseActivity() {
    // 发送按钮
    private var send_bt: Button? = null
    // 删除按钮
    private var del_bt: Button? = null
    // 顶部显示预览图片位置的textview
    private val positionTextView: TextView? = null
    // 获取前一个activity传过来的position
    private var position: Int = 0
    // 当前的位置
    private var location = 0

    private var listViews: ArrayList<View>? = null
    private var adapter: MyPageAdapter? = null
    private var mContext: Context? = null

    internal var tempSelectBitmap: ArrayList<ImageItem>? = ArrayList()

    private val pageChangeListener = object : OnPageChangeListener {

        override fun onPageSelected(arg0: Int) {
            location = arg0
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {

        }

        override fun onPageScrollStateChanged(arg0: Int) {

        }
    }

    override fun dealDatas(requestCode: Int, obj: Any) {

    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setTitleId(): Int {
        return 0
    }

    override fun setContentView(): Int {
        return R.layout.plugin_camera_gallery
    }

    override fun initData() {
        PublicWay.activityList.add(this)
        mContext = this
        send_bt = findViewById(R.id.send_button) as Button
        del_bt = findViewById(R.id.btn_right) as Button
        send_bt!!.setOnClickListener(GallerySendListener())
        del_bt!!.setOnClickListener(DelListener())

        position = Integer.parseInt(intent!!.getStringExtra("position"))
        tempSelectBitmap = JumpTo.getObject(this) as ArrayList<ImageItem>
        setTitle("预览")
        isShowOkBt()
        // 为发送按钮设置文字

    }


    // 返回按钮添加的监听器
    private inner class BackListener : OnClickListener {
        override fun onClick(v: View) {
            finish()
        }
    }

    // 删除按钮添加的监听器
    private inner class DelListener : OnClickListener {

        override fun onClick(v: View) {
            if (listViews!!.size == 1) {
                tempSelectBitmap!!.clear()
                send_bt!!.text = (getString(R.string.finish) + "("
                        + tempSelectBitmap!!.size + "/" + PublicWay.MAX_SELECT_COUNT
                        + ")")
                val intent = Intent("data.broadcast.action")
                sendBroadcast(intent)
                finish()

            } else {
                tempSelectBitmap!!.removeAt(location)

                listViews!!.removeAt(location)
                adapter!!.setListViews(listViews)
                send_bt!!.text = (getString(R.string.finish) + "("
                        + tempSelectBitmap!!.size + "/" + PublicWay.MAX_SELECT_COUNT
                        + ")")
                adapter!!.notifyDataSetChanged()
            }
        }
    }

    // 完成按钮的监听
    private inner class GallerySendListener : OnClickListener {
        override fun onClick(v: View) {

            AlbumHelper.MAX_SELECT = tempSelectBitmap!!.size
            val intent = Intent()
            setResult(12, intent)

            for (i in PublicWay.activityList.indices) {
                if (null != PublicWay.activityList[i]) {
                    PublicWay.activityList[i].finish()
                }
            }
        }

    }

    fun isShowOkBt() {
        if (tempSelectBitmap!!.size > 0) {
            send_bt!!.text = (getString(R.string.finish) + "("
                    + tempSelectBitmap!!.size + "/" + PublicWay.MAX_SELECT_COUNT + ")")
            send_bt!!.isPressed = true
            send_bt!!.isClickable = true
            send_bt!!.setTextColor(Color.WHITE)
        } else {
            send_bt!!.isPressed = false
            send_bt!!.isClickable = false
            send_bt!!.setTextColor(Color.parseColor("#E1E0DE"))
        }
    }

    internal inner class MyPageAdapter(private var listViews: ArrayList<View>?) : PagerAdapter() {

        private var size: Int = 0

        init {
            size = if (listViews == null) 0 else listViews!!.size
        }

        fun setListViews(listViews: ArrayList<View>?) {
            this.listViews = listViews
            size = listViews?.size ?: 0
        }

        override fun getCount(): Int {
            return size
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun destroyItem(arg0: View, arg1: Int, arg2: Any) {

        }

        override fun finishUpdate(arg0: View) {}

        override fun instantiateItem(arg0: View, arg1: Int): Any {


            return listViews!![arg1 % size]
        }

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1
        }

    }
}

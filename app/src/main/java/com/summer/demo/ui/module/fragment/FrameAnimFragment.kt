package com.summer.demo.ui.module.fragment

import android.graphics.Bitmap
import android.view.View
import com.summer.demo.R
import com.summer.demo.anim.DragRelativeLayout
import com.summer.demo.anim.FrameImgBean
import com.summer.demo.module.base.BaseFragment
import com.summer.demo.view.DragLayer
import com.summer.helper.db.CommonService
import com.summer.helper.db.DBType
import com.summer.helper.db.SerializeUtil
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SThread
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

/**
 * 帧动画演示
 * Created by xiaqiliang on 2017年03月17日 09:48.
 */

class FrameAnimFragment : BaseFragment() {

    internal lateinit var service: CommonService
    private val dragLayer:DragLayer by Bind(R.id.draglyer)

    internal var dragRelativeLayouts: MutableList<DragRelativeLayout>? = null

    private fun createLittle() {

    }

    override fun initView(view: View) {
        service = CommonService(context)
        createLittle()
        val controlLayout = DragRelativeLayout(context!!)
        val fileName = "kaiche"
        val time = System.currentTimeMillis()
        val bitmaps = ArrayList<FrameImgBean>()
        for (i in 1..126) {
            val name = (40000 + i).toString() + ""

            val lastName = fileName + File.separator + name + ".png"
            val frameImgBean = FrameImgBean()
            frameImgBean.imgName = lastName
            frameImgBean.imgType = 2
            bitmaps.add(frameImgBean)
        }
        dragRelativeLayouts = ArrayList()
        for (i in 0..0) {
            val dragRelativeLayout = DragRelativeLayout(context!!)
            dragRelativeLayout.setLayoutPosition(40 + (i + 100), 180 + (i + 600))
            dragLayer.addView(dragRelativeLayout)
            val datas = ArrayList<FrameImgBean>()
            datas.addAll(bitmaps)
            dragRelativeLayout.initBitmaps(datas)
            dragRelativeLayout.circlePlay()
            dragRelativeLayouts!!.add(dragRelativeLayout)
        }


    }

    override fun onHide() {

    }

    override fun onPause() {
        super.onPause()

    }

    override fun onStop() {
        super.onStop()
        Logs.i("check:ONPAUSE")
        if (dragRelativeLayouts != null) {
            for (i in dragRelativeLayouts!!.indices) {
                dragRelativeLayouts!![i].stopPlay()
            }

        }
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Logs.i("check:ONPAUSE")
    }


    /**
     * 缓存Bitmap的办法
     */
    private fun cacheBitmap(bitmaps: Array<Bitmap>, fileName: String) {
        //缓存到本地
        SThread.getIntances().submit {
            val bitmappath = ArrayList<ByteArray>()
            for (i in bitmaps.indices) {
                val bitmap = bitmaps[i]
                Logs.i("bitmap:$bitmap")
                val size = bitmap.width * bitmap.height * 4

                val out = ByteArrayOutputStream(size)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                try {
                    out.flush()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    try {
                        out.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
                val arrays = out.toByteArray()
                bitmappath.add(arrays)
            }
            val lastData = SerializeUtil.serializeObject(bitmappath)
            service.insert(DBType.COMMON_DATAS, fileName, lastData)
        }

    }

    public override fun loadData() {

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.activity_anim
    }

}

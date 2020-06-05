package com.summer.demo.ui.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.summer.demo.R
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SUtils
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * 图片使用综合示例
 * @author Administrator
 */
class PictureUseFragment : BaseSimpleFragment(), View.OnClickListener {
    internal lateinit var ivBg: ImageView

    /**
     * 获取内存卡主路径
     * @return
     */
    private// 判断sd卡是否存在
    // 获取跟目录
    val sdPath: String
        get() {
            var sdDir: File? = null
            try {
                val sdCardExist = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
                if (sdCardExist) {
                    sdDir = Environment.getExternalStorageDirectory()
                } else {
                    val file = File(Environment.getDataDirectory().toString() + "/sdcard")
                    return if (file.canRead()) {
                        file.toString()
                    } else {
                        ""
                    }
                }
                if (sdDir != null) {
                    return sdDir.toString()
                }
            } catch (e: Exception) {
                Log.e("Error", e.message)
            }

            return ""
        }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_picuse, null)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        ivBg = view.findViewById<View>(R.id.iv_bg) as ImageView
        val btnCommon = view.findViewById<View>(R.id.btn_common) as Button
        btnCommon.setOnClickListener(this)

        val btnLonger = view.findViewById<View>(R.id.btn_longer) as Button
        btnLonger.setOnClickListener(this)

        val btnSpecial = view.findViewById<View>(R.id.btn_special) as Button
        btnSpecial.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_common -> {
                /* 图片设置一般使用框架，这里使用的是Glide框架 */
                val path = "$sdPath/temp.jpg"
                /* File是文件管理类，File的生成方式一般是new File(文件的绝对路径)*/
                val file = File(path)
                Logs.i("xia", "检查文件是否存在的方法:" + file.exists())
                Logs.i("xia", "获取文件的绝对路径" + file.absolutePath)
                Logs.i("xia", "判断文件是不是文件夹:" + file.isDirectory)
                if (!file.exists()) {
                    SUtils.makeToast(context, "请在内存卡的根目录添加图片<temp.jpg>")
                    return
                }
                Glide.with(context).load(file).into(ivBg)
            }
            R.id.btn_special//asset里的图片
            -> {
                val `in`: InputStream
                try {
                    `in` = context!!.assets.open("pic/xiehou06.jpg")
                    val bitmap = BitmapFactory.decodeStream(`in`)
                    ivBg.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            R.id.btn_longer -> Glide.with(context).load("http://img.idol001.com/middle/2016/11/27/e3992502d3267e681279c4191058c7ad1480222068.jpg").into(ivBg)
        }/* 注：SUtils里有封装好的方法 *///SUtils.setPic(ivBg, path);
        /* 这里最好使用SUtil.setPic(),将图片缓存到了本地*/
    }

}

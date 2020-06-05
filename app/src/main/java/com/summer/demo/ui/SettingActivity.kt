package com.summer.demo.ui

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.summer.demo.R
import com.summer.demo.dialog.BaseTipsDialog
import com.summer.demo.helper.MainHelper
import com.summer.demo.module.base.BaseActivity
import com.summer.demo.utils.BaseUtils
import com.summer.demo.utils.CUtils
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SFileUtils
import com.summer.helper.utils.SUtils
import java.io.File
import java.math.BigDecimal

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/17 10:05
 */
class SettingActivity : BaseActivity() {

    @BindView(R.id.tv_mine_phone)
    internal var mTvPhone: TextView? = null

    @BindView(R.id.tv_mine_push)
    internal var mTvPush: TextView? = null

    @BindView(R.id.tv_mine_clear)
    internal var mTvClear: TextView? = null

    override fun initData() {
        setBackTag("ac_user_setting")


        val pushState = MainHelper.PUSH_STATUS
        var pushStr = getString(R.string.mine_set_push_open_night)
        when (pushState) {
            3 -> pushStr = getString(R.string.mine_set_push_open)
            2 -> pushStr = getString(R.string.mine_set_push_open_night)
            1 -> pushStr = getString(R.string.mine_set_push_close)
        }
        mTvPush!!.text = pushStr
        getAppCache()
    }

    override fun dealDatas(requestCode: Int, obj: Any) {

    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @OnClick(R.id.mine_set_phone, R.id.mine_set_push, R.id.mine_set_clear, R.id.mine_set_logout, R.id.ll_about)
    fun onCLick(view: View) {

        when (view.id) {

            R.id.mine_set_phone -> {
            }
            R.id.mine_set_push -> {
            }
            R.id.mine_set_clear -> onClickCleanCache()
            R.id.mine_set_logout ->

                BaseUtils.showEasyDialog(context!!, "确定退出登录吗？", object : BaseTipsDialog.DialogAfterClickListener {
                    override fun onSure() {
                        /*       //埋点
                        CUtils.onClick(context,"user_set_logout_sure");
                        //清除Token
                        FSXQSharedPreference.getInstance().setTokenEable("");
                        //重新进入登录页面
                        //LoginByWchatActivity.show(UserSetActivity.this);
                        //清除缓存
                        new CommonService(context).commonDeleteData();
                        //推送Tag清空
                        MainHelper.setEmtpyJpushTag();
                        //设置为游客模式
                        MarUser.isTourist = true;
                        ActivitysManager.finishAllActivity();
                        MainActivity.show(context);*/
                    }

                    override fun onCancel() {
                        CUtils.onClick(context!!, "user_set_logout_cancel")
                    }
                })
            R.id.ll_about -> {
            }
        }

    }

    private fun getAppCache() {
        mTvClear!!.text = acquireSize()
    }

    private fun acquireSize(): String {
        try {
            val size = getFolderSize(File(this.cacheDir.toString() + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR)) + getFolderSize(File(SFileUtils.getFileDirectory()))
            return getFormatSize(size.toDouble())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "0.0Byte"
    }

    private fun clearCache() {
        val ceche = cacheDir.toString()
        deleteFolderFile(ceche, true)
        deleteFolderFile(SFileUtils.getFileDirectory(), false)
    }

    private fun onClickCleanCache() {
        val tipsDialog = BaseTipsDialog(context, "是否清空缓存?", object : BaseTipsDialog.DialogAfterClickListener {
            override fun onSure() {
                clearCache()
                CUtils.onClick(context!!, "user_set_clear_sure")
                SUtils.makeToast(context, "已清除缓存!")
                mTvClear!!.text = acquireSize()
            }

            override fun onCancel() {
                CUtils.onClick(context!!, "user_set_clear_cancel")
            }
        })
        tipsDialog.hideTitle()
        tipsDialog.show()
    }


    override fun onResume() {
        super.onResume()

        initData()
    }

    override fun setTitleId(): Int {
        return R.string.mine_set_title
    }

    override fun setContentView(): Int {
        return R.layout.activity_setting
    }

    companion object {

        @Throws(Exception::class)
        fun getFolderSize(file: File): Long {
            var size: Long = 0
            try {
                val fileList = file.listFiles() ?: return 0
                for (i in fileList.indices) {
                    // 如果下面还有文件
                    if (fileList[i].isDirectory) {
                        size = size + getFolderSize(fileList[i])
                    } else {
                        val fileItem = fileList[i]
                        val fileName = fileItem.name
                        if (fileName == "armeabi-v7a_ffmpeg" || fileName == "x86_ffmpeg") {
                            continue
                        }
                        size = size + fileList[i].length()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return size
        }

        /**
         * 格式化单位
         *
         * @param size
         * @return
         */
        fun getFormatSize(size: Double): String {
            val kiloByte = size / 1024
            if (kiloByte < 1) {
                return size.toString() + "B"
            }

            val megaByte = kiloByte / 1024
            if (megaByte < 1) {
                val result1 = BigDecimal(java.lang.Double.toString(kiloByte))
                return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toPlainString() + "KB"
            }

            val gigaByte = megaByte / 1024
            if (gigaByte < 1) {
                val result2 = BigDecimal(java.lang.Double.toString(megaByte))
                return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toPlainString() + "MB"
            }

            val teraBytes = gigaByte / 1024
            if (teraBytes < 1) {
                val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
                return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toPlainString() + "GB"
            }
            val result4 = BigDecimal(teraBytes)
            return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
        }

        /**
         * 删除指定目录下文件及目录
         *
         * @param deleteThisPath
         * @param filePath
         * @return
         */
        fun deleteFolderFile(filePath: String, deleteThisPath: Boolean) {
            if (!TextUtils.isEmpty(filePath)) {
                try {
                    val file = File(filePath)
                    if (file.isDirectory) {// 如果下面还有文件
                        val files = file.listFiles()
                        for (i in files.indices) {
                            deleteFolderFile(files[i].absolutePath, true)
                        }
                    }
                    if (deleteThisPath) {
                        if (!file.isDirectory) {// 如果是文件，删除
                            val fileName = file.name
                            Logs.i("fileName:$fileName")
                            if (fileName == "armeabi-v7a_ffmpeg" || fileName == "x86_ffmpeg") {
                                return
                            }
                            file.delete()
                        } else {// 目录
                            if (file.listFiles().size == 0) {// 目录下没有文件或者目录，删除
                                file.delete()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }
}

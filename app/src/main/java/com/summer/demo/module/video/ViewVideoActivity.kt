package com.summer.demo.module.video

import android.media.MediaPlayer
import android.view.View
import android.widget.RelativeLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.summer.demo.R
import com.summer.demo.module.base.BaseActivity
import com.summer.demo.module.video.util.MyVideoView
import com.summer.helper.server.PostData
import com.summer.helper.utils.JumpTo
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SFileUtils
import com.summer.helper.utils.SUtils
import com.summer.helper.view.LoadingDialog
import java.io.File

/**
 * 单纯浏览一个视频
 * Created by xiaqiliang on 2017/4/1.
 */
class ViewVideoActivity : BaseActivity() {
    internal lateinit var loadingDialog: LoadingDialog
    internal var path: String? = null

    @BindView(R.id.vd_play)
    internal var mVideoView: MyVideoView? = null
    @BindView(R.id.rl_parent)
    internal var rlParent: RelativeLayout? = null


    override fun onStart() {
        super.onStart()
    }

    override fun dealDatas(rquestCode: Int, obj: Any) {

    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setTitleId(): Int {
        return 0
    }

    override fun setContentView(): Int {
        return R.layout.activity_show_video
    }

    override fun initData() {
        path = JumpTo.getString(this)
        if (path != null && path!!.startsWith("//")) {
            path = PostData.OOSHEAD + ":" + path
        }
        ButterKnife.bind(this)
        loadingDialog = LoadingDialog(context)
        loadingDialog.startLoading()
        setLayoutFullscreen(true)
        changeHeaderStyleTrans(context!!.resources.getColor(R.color.half_grey))
        removeTitle()
        val parent = findViewById(R.id.rl_parent) as RelativeLayout
        val lp = this.window.attributes
        lp.width = SUtils.screenWidth
        lp.height = SUtils.screenHeight
        this.window.attributes = lp
        parent.setOnClickListener { this@ViewVideoActivity.finish() }
        // 开始播放
        setVideoUrl(path)
    }

    fun setVideoUrl(url: String?) {
        val filepath = SFileUtils.getVideoDirectory()
        val fileName = SUtils.getUrlHashCode(url!!) + SFileUtils.FileType.FILE_MP4
        val file = File(filepath + fileName)
        Logs.i("file:" + file.exists() + ",,")
        if (file.exists()) {
            mVideoView!!.setVideoPath(file.absolutePath)
        } else {
            mVideoView!!.setVideoPath(url)
            SUtils.downloadVideo(context, url)
        }
        var listener = object : MediaPlayer.OnPreparedListener{
            override fun onPrepared(mp: MediaPlayer?) {
                loadingDialog.cancelLoading()
                mVideoView!!.setLooping(true)
                mVideoView!!.start()
                val widthF = mVideoView!!.videoWidth.toFloat()
                val heightF = mVideoView!!.videoHeight.toFloat()
                val layoutParams = mVideoView!!.layoutParams
                layoutParams.width = SUtils.screenWidth
                layoutParams.height = (SUtils.screenWidth / widthF * heightF).toInt()
                mVideoView!!.layoutParams = layoutParams
            }
        }
        mVideoView!!.setOnPreparedListener(listener)
    }

}

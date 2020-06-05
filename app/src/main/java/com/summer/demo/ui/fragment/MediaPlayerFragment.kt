package com.summer.demo.ui.fragment

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.summer.demo.R
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SUtils

import java.io.File

/**
 * 音频播放示例
 *
 * @author xiastars@vip.qq.com
 */
class MediaPlayerFragment : BaseSimpleFragment(), View.OnClickListener {
    internal var mMediaPlayer: MediaPlayer? = null
    //这个状态用来确认是否在播放状态,0表示MediaPlayer没有被占用，1表示正在占用,2表示暂停
    internal var mAudioState: Int = 0
    internal var demoUrl = "http://cdn.ishuidi.com.cn/xsd/%E5%88%9B%E8%AF%BE%E8%B5%84%E6%BA%90/%E5%A3%B0%E9%9F%B3/%E8%8B%B1%E6%96%87%E5%84%BF%E6%AD%8C/%E3%80%8AHead,%20Shoulders,%20Knees%20and%20Toes%E3%80%8B/%E3%80%8AHead,%20Shoulders,%20Knees%20and%20Toes%E3%80%8B.mp3"

    /**
     * 获取内存卡主路径
     *
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
        val view = inflater.inflate(R.layout.fragment_mediaplayer, null)
        initView(view)
        mMediaPlayer = MediaPlayer()
        return view
    }

    private fun initView(view: View) {
        val btnCommon = view.findViewById<View>(R.id.btn_common) as Button
        btnCommon.setOnClickListener(this)

        val btnLonger = view.findViewById<View>(R.id.btn_longer) as Button
        btnLonger.setOnClickListener(this)

        val btnSpecial = view.findViewById<View>(R.id.btn_special) as Button
        btnSpecial.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_common//播放
            -> playMediaFile(demoUrl)
            R.id.btn_longer//暂停
            -> pausePlayingAudio()
            R.id.btn_special//继续播放
            -> restartPlayingAudio()
        }
    }


    override fun onResume() {
        super.onResume()
        if (null == mMediaPlayer) {
            mMediaPlayer = MediaPlayer()
        }
    }

    /**
     * 开始播放文件
     *
     * @param fileName
     */
    fun playMediaFile(fileName: String) {
        var fileName = fileName
        //检查传进来的文件是否为空
        if (TextUtils.isEmpty(fileName)) {
            return
        }
        //根据链接，查找本地数据库，如果缓存过，就取本地的文件，如果没有，则把文件下载下来
        val local = SUtils.downloadAudio(context, fileName, true)
        if (null != local) {
            fileName = local
        }
        //存在播放过程中，切换音乐的情况，先停止先前的播放
        stopPlayingAudio()
        Logs.i("xia", "播放:$fileName")
        try {
            if (null == mMediaPlayer) {
                mMediaPlayer = MediaPlayer()
            }
            //reset ,setDataSource,setAudioSgtreamType,prepareAsync,这四个必填，顺序不可乱
            mMediaPlayer!!.reset()
            //将音频路径传入进去，可以是网络链接，可以是本地链接
            mMediaPlayer!!.setDataSource(fileName)
            //设置播放音频流类型，经测试，发现本地的文件用STREAM_MUSIC会卡顿一点时间，所以在这里区分开
            if (fileName.startsWith("http")) {
                mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            } else {
                mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_SYSTEM)
            }
            mMediaPlayer!!.prepareAsync()
            //是否循环播放
            //mMediaPlayer.setLooping(true);
            //音乐加载回调，回调后，马上start,开始播放
            mMediaPlayer!!.setOnPreparedListener {
                Logs.i("xia", "开始播放")
                mMediaPlayer!!.start()
                mAudioState = 1
            }
            mMediaPlayer!!.setOnCompletionListener { stopPlayingAudio() }
        } catch (e: Exception) {
            e.printStackTrace()
            // playMediaFile(fileName,listener);
        }

    }

    /**
     * 暂停播放音频
     */
    fun pausePlayingAudio() {
        if (null == mMediaPlayer)
            return
        if (0 == mAudioState)
            return
        mMediaPlayer!!.pause()
        mAudioState = 2
    }

    /**
     * 继续播放音频
     */
    fun restartPlayingAudio() {
        if (null == mMediaPlayer)
            return
        if (0 == mAudioState)
            return
        mMediaPlayer!!.start()
        mAudioState = 1
    }


    /**
     * 停止播放
     */
    fun stopPlayingAudio() {
        if (null == mMediaPlayer)
            return
        if (0 == mAudioState)
            return
        try {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            Logs.i("xia", "销毁")
            mMediaPlayer = null
            mAudioState = 0
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mMediaPlayer = null
            mAudioState = 0
        }
    }

    /**
     * 当前Activity关闭时，停止音乐播放
     */
    override fun onStop() {
        super.onStop()
        stopPlayingAudio()
    }

}

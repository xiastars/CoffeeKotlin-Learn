package com.summer.demo.helper

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.widget.SeekBar
import android.widget.TextView

import com.summer.demo.AppContext
import com.summer.demo.listener.OnAudioPlayListener
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SUtils

import java.lang.ref.WeakReference

/**
 * 播放音频
 */
class PlayAudioHelper {
    internal var mMediaPlayer: MediaPlayer? = null
    internal var mAudioState: Int = 0
    internal var onAudioPlayListener: OnAudioPlayListener? = null
    //TopicVoiceInfo anwserAudio;//纯粹是为了控制列表里的播放状态
    internal var seekBar: SeekBar? = null
    internal var tvTime: TextView? = null
    internal var myHandler: MyHandler
    //强制关闭状态；音频有可能在onPrepare回调前关闭，从而导致没有真正地关闭
    internal var FORCE_STOP_STATE = 0

    constructor() {
        myHandler = MyHandler(this)
    }

    constructor(onAudioPlayListener: OnAudioPlayListener) {
        this.onAudioPlayListener = onAudioPlayListener
        myHandler = MyHandler(this)
    }

    /**
     * 开始播放文件
     *
     * @param fileName
     */
    fun playMediaFile(fileName: String) {
        var fileName = fileName

        if (TextUtils.isEmpty(fileName)) {
            return
        }
        //根据链接，查找本地数据库，如果缓存过，就取本地的文件，如果没有，则把文件下载下来
        val local = SUtils.downloadAudio(AppContext.instance, fileName, true)
        if (null != local) {
            fileName = local
        }
        stopPlayingAudio()
        FORCE_STOP_STATE = 0
        Logs.i("播放:$fileName")
        try {
            if (null == mMediaPlayer) {
                mMediaPlayer = MediaPlayer()
            }
            mMediaPlayer!!.reset()
            mMediaPlayer!!.setDataSource(fileName)
            //设置播放音频流类型，经测试，发现本地的文件用STREAM_MUSIC会卡顿一点时间，所以在这里区分开
            /* if (fileName.startsWith("http")) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            } else {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM);
            }*/
            mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer!!.prepareAsync()
            mMediaPlayer!!.setOnPreparedListener(MediaPlayer.OnPreparedListener {
                Logs.i("开始播放")
                if (FORCE_STOP_STATE == 1) {
                    stopPlayingAudio()
                    return@OnPreparedListener
                }
                /*   if(anwserAudio != null){
                        //LOCAL_PLAY_POSITION = anwserAudio.getLocalPosition();
                    }*/
                myHandler.sendEmptyMessage(0)
                mMediaPlayer!!.start()
                mAudioState = 1
                if (onAudioPlayListener != null) {
                    onAudioPlayListener!!.onStart()
                }
            })
            mMediaPlayer!!.setOnCompletionListener(MediaPlayer.OnCompletionListener {
                if (onAudioPlayListener == null) {
                    return@OnCompletionListener
                }
                onAudioPlayListener!!.onComplete()
                stopPlayingAudio()
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Logs.i("播放失败")
            // playMediaFile(fileName,listener);
        }

    }

    fun playRaw(rawid: Int) {
        if (rawid == 0) {
            return
        }
        stopPlayingAudio()
        FORCE_STOP_STATE = 0
        try {
            if (null == mMediaPlayer) {
                mMediaPlayer = MediaPlayer()
            }
            mMediaPlayer!!.reset()
            mMediaPlayer!!.setDataSource(AppContext.instance, Uri.parse("android.resource://" + AppContext.instance!!.getPackageName() + "/" + rawid))
            mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_SYSTEM)
            mMediaPlayer!!.prepareAsync()
            mMediaPlayer!!.setOnPreparedListener(MediaPlayer.OnPreparedListener {
                Logs.i("开始播放")
                if (FORCE_STOP_STATE == 1) {
                    stopPlayingAudio()
                    return@OnPreparedListener
                }
                /*   if(anwserAudio != null){
                        //LOCAL_PLAY_POSITION = anwserAudio.getLocalPosition();
                    }*/
                myHandler.sendEmptyMessage(0)
                mMediaPlayer!!.start()
                mAudioState = 1
                if (onAudioPlayListener != null) {
                    onAudioPlayListener!!.onStart()
                }
            })
            mMediaPlayer!!.setOnCompletionListener(MediaPlayer.OnCompletionListener {
                if (onAudioPlayListener == null) {
                    return@OnCompletionListener
                }
                onAudioPlayListener!!.onComplete()
                stopPlayingAudio()
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Logs.i("播放失败")
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


    fun checkEnable(): Boolean {
        return mAudioState == 0
    }

    fun stopPlayingAndClearStatus() {
        tvTime = null
        seekBar = null
        stopPlayingAudio()
    }

    fun stopPlayingAudio() {
        FORCE_STOP_STATE = 1
        myHandler.removeMessages(0)
        if (mMediaPlayer != null && seekBar != null) {
            seekBar!!.progress = 0
        }
        Logs.i("停止播放")
        CURRENT_PLAY_TIME = 0
        LOCAL_PLAY_POSITION = -1
        if (null == mMediaPlayer)
            return
        if (0 == mAudioState)
            return
        try {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
            mAudioState = 0
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mMediaPlayer = null
            mAudioState = 0
        }
        if (onAudioPlayListener != null) {
            onAudioPlayListener!!.onComplete()
        }
        seekBar = null
        tvTime = null
        playAudioHelper = null
    }

    fun setOnAudioPlayListener(onAudioPlayListener: OnAudioPlayListener) {
        this.onAudioPlayListener = onAudioPlayListener
    }

    fun setSeekBarAndiTimeView(seekBar: SeekBar?, tvTime: TextView) {
        this.seekBar = seekBar
        this.tvTime = tvTime
        if (seekBar == null) {
            return
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (mMediaPlayer == null) {
                    return
                }
                val dest = seekBar.progress
                val time = mMediaPlayer!!.duration
                val max = seekBar.max
                mMediaPlayer!!.seekTo(time * dest / max)
            }
        })
    }

    private fun updateSeekBar() {
        if (mMediaPlayer == null || tvTime == null || mAudioState == 0) {
            myHandler.removeMessages(0)
            return
        }
        val position = mMediaPlayer!!.currentPosition
        val time = mMediaPlayer!!.duration
        CURRENT_PLAY_TIME = (time - position) / 1000
        if (seekBar == null) {
            tvTime!!.text = " $CURRENT_PLAY_TIME\""
            myHandler.sendEmptyMessageDelayed(0, 500)
            return
        }

        val max = seekBar!!.max
        Logs.i("position::$position,,$time,,,$max")
        CURRENT_PLAY_TIME = (time - position) / 1000
        if (time == 0) {
            return
        }
        val progress = (max.toFloat() * position / time).toInt()
        CURRENT_PLAY_POS = progress
        if (tvTime != null) {
            /* if(seekBar.getTag() != null){
                int pos = (int) seekBar.getTag();
                Logs.i("pos::::"+anwserAudio.getLocalPosition()+",,"+anwserAudio.isLocalPlaying() +",,anwserAudio"+seekBar+"<,,"+CURRENT_PLAY_POS);
                if(anwserAudio != null && pos == anwserAudio.getLocalPosition() && anwserAudio.isLocalPlaying()){
                    if(anwserAudio != null){
                        anwserAudio.getLocalSeekBar().setProgress(CURRENT_PLAY_POS);
                        anwserAudio.getTvTime().setText(CURRENT_PLAY_TIME+"\"");
                        anwserAudio.setLocalPlayingTime(CURRENT_PLAY_TIME);
                        anwserAudio.setLocalPlayingProgress(CURRENT_PLAY_POS);
                    }
                }else{
                    seekBar.setProgress(0);
                }
            }else{
                seekBar.setProgress(0);
            }*/
        }

        myHandler.sendEmptyMessageDelayed(0, 500)
    }

    class MyHandler(activity: PlayAudioHelper) : Handler() {
        private val mActivity: WeakReference<PlayAudioHelper>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity = mActivity.get()
            if (null != activity) {
                when (msg.what) {
                    0 -> activity.updateSeekBar()
                }
            }
        }
    }

    companion object {
        internal var playAudioHelper: PlayAudioHelper? = null

        var LOCAL_PLAY_POSITION = -1//主题列表在播放状态音频的标记
        var CURRENT_PLAY_POS = 0//当前播放位置
        var CURRENT_PLAY_TIME = 0

        val instance: PlayAudioHelper
            @Synchronized get() {
                if (playAudioHelper != null) {
                    playAudioHelper!!.stopPlayingAudio()
                }
                playAudioHelper = PlayAudioHelper()

                return playAudioHelper!!
            }

        val instanceWithoutStop: PlayAudioHelper
            @Synchronized get() {
                playAudioHelper = PlayAudioHelper()
                return playAudioHelper as PlayAudioHelper
            }
    }

    /* public TopicVoiceInfo getAnwserAudio() {
        return anwserAudio;
    }

    public void setAnwserAudio(TopicVoiceInfo anwserAudio) {
        this.anwserAudio = anwserAudio;
    }*/
}

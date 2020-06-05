package com.summer.demo.module.video.util

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView

import java.io.IOException

/**
 * Created by dell on 2017/6/24.
 */

class MyVideoView : TextureView, TextureView.SurfaceTextureListener {

    private var mOnCompletionListener: MediaPlayer.OnCompletionListener? = null
    private var mOnPreparedListener: MediaPlayer.OnPreparedListener? = null
    private var mOnErrorListener: MediaPlayer.OnErrorListener? = null
    private var mOnSeekCompleteListener: MediaPlayer.OnSeekCompleteListener? = null
    private var mOnPlayStateListener: OnPlayStateListener? = null
    var mediaPlayer: MediaPlayer? = null
        private set
    private var mSurfaceHolder: SurfaceTexture? = null

    private var mCurrentState = STATE_IDLE
    private var mTargetState = STATE_IDLE

    var videoWidth: Int = 0
        private set
    var videoHeight: Int = 0
        private set
    //	private int mSurfaceWidth;
    //	private int mSurfaceHeight;

    private var mVolumn = -1f
    var duration: Int = 0
        private set
    private var mUri: Uri? = null

    /** 获取当前播放位置  */
    //可用状态{Idle, Initialized, Prepared, Started, Paused, Stopped, PlaybackCompleted}
    val currentPosition: Int
        get() {
            var position = 0
            if (mediaPlayer != null) {
                when (mCurrentState) {
                    STATE_PLAYBACK_COMPLETED -> position = duration
                    STATE_PLAYING, STATE_PAUSED -> try {
                        position = mediaPlayer!!.currentPosition
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
            return position
        }

    //可用状态{Idle, Initialized, Prepared, Started, Paused, Stopped, PlaybackCompleted}
    val isPlaying: Boolean
        get() {
            if (mediaPlayer != null && mCurrentState == STATE_PLAYING) {
                try {
                    return mediaPlayer!!.isPlaying
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            return false
        }

    private val mCompletionListener = MediaPlayer.OnCompletionListener { mp ->
        mCurrentState = STATE_PLAYBACK_COMPLETED
        //			mTargetState = STATE_PLAYBACK_COMPLETED;
        if (mOnCompletionListener != null)
            mOnCompletionListener!!.onCompletion(mp)
    }

    internal var mPreparedListener: MediaPlayer.OnPreparedListener = MediaPlayer.OnPreparedListener { mp ->
        //必须是正常状态
        if (mCurrentState == STATE_PREPARING) {
            mCurrentState = STATE_PREPARED
            try {
                duration = mp.duration
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }

            try {
                videoWidth = mp.videoWidth
                videoHeight = mp.videoHeight
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }

            when (mTargetState) {
                STATE_PREPARED -> if (mOnPreparedListener != null)
                    mOnPreparedListener!!.onPrepared(mediaPlayer)
                STATE_PLAYING -> start()
            }
        }
    }

    private val mSeekCompleteListener = MediaPlayer.OnSeekCompleteListener { mp ->
        if (mOnSeekCompleteListener != null)
            mOnSeekCompleteListener!!.onSeekComplete(mp)
    }

    private val mErrorListener = MediaPlayer.OnErrorListener { mp, framework_err, impl_err ->
        mCurrentState = STATE_ERROR
        //			mTargetState = STATE_ERROR;
        //FIX，可以考虑出错以后重新开始
        if (mOnErrorListener != null)
            mOnErrorListener!!.onError(mp, framework_err, impl_err)

        true
    }

    /** 是否可用  */
    //|| mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYING
    val isPrepared: Boolean
        get() = mediaPlayer != null && mCurrentState == STATE_PREPARED

    private val mVideoHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                HANDLER_MESSAGE_PARSE -> pause()
                HANDLER_MESSAGE_LOOP -> if (isPlaying) {
                    seekTo(msg.arg1)
                    sendMessageDelayed(this.obtainMessage(HANDLER_MESSAGE_LOOP, msg.arg1, msg.arg2), msg.arg2.toLong())
                }
                else -> {
                }
            }
            super.handleMessage(msg)
        }
    }

    //	SurfaceTextureAvailable

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initVideoView()
    }

    constructor(context: Context) : super(context) {
        initVideoView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initVideoView()
    }

    protected fun initVideoView() {
        try {
            val mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            mVolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        } catch (e: UnsupportedOperationException) {

        }

        //		mTryCount = 0;
        videoWidth = 0
        videoHeight = 0
        surfaceTextureListener = this
        //		setFocusable(true);
        //		setFocusableInTouchMode(true);
        //		requestFocus();
        mCurrentState = STATE_IDLE
        mTargetState = STATE_IDLE
    }

    fun setOnPreparedListener(l: MediaPlayer.OnPreparedListener) {
        mOnPreparedListener = l
    }

    fun setOnErrorListener(l: MediaPlayer.OnErrorListener) {
        mOnErrorListener = l
    }

    fun setOnPlayStateListener(l: OnPlayStateListener) {
        mOnPlayStateListener = l
    }

    fun setOnSeekCompleteListener(l: MediaPlayer.OnSeekCompleteListener) {
        mOnSeekCompleteListener = l
    }

    interface OnPlayStateListener {
        fun onStateChanged(isPlaying: Boolean)
    }

    fun setOnCompletionListener(l: MediaPlayer.OnCompletionListener) {
        mOnCompletionListener = l
    }

    fun setVideoPath(path: String) {
        //		if (StringUtils.isNotEmpty(path) && MediaUtils.isNative(path)) {
        mTargetState = STATE_PREPARED
        openVideo(Uri.parse(path))
        //		}
    }

    fun reOpen() {
        mTargetState = STATE_PREPARED
        openVideo(mUri)
    }

    /** 重试  */
    private fun tryAgain(e: Exception) {
        e.printStackTrace()
        mCurrentState = STATE_ERROR
        openVideo(mUri)
    }

    fun start() {
        mTargetState = STATE_PLAYING
        //可用状态{Prepared, Started, Paused, PlaybackCompleted}
        if (mediaPlayer != null && (mCurrentState == STATE_PREPARED || mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYING || mCurrentState == STATE_PLAYBACK_COMPLETED)) {
            try {
                if (!isPlaying)
                    mediaPlayer!!.start()
                mCurrentState = STATE_PLAYING
                if (mOnPlayStateListener != null)
                    mOnPlayStateListener!!.onStateChanged(true)
            } catch (e: IllegalStateException) {
                tryAgain(e)
            } catch (e: Exception) {
                tryAgain(e)
            }

        }
    }

    fun pause() {
        mTargetState = STATE_PAUSED
        //可用状态{Started, Paused}
        if (mediaPlayer != null && (mCurrentState == STATE_PLAYING || mCurrentState == STATE_PAUSED)) {
            try {
                mediaPlayer!!.pause()
                mCurrentState = STATE_PAUSED
                if (mOnPlayStateListener != null)
                    mOnPlayStateListener!!.onStateChanged(false)
            } catch (e: IllegalStateException) {
                tryAgain(e)
            } catch (e: Exception) {
                tryAgain(e)
            }

        }
    }

    fun stop() {
        mTargetState = STATE_STOP
        if (mediaPlayer != null && (mCurrentState == STATE_PLAYING || mCurrentState == STATE_PAUSED)) {
            try {
                mediaPlayer!!.stop()
                mCurrentState = STATE_STOP
                if (mOnPlayStateListener != null)
                    mOnPlayStateListener!!.onStateChanged(false)
            } catch (e: IllegalStateException) {
                tryAgain(e)
            } catch (e: Exception) {
                tryAgain(e)
            }

        }
    }

    fun setVolume(volume: Float) {
        //可用状态{Idle, Initialized, Stopped, Prepared, Started, Paused, PlaybackCompleted}
        if (mediaPlayer != null && (mCurrentState == STATE_PREPARED || mCurrentState == STATE_PLAYING || mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYBACK_COMPLETED)) {
            try {
                mediaPlayer!!.setVolume(volume, volume)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun setLooping(looping: Boolean) {
        //可用状态{Idle, Initialized, Stopped, Prepared, Started, Paused, PlaybackCompleted}
        if (mediaPlayer != null && (mCurrentState == STATE_PREPARED || mCurrentState == STATE_PLAYING || mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYBACK_COMPLETED)) {
            try {
                mediaPlayer!!.isLooping = looping
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun seekTo(msec: Int) {
        var msec = msec
        //可用状态{Prepared, Started, Paused, PlaybackCompleted}
        if (mediaPlayer != null && (mCurrentState == STATE_PREPARED || mCurrentState == STATE_PLAYING || mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYBACK_COMPLETED)) {
            try {
                if (msec < 0)
                    msec = 0
                mediaPlayer!!.seekTo(msec)
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    /** 调用release方法以后MediaPlayer无法再恢复使用  */
    fun release() {
        mTargetState = STATE_RELEASED
        mCurrentState = STATE_RELEASED
        if (mediaPlayer != null) {
            try {
                mediaPlayer!!.release()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            mediaPlayer = null
        }
    }

    fun openVideo(uri: Uri?) {
        if (uri == null || mSurfaceHolder == null || context == null) {
            // not ready for playback just yet, will try again later
            if (mSurfaceHolder == null && uri != null) {
                mUri = uri
            }
            return
        }

        mUri = uri
        duration = 0

        //Idle 状态：当使用new()方法创建一个MediaPlayer对象或者调用了其reset()方法时，该MediaPlayer对象处于idle状态。
        //End 状态：通过release()方法可以进入End状态，只要MediaPlayer对象不再被使用，就应当尽快将其通过release()方法释放掉
        //Initialized 状态：这个状态比较简单，MediaPlayer调用setDataSource()方法就进入Initialized状态，表示此时要播放的文件已经设置好了。
        //Prepared 状态：初始化完成之后还需要通过调用prepare()或prepareAsync()方法，这两个方法一个是同步的一个是异步的，只有进入Prepared状态，才表明MediaPlayer到目前为止都没有错误，可以进行文件播放。

        var exception: Exception? = null
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
                mediaPlayer!!.setOnPreparedListener(mPreparedListener)
                mediaPlayer!!.setOnCompletionListener(mCompletionListener)
                mediaPlayer!!.setOnErrorListener(mErrorListener)
                mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mediaPlayer!!.setOnSeekCompleteListener(mSeekCompleteListener)
                //			mMediaPlayer.setScreenOnWhilePlaying(true);
                mediaPlayer!!.setVolume(mVolumn, mVolumn)
                mediaPlayer!!.setSurface(Surface(mSurfaceHolder))
            } else {
                mediaPlayer!!.reset()
            }
            mediaPlayer!!.setDataSource(context, uri)

            //			if (mLooping)
            //				mMediaPlayer.setLooping(true);//循环播放
            mediaPlayer!!.prepareAsync()
            // we don't set the target state here either, but preserve the
            // target state that was there before.
            mCurrentState = STATE_PREPARING
        } catch (ex: IOException) {
            exception = ex
        } catch (ex: IllegalArgumentException) {
            exception = ex
        } catch (ex: Exception) {
            exception = ex
        }

        if (exception != null) {
            exception.printStackTrace()
            mCurrentState = STATE_ERROR
            mErrorListener?.onError(mediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0)
        }
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        val needReOpen = mSurfaceHolder == null
        mSurfaceHolder = surface
        if (needReOpen) {
            reOpen()
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        //画布失效
        mSurfaceHolder = null
        release()
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

    }

    /** 定时暂停  */
    fun pauseDelayed(delayMillis: Int) {
        if (mVideoHandler.hasMessages(HANDLER_MESSAGE_PARSE))
            mVideoHandler.removeMessages(HANDLER_MESSAGE_PARSE)
        mVideoHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_PARSE, delayMillis.toLong())
    }

    /** 暂停并且清除定时任务  */
    fun pauseClearDelayed() {
        pause()
        if (mVideoHandler.hasMessages(HANDLER_MESSAGE_PARSE))
            mVideoHandler.removeMessages(HANDLER_MESSAGE_PARSE)
        if (mVideoHandler.hasMessages(HANDLER_MESSAGE_LOOP))
            mVideoHandler.removeMessages(HANDLER_MESSAGE_LOOP)
    }

    /** 区域内循环播放  */
    fun loopDelayed(startTime: Int, endTime: Int) {
        val delayMillis = endTime - startTime
        seekTo(startTime)
        if (!isPlaying)
            start()
        if (mVideoHandler.hasMessages(HANDLER_MESSAGE_LOOP))
            mVideoHandler.removeMessages(HANDLER_MESSAGE_LOOP)
        mVideoHandler.sendMessageDelayed(mVideoHandler.obtainMessage(HANDLER_MESSAGE_LOOP, currentPosition, delayMillis), delayMillis.toLong())
    }

    companion object {

        private val STATE_ERROR = -1
        private val STATE_IDLE = 0
        private val STATE_PREPARING = 1
        private val STATE_PREPARED = 2
        private val STATE_PLAYING = 3
        private val STATE_PAUSED = 4
        private val STATE_STOP = 5
        /**
         * PlaybackCompleted状态：文件正常播放完毕，而又没有设置循环播放的话就进入该状态，
         * 并会触发OnCompletionListener的onCompletion
         * ()方法。此时可以调用start()方法重新从头播放文件，也可以stop()停止MediaPlayer，或者也可以seekTo()来重新定位播放位置。
         */
        private val STATE_PLAYBACK_COMPLETED = 5
        /** Released/End状态：通过release()方法可以进入End状态  */
        private val STATE_RELEASED = 5

        //	/** 是否能即可播放 */
        //	public boolean canStart() {
        //		return mMediaPlayer != null && (mCurrentState == STATE_PREPARED || mCurrentState == STATE_PAUSED);
        //	}

        private val HANDLER_MESSAGE_PARSE = 0
        private val HANDLER_MESSAGE_LOOP = 1
    }
}

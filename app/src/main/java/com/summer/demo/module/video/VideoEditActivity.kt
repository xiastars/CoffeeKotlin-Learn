package com.summer.demo.module.video

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.summer.demo.R
import com.summer.demo.module.album.util.ImageItem
import com.summer.demo.module.base.BaseActivity
import com.summer.demo.module.video.util.CompressListener
import com.summer.demo.module.video.util.Compressor
import com.summer.demo.module.video.util.EditSpacingItemDecoration
import com.summer.demo.module.video.util.ExtractFrameWorkThread
import com.summer.demo.module.video.util.ExtractVideoInfoUtil
import com.summer.demo.module.video.util.InitListener
import com.summer.demo.module.video.util.MyVideoView
import com.summer.demo.module.video.util.OnTrimVideoListener
import com.summer.demo.module.video.util.PictureUtils
import com.summer.demo.module.video.util.RangeSeekBar
import com.summer.demo.module.video.util.TrimVideoUtils
import com.summer.demo.module.video.util.VideoEditInfo
import com.summer.demo.view.CommonSureView5
import com.summer.helper.permission.PermissionUtils
import com.summer.helper.utils.JumpTo
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SFileUtils
import com.summer.helper.utils.STimeUtils
import com.summer.helper.utils.SUtils
import com.summer.helper.view.LoadingDialog
import com.summer.helper.view.review.RRelativeLayout

import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

/**
 * 视频编辑界面
 */
class VideoEditActivity : BaseActivity() {
    @BindView(R.id.btn_right)
    internal var btnRight: CommonSureView5? = null
    @BindView(R.id.title)
    internal var title: RRelativeLayout? = null
    @BindView(R.id.uVideoView)
    internal var mVideoView: MyVideoView? = null
    @BindView(R.id.id_rv_id)
    internal var mRecyclerView: RecyclerView? = null
    @BindView(R.id.positionIcon)
    internal var positionIcon: ImageView? = null
    @BindView(R.id.id_seekBarLayout)
    internal var seekBarLayout: LinearLayout? = null
    @BindView(R.id.layout_bottom)
    internal var layoutBottom: FrameLayout? = null
    @BindView(R.id.tv_edit_video)
    internal var tvEditVideo: TextView? = null
    @BindView(R.id.rl_edtview)
    internal var rlEdtview: RelativeLayout? = null
    @BindView(R.id.tv_video_time)
    internal var tvVideoTime: TextView? = null

    private var mExtractVideoInfoUtil: ExtractVideoInfoUtil? = null
    internal var loadingDialog: LoadingDialog? = null

    private var seekBar: RangeSeekBar? = null
    private var videoEditAdapter: VideoEditAdapter? = null
    private var averageMsPx: Float = 0.toFloat()//每毫秒所占的px
    private var averagePxMs: Float = 0.toFloat()//每px所占用的ms毫秒
    private var OutPutFileDirPath: String? = null
    private var mExtractFrameWorkThread: ExtractFrameWorkThread? = null
    private var path: String? = null
    private var leftProgress: Long = 0
    private var rightProgress: Long = 0
    private var scrollPos: Long = 0
    private var mScaledTouchSlop: Int = 0
    private var lastScrollX: Int = 0
    private var isSeeking: Boolean = false
    internal var videoItem: ImageItem? = null
    internal lateinit var mCompressor: Compressor

    internal var finishEnable: Boolean = false//完成是否可以点击
    private var duration: Long = 0//视频时长
    private var mMaxWidth: Int = 0
    internal var mVideoWidth = 1080
    internal var mVideoHeight = 720
    internal var mScale = 1f

    private var isOverScaledTouchSlop: Boolean = false

    private val mOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            Log.d(TAG, "-------newState:>>>>>$newState")
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                isSeeking = false
                //                videoStart();
            } else {
                isSeeking = true
                if (isOverScaledTouchSlop && mVideoView != null && mVideoView!!.isPlaying) {
                    videoPause()
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            isSeeking = false
            val scrollX = scrollXDistance
            //达不到滑动的距离
            if (Math.abs(lastScrollX - scrollX) < mScaledTouchSlop) {
                isOverScaledTouchSlop = false
                return
            }
            isOverScaledTouchSlop = true
            Log.d(TAG, "-------scrollX:>>>>>$scrollX")
            //初始状态,why ? 因为默认的时候有35dp的空白！
            if (scrollX == -SUtils.getDip(this@VideoEditActivity, 35)) {
                scrollPos = 0
            } else {
                // why 在这里处理一下,因为onScrollStateChanged早于onScrolled回调
                if (mVideoView != null && mVideoView!!.isPlaying) {
                    videoPause()
                }
                isSeeking = true
                scrollPos = (averageMsPx * (SUtils.getDip(this@VideoEditActivity, 35) + scrollX)).toLong()
                Log.d(TAG, "-------scrollPos:>>>>>$scrollPos")
                leftProgress = seekBar!!.selectedMinValue + scrollPos
                rightProgress = seekBar!!.selectedMaxValue + scrollPos
                Log.d(TAG, "-------leftProgress:>>>>>$leftProgress")
                mVideoView!!.seekTo(leftProgress.toInt())
            }
            lastScrollX = scrollX
        }
    }

    /**
     * 水平滑动了多少px
     *
     * @return int px
     */
    private val scrollXDistance: Int
        get() {
            val layoutManager = mRecyclerView!!.layoutManager as LinearLayoutManager?
            val position = layoutManager!!.findFirstVisibleItemPosition()
            val firstVisibleChildView = layoutManager.findViewByPosition(position)
            val itemWidth = firstVisibleChildView!!.width
            return position * itemWidth - firstVisibleChildView.left
        }

    private var animator: ValueAnimator? = null

    private val mUIHandler = MainHandler(this)

    private val mOnRangeSeekBarChangeListener = object : RangeSeekBar.OnRangeSeekBarChangeListener {
        override fun onRangeSeekBarValuesChanged(bar: RangeSeekBar, minValue: Long, maxValue: Long, action: Int, isMin: Boolean, pressedThumb: RangeSeekBar.Thumb?) {
            leftProgress = minValue + scrollPos
            rightProgress = maxValue + scrollPos
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d(TAG, "-----ACTION_DOWN---->>>>>>")
                    isSeeking = false
                    videoPause()
                }
                MotionEvent.ACTION_MOVE -> {
                    Log.d(TAG, "-----ACTION_MOVE---->>>>>>")
                    isSeeking = true
                    mVideoView!!.seekTo((if (pressedThumb == RangeSeekBar.Thumb.MIN)
                        leftProgress
                    else
                        rightProgress).toInt())
                }
                MotionEvent.ACTION_UP -> {
                    Log.d(TAG, "-----ACTION_UP--leftProgress--->>>>>>$leftProgress")
                    isSeeking = false
                    //从minValue开始播
                    mVideoView!!.seekTo(leftProgress.toInt())
                }
                else -> {
                }
            }//                    videoStart();
            duration = rightProgress - leftProgress
            changeFinishStyle()
        }
    }

    private val handler = Handler()
    private val run = object : Runnable {

        override fun run() {
            videoProgressUpdate()
            handler.postDelayed(this, 1000)
        }
    }

    override fun dealDatas(requestCode: Int, obj: Any) {

    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setTitleId(): Int {
        return R.string.edit_video
    }

    override fun setContentView(): Int {
        return R.layout.activity_video_edit
    }

    override fun initData() {
        ButterKnife.bind(this)
        context = this
        btnRight!!.visibility = View.VISIBLE
        btnRight!!.changeStyle(true)
        SUtils.initScreenDisplayMetrics(this)
        PermissionUtils.checkReadPermission(this)
        videoItem = JumpTo.getObject(this) as ImageItem
        if (videoItem != null) {
            path = videoItem!!.videoPath
            duration = videoItem!!.duration.toLong()
        }
        Logs.i("xia", "duration:$duration")
        if (!File(path!!).exists()) {
            SUtils.makeToast(this, "视频文件不存在")
            finish()
            return
        }
        initVideo()
        initView()
        initEditVideo()
        initPlay()
    }

    private fun initVideo() {
        if (!File(path!!).exists()) {
            Toast.makeText(this, "视频文件不存在", Toast.LENGTH_LONG).show()
            finish()
        }
        mExtractVideoInfoUtil = ExtractVideoInfoUtil(path!!)
        duration = java.lang.Long.valueOf(mExtractVideoInfoUtil!!.videoLength)
        mMaxWidth = SUtils.screenWidth - SUtils.getDip(this, 70)
        mScaledTouchSlop = ViewConfiguration.get(this).scaledTouchSlop
    }

    private fun initView() {

        changeFinishStyle()
        if (finishEnable) {
            rlEdtview!!.visibility = View.GONE
            layoutBottom!!.visibility = View.VISIBLE
        } else {
            rlEdtview!!.visibility = View.VISIBLE
            layoutBottom!!.visibility = View.GONE

        }
        mRecyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        videoEditAdapter = VideoEditAdapter(this,
                (SUtils.screenWidth - SUtils.getDip(this, 70)) / 10)
        mRecyclerView!!.adapter = videoEditAdapter
        mRecyclerView!!.addOnScrollListener(mOnScrollListener)
    }

    /**
     * 改变完成的颜色
     */
    private fun changeFinishStyle() {
        finishEnable = duration <= MAX_CUT_DURATION
        if (finishEnable) {
            btnRight!!.setTextColor(context!!.resources.getColor(R.color.black))
        } else {
            btnRight!!.setTextColor(context!!.resources.getColor(R.color.black33))
        }
        tvVideoTime!!.text = "视频时长:" + duration / 1000 + "s"
    }

    private fun initEditVideo() {
        //for video edit
        val startPosition: Long = 0
        val endPosition = duration
        val thumbnailsCount: Int
        val rangeWidth: Int
        val isOver_60_s: Boolean
        if (endPosition <= MAX_CUT_DURATION) {
            isOver_60_s = false
            thumbnailsCount = MAX_COUNT_RANGE
            rangeWidth = mMaxWidth
        } else {
            isOver_60_s = true
            thumbnailsCount = (endPosition * 1.0f / (MAX_CUT_DURATION * 1.0f) * MAX_COUNT_RANGE).toInt()
            rangeWidth = mMaxWidth / MAX_COUNT_RANGE * thumbnailsCount
        }
        mRecyclerView!!.addItemDecoration(EditSpacingItemDecoration(SUtils.getDip(this, 35), thumbnailsCount))

        if (isOver_60_s) {
            seekBar = RangeSeekBar(this, 0L, MAX_CUT_DURATION)
            seekBar!!.selectedMinValue = 0L
            seekBar!!.selectedMaxValue = MAX_CUT_DURATION
        } else {
            seekBar = RangeSeekBar(this, 0L, endPosition)
            seekBar!!.selectedMinValue = 0L
            seekBar!!.selectedMaxValue = endPosition
        }
        seekBar!!.setMin_cut_time(MIN_CUT_DURATION)//设置最小裁剪时间
        seekBar!!.isNotifyWhileDragging = true
        seekBar!!.setOnRangeSeekBarChangeListener(mOnRangeSeekBarChangeListener)
        seekBarLayout!!.addView(seekBar)

        Log.d(TAG, "-------thumbnailsCount--->>>>$thumbnailsCount")
        averageMsPx = duration * 1.0f / rangeWidth * 1.0f
        Log.d(TAG, "-------rangeWidth--->>>>$rangeWidth")
        Log.d(TAG, "-------localMedia.getDuration()--->>>>$duration")
        Log.d(TAG, "-------averageMsPx--->>>>$averageMsPx")
        OutPutFileDirPath = PictureUtils.getSaveEditThumbnailDir(this)
        val extractW = (SUtils.screenWidth - SUtils.getDip(this, 70)) / MAX_COUNT_RANGE
        val extractH = SUtils.getDip(this, 55)
        mExtractFrameWorkThread = ExtractFrameWorkThread(extractW, extractH, mUIHandler, path!!, OutPutFileDirPath!!, startPosition, endPosition, thumbnailsCount)
        mExtractFrameWorkThread!!.start()

        leftProgress = 0
        rightProgress = if (isOver_60_s) MAX_CUT_DURATION else endPosition
        averagePxMs = mMaxWidth * 1.0f / (rightProgress - leftProgress)
        duration = rightProgress - leftProgress
        changeFinishStyle()
        Log.d(TAG, "------averagePxMs----:>>>>>$averagePxMs")
    }

    private fun initPlay() {
        mVideoView!!.setVideoPath(path!!)
        //设置videoview的OnPrepared监听
        var prepareListener = object : MediaPlayer.OnPreparedListener{
            override fun onPrepared(mp: MediaPlayer?) {
                mVideoWidth = mVideoView!!.videoWidth
                mVideoHeight = mVideoView!!.videoHeight
                mScale = 320 / mVideoWidth.toFloat()
                var scale = SUtils.screenWidth / mVideoWidth.toFloat()
                if (mVideoHeight > mVideoWidth) {
                    scale /= 1.5f
                }
                val params = mVideoView!!.layoutParams as FrameLayout.LayoutParams
                params.height = (mVideoHeight * scale).toInt()
                params.width = (mVideoWidth * scale).toInt()
                mVideoView!!.invalidate()
                mVideoView!!.requestLayout()
                mVideoView!!.start()
                //设置MediaPlayer的OnSeekComplete监听
                mp!!.setOnSeekCompleteListener {
                    if (!isSeeking) {
                        videoStart()
                    }
                }
            }
        }
        mVideoView!!.setOnPreparedListener (prepareListener)
        //first
        videoStart()
    }

    private fun anim() {
        Log.d(TAG, "--anim--onProgressUpdate---->>>>>>>" + mVideoView!!.currentPosition)
        if (positionIcon!!.visibility == View.GONE) {
            positionIcon!!.visibility = View.VISIBLE
        }
        val params = positionIcon!!.layoutParams as FrameLayout.LayoutParams
        val start = (SUtils.getDip(this, 35) + (leftProgress/*mVideoView.getCurrentPosition()*/ - scrollPos) * averagePxMs).toInt()
        val end = (SUtils.getDip(this, 35) + (rightProgress - scrollPos) * averagePxMs).toInt()
        animator = ValueAnimator
                .ofInt(start, end)
                .setDuration(rightProgress - scrollPos - (leftProgress/*mVideoView.getCurrentPosition()*/ - scrollPos))
        animator!!.interpolator = LinearInterpolator()
        animator!!.addUpdateListener { animation ->
            params.leftMargin = animation.animatedValue as Int
            positionIcon!!.layoutParams = params
        }
        animator!!.start()
    }

    @OnClick(R.id.ll_back, R.id.tv_edit_video, R.id.btn_right)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.ll_back -> this.finish()
            R.id.tv_edit_video -> {
                rlEdtview!!.visibility = View.GONE
                layoutBottom!!.visibility = View.VISIBLE
                tvVideoTime!!.visibility = View.VISIBLE
            }
            R.id.btn_right -> {
                if (!finishEnable) {
                    SUtils.makeToast(context, "请先编辑视频!")
                    return
                }
                compressVideo()
            }
        }
    }

    /**
     * 裁剪和压缩视频
     */
    private fun compressVideo() {
        loadingDialog = LoadingDialog(context)
        loadingDialog!!.startLoading("正在压缩中")
        val saveCutPath = SFileUtils.getVideoDirectory() + System.currentTimeMillis() + "_cut" + SFileUtils.FileType.FILE_MP4
        try {
            TrimVideoUtils.startTrim(File(path!!), saveCutPath, leftProgress, rightProgress, object : OnTrimVideoListener {
                override fun onTrimStarted() {

                }

                override fun getResult(uri: Uri) {
                    //JumpTo.getInstance().commonJump(context, ShowVideoDialog.class,saveCutPath);
                    mCompressor = Compressor(this@VideoEditActivity)
                    mCompressor.loadBinary(object : InitListener {
                        override fun onLoadSuccess() {
                            //cutCommand(path, saveCutPath, leftProgress, rightProgress);
                            Logs.i("裁剪后的大小" + File(saveCutPath).length() / 1024)
                            val saveCompressPath = SFileUtils.getVideoDirectory() + System.currentTimeMillis() + "_compress" + SFileUtils.FileType.FILE_MP4
                            execCommand(saveCutPath, saveCompressPath)
                        }

                        override fun onLoadFail(reason: String) {

                        }
                    })
                }

                override fun cancelAction() {

                }

                override fun onError(message: String) {

                }
            })
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun cutCommand(inputVideo: String, outPutVideo: String, startTime: Long, endTime: Long) {
        val startTimeFormat = STimeUtils.getOverTimeString(startTime)
        val endTimeFormat = STimeUtils.getOverTimeString(endTime)

        val cmd = "-i $inputVideo -ss $startTimeFormat -to $endTimeFormat -acodec aac -ar 8000 -ac 2 -b:a 5k $outPutVideo"

        Logs.i("cmd:$cmd")
        mCompressor = Compressor(this@VideoEditActivity)
        mCompressor.loadBinary(object : InitListener {
            override fun onLoadSuccess() {
                mCompressor.execCommand(cmd, object : CompressListener {
                    override fun onExecSuccess(message: String) {

                        val saveCompressPath = SFileUtils.getVideoDirectory() + System.currentTimeMillis() + "_compress" + SFileUtils.FileType.FILE_MP4
                        execCommand(outPutVideo, saveCompressPath)
                    }

                    override fun onExecFail(reason: String) {
                        cancelLoading(true)
                        Logs.i("结束视频裁剪。。。。。。。。。$reason")
                    }

                    override fun onExecProgress(message: String) {
                        Logs.i("结束视频裁剪。。。。。。。。。$message")
                    }
                })
            }

            override fun onLoadFail(reason: String) {
                cancelLoading(true)
            }
        })

    }

    private fun cancelLoading(failure: Boolean) {
        if (loadingDialog != null) {
            loadingDialog!!.cancelLoading()
        }
        if (failure) {
            SUtils.makeToast(context, "处理视频失败")
        }
    }

    /**
     * 开始压缩
     *
     * @param currentInputVideoPath
     * @param currentOutputVideoPath
     */
    private fun execCommand(currentInputVideoPath: String, currentOutputVideoPath: String) {
        var measureWidth = 0
        var measureHeight = 0
        if (mVideoWidth > mVideoHeight) {
            measureWidth = 640
            measureHeight = 360
        } else {
            measureWidth = 360
            measureHeight = 640
        }
        val mesureSize = measureWidth.toString() + "x" + measureHeight
        Logs.i("scale:$mScale,,,,$measureWidth,,,,$measureHeight")
        val cmd = "-threads 2 -y -i " + currentInputVideoPath + " -strict -2 -vcodec libx264 -preset ultrafast " +
                "-crf 26 -acodec aac -ar 8000 -ac 2 -b:a 5k -s " + mesureSize + " " + currentOutputVideoPath
        /*    String cmd = "-threads 2 -y -i " + currentInputVideoPath + " -strict -2 -vcodec libx264 -preset ultrafast " +
                "-crf 28 -acodec aac -ar 44100 -ac 2 -b:a 5k -qscale 200 " + currentOutputVideoPath;*/
        Logs.i("视频压缩 :$cmd")
        val mFile = File(currentOutputVideoPath)
        if (mFile.exists()) {
            mFile.delete()
        }
        mCompressor.execCommand(cmd, object : CompressListener {
            override fun onExecSuccess(message: String) {
                SFileUtils.deleteFile(currentInputVideoPath)
                cancelLoading(false)
                val intent = Intent(TRIM_VIDEO)
                val items = ArrayList<ImageItem>()
                Logs.i("file::::" + File(currentOutputVideoPath).length() / 1024)
                videoItem!!.videoPath = currentOutputVideoPath
                items.add(videoItem!!)
                intent.putExtra(JumpTo.TYPE_OBJECT, items)
                context!!.sendBroadcast(intent)
                this@VideoEditActivity.finish()
            }

            override fun onExecFail(reason: String) {
                cancelLoading(true)
                Logs.i("结束视频压缩。。。。。。。。。$reason")
            }

            override fun onExecProgress(message: String) {
                Logs.i("结束视频压缩。。。。。。。。。$message")
            }
        })
    }

    private class MainHandler internal constructor(activity: VideoEditActivity) : Handler() {
        private val mActivity: WeakReference<VideoEditActivity>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity = mActivity.get()
            if (activity != null) {
                if (msg.what == ExtractFrameWorkThread.MSG_SAVE_SUCCESS) {
                    if (activity.videoEditAdapter != null) {
                        val info = msg.obj as VideoEditInfo
                        activity.videoEditAdapter!!.addItemVideoInfo(info)
                    }
                }
            }
        }
    }


    private fun videoStart() {
        Log.d(TAG, "----videoStart----->>>>>>>")
        mVideoView!!.start()
        positionIcon!!.clearAnimation()
        if (animator != null && animator!!.isRunning) {
            animator!!.cancel()
        }
        anim()
        handler.removeCallbacks(run)
        handler.post(run)
    }

    private fun videoProgressUpdate() {
        val currentPosition = mVideoView!!.currentPosition.toLong()
        Log.d(TAG, "----onProgressUpdate-cp---->>>>>>>$currentPosition")
        if (currentPosition >= rightProgress) {
            mVideoView!!.seekTo(leftProgress.toInt())
            positionIcon!!.clearAnimation()
            if (animator != null && animator!!.isRunning) {
                animator!!.cancel()
            }
            anim()
        }
    }

    private fun videoPause() {
        isSeeking = false
        if (mVideoView != null && mVideoView!!.isPlaying) {
            mVideoView!!.pause()
            handler.removeCallbacks(run)
        }
        Log.d(TAG, "----videoPause----->>>>>>>")
        if (positionIcon!!.visibility == View.VISIBLE) {
            positionIcon!!.visibility = View.GONE
        }
        positionIcon!!.clearAnimation()
        if (animator != null && animator!!.isRunning) {
            animator!!.cancel()
        }
    }


    override fun onResume() {
        super.onResume()
        if (mVideoView != null) {
            mVideoView!!.seekTo(leftProgress.toInt())
            //            videoStart();
        }
    }

    override fun onPause() {
        super.onPause()
        if (mVideoView != null && mVideoView!!.isPlaying) {
            videoPause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (animator != null) {
            animator!!.cancel()
        }
        if (mVideoView != null) {
            mVideoView!!.stop()
        }
        if (mExtractVideoInfoUtil != null) {
            mExtractVideoInfoUtil!!.release()
        }
        mRecyclerView!!.removeOnScrollListener(mOnScrollListener)
        if (mExtractFrameWorkThread != null) {
            mExtractFrameWorkThread!!.stopExtract()
        }
        mUIHandler.removeCallbacksAndMessages(null)
        handler.removeCallbacksAndMessages(null)
        if (!TextUtils.isEmpty(OutPutFileDirPath)) {
            PictureUtils.deleteFile(File(OutPutFileDirPath!!))
        }
    }

    companion object {
        private val TAG = VideoEditActivity::class.java.simpleName
        private val MIN_CUT_DURATION = 3 * 1000L// 最小剪辑时间3s
        private val MAX_CUT_DURATION = 115 * 1000L//视频最多剪切多长时间
        private val MAX_COUNT_RANGE = 10//seekBar的区域内一共有多少张图片
        val TRIM_VIDEO = "TRIM_VIDEO"
    }
}

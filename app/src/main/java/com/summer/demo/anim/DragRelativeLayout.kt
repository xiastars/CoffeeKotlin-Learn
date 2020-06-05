package com.summer.demo.anim

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Point
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.widget.RelativeLayout
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.summer.demo.R
import com.summer.demo.view.BaseDragView
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SThread
import com.summer.helper.utils.SUtils
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*

class DragRelativeLayout(context: Context) : BaseDragView(context), OnClickListener {
    /* 气泡文字 */
    var dialogtext: String? = null
    internal var mCreator: Context? = null
    internal var mMoveTipDialog: Dialog? = null
    /* 当前角色所属类型 */
    internal var mUserType = 1
    /* 是否可以切换数据 */
    internal var mChameleon = false
    /* 绑定的头部录音 */
    internal var mAnimTriggered = false
    /* 执行动作类型 */
    internal var mActionMode = -1
    /* 动画是不是循环类型 */
    internal var mCircleMode = false
    /* 走动X轴每次距离 */
    internal var mPerPaceX: Float = 0.toFloat()
    /* 走动Y轴每次距离 */
    internal var mPerPaceY: Float = 0.toFloat()
    internal var mPerTime: Int = 0
    internal var mMoveIndex = 0
    /* 预览模式 */
    internal var mPreviewMode = false
    /* 播放动画的当前帧index */
    internal var playIndex = 0
    /* 音频路径 */
    internal var mAudioPath: String? = null
    /* 默认背景 */
    internal var mDefaultIcon: String? = null
    /* 音频正在播放中 */
    internal var mAudioPlaying: Boolean = false
    /* 要进行的动作类型与URL */
    internal var mActionUrl: String? = null
    /* 结束时X轴 */
    internal var endX: Int = 0
    /* 默认情况下，不能拖动，只有选中了才能拖动 */
    internal var dragAble: Boolean = false
    /* 结束时Y轴 */
    internal var endY: Int = 0
    /* 音频正在录制中 */
    internal var mAudioRecording: Boolean = false
    /* 准备走动阶段 */
    var isPrepareMoving: Boolean = false
    /* Bitmap生成 index */
    /* 第一次走动提醒 */
    internal var bitmapIndex = 0
    /* 监听动作播放 */
    internal var onActionFinishListener: OnActionFinishListener? = null
    /* 第一个非透明的点 */
    internal var firstCP = Point()
    internal var mMeasured = false
    /* 带有图片可以缩放的 */
    private var rlPet: RelativeLayout? = null
    private var ivImage: ImageView? = null
    private val view1: View? = null
    private val view2: View? = null
    private val view3: View? = null
    private val view4: View? = null
    internal var bitmaps: MutableList<FrameImgBean>? = null

    private var mHandler: MyHandler? = null

    init {
        mCreator = context
        this.setOnClickListener(this)
        mHandler = MyHandler(this)
        mParams = this.layoutParams as FrameLayout.LayoutParams?
        if (null == mParams) {
            mParams = FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            layoutParams = mParams
        }
        addChildView()
    }

    private fun addChildView() {
        val view = LayoutInflater.from(context).inflate(R.layout.view_creator, null) as RelativeLayout
        rlPet = view.findViewById<View>(R.id.rl_pet) as RelativeLayout
        ivImage = view.findViewById<View>(R.id.iv_pet) as ImageView
        ivImage!!.scaleType = ScaleType.FIT_XY
        this.addView(view)
        //		disableClip(ivImage);
    }

    /*	private static void disableClip(View view) {
		if (view.getParent() instanceof View) {
			View g = (View) view.getParent();
			if (g != null && g instanceof ViewGroup) {
				ViewGroup v = (ViewGroup) g;
				v.setClipChildren(false);
				v.setClipToPadding(false);
				disableClip(v);
			}
		}
	}*/

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    /**
     * 当View处于选中状态时，设置四个点显示
     *
     * @param visible
     */
    fun showOrHideFocusedView(visible: Int) {
        view1!!.visibility = visible
        view2!!.visibility = visible
        view3!!.visibility = visible
        view4!!.visibility = visible
        if (visible == View.VISIBLE) {
            dragAble = true
        } else {
            dragAble = false
        }
        this.requestLayout()
        this.invalidate()
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // setScaleX(1);
                // setScaleY(1);
                Logs.i("DragReletiveLayout -- > ACTION_CANCEL")
                // mDragLayer.checkOnScaleButton((int)event.getX(),
                // (int)event.getY());
                performClick()
                if (!mIsPressed) {
                    mIsPressed = true
                    // ValueAnimator mScaleAnimation = ValueAnimator.ofFloat(0.85f,
                    // getScaleX());
                    // mScaleAnimation.addUpdateListener(new
                    // AnimatorUpdateListener() {
                    // public void onAnimationUpdate(ValueAnimator animation) {
                    // float value = ((Float)
                    // animation.getAnimatedValue()).floatValue();
                    // setScaleX(value);
                    // setScaleY(value);
                    // }
                    // });
                    // mScaleAnimation.setInterpolator(new
                    // OvershootInterpolator(1.2f));
                    // mScaleAnimation.setDuration(100);
                    // mScaleAnimation.start();
                }

                mLongPressHelper.postCheckForLongPress()
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                mIsPressed = false
                mLongPressHelper.cancelLongPress()
            }
        }
        return false
    }

    override fun cancelLongPress() {
        mIsPressed = false
        mLongPressHelper.cancelLongPress()
    }

    override fun setLayoutPosition(x: Int, y: Int) {
        // Logs.i("xia","重新X:"+x+",,,最后Y:"+y+",,播放模式:"+mCreator.getmPlayMode());
        //		if (!mCreator.isSingleOrEditMode()) {
        //			float scale = mCreator.getDragLayer().mPlayScale;
        //			x = (int) ((scale * (float) x));
        //			y = (int) (mCreator.getDragLayer().mPlayScaleY * (float) y);
        //		}
        if (null != mParams) {
            mParams!!.leftMargin = x
            mParams!!.topMargin = y
            requestLayout()
            invalidate()
        }
    }

    /**
     * 设置默认资源图片
     */
    fun setDefalultIcon() {
        Logs.i("xia", mDefaultIcon)
        if (!TextUtils.isEmpty(mDefaultIcon)) {
            try {
                SUtils.setPic(ivImage, mDefaultIcon, true, object : SimpleTarget<ImageView>() {

                    override fun onResourceReady(arg0: ImageView?, arg1: GlideAnimation<in ImageView>) {
                        if (arg0 != null && arg0 is GlideBitmapDrawable) {
                            val bitmap = arg0.bitmap
                            if (!bitmap.isRecycled) {
                                ivImage!!.setImageBitmap(bitmap)
                                invalidate()
                                requestLayout()
                                val time = System.currentTimeMillis()
                                if (firstCP.x == 0 && firstCP.y == 0) {
                                    for (i in 0 until bitmap.width) {
                                        if (firstCP.x != 0) {
                                            break
                                        }
                                        for (j in 0 until bitmap.height) {
                                            val color = bitmap.getPixel(i, j)
                                            if (color != 0) {
                                                firstCP.x = i
                                                break
                                            }
                                        }
                                    }
                                    for (i in 0 until bitmap.height) {
                                        for (j in 0 until bitmap.width) {
                                            val color = bitmap.getPixel(j, i)
                                            if (color != 0) {
                                                firstCP.y = i
                                                return
                                            }
                                        }
                                    }
                                }
                                Logs.i("xia", "TIME:" + (System.currentTimeMillis() - time))
                            }

                        }
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    @SuppressLint("NewApi")
    private fun setPhotoIndex() {
        if (null == bitmaps || bitmaps!!.size < playIndex) {
            return
        }
        Logs.i("index:::$playIndex")
        initSycnBitmap()
        val bitmap = bitmaps!![playIndex].bitmap ?: return
        bitmaps!![playIndex].createTime = System.currentTimeMillis()
        if (bitmap != null && !bitmap.isRecycled) {
            try {
                ivImage!!.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun initSycnBitmap() {
        if (null == bitmaps) {
            return
        }
        val size = bitmaps!!.size
        bitmapIndex++
        //如果超出，则从0开始
        if (bitmapIndex >= size) {
            bitmapIndex = 0
        }
        SThread.getIntances().submit(Runnable {
            if (playIndex == 0) {
                return@Runnable
            }

            for (i in bitmaps!!.indices) {
                val lastFrameImgBean = bitmaps!![i]
                if (System.currentTimeMillis() - lastFrameImgBean.createTime > 500 && lastFrameImgBean.createTime != 0L) {
                    val b = lastFrameImgBean.bitmap
                    if (b != null) {
                        b.recycle()
                    }
                }
            }
            try {
                val frameImgBean = bitmaps!![bitmapIndex]
                val type = frameImgBean.imgType
                if (frameImgBean.bitmap != null) {
                    return@Runnable
                }
                if (type == 2) {
                    //读取asset里的文件
                    val stream = mCreator!!.assets.open(frameImgBean.imgName)
                    val OPTIONS_DECODE = BitmapFactory.Options()
                    OPTIONS_DECODE.inSampleSize = 2
                    val bitmap = BitmapFactory.decodeStream(stream, null, OPTIONS_DECODE)
                    frameImgBean.bitmap = bitmap
                    frameImgBean.createTime = System.currentTimeMillis()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
    }

    /**
     * 循环播放动画
     */
    fun circlePlay() {
        /* 如果退出本类，则停止 */
        if ((mCreator as Activity).isFinishing) {
            return
        }
        mCircleMode = true

        if (null == bitmaps) {
            if (null != onActionFinishListener) {
                onActionFinishListener!!.finish()
            }
            return
        }
        playIndex++
        if (playIndex > bitmaps!!.size) {
            //			stopCicelMode();
            return
        }
        /* 判断结束动画 */
        if (bitmaps!!.size == playIndex) {
            mAnimTriggered = false
            playIndex = 0
            if (mCircleMode) {
                mHandler!!.sendEmptyMessageDelayed(0, CommomData.PLAY_TIME.toLong())
            } else {
                // setDefalultIcon();

                setDefalultIcon()
                afterPlayEnd()
            }
            return
        }

        mHandler!!.sendEmptyMessageDelayed(0, CommomData.PLAY_TIME.toLong())
    }

    private fun startCirclePlay() {
        circlePlay()
    }

    /**
     * 预先初始化
     *
     * @param bitmaps
     */
    fun initBitmaps(bitmaps: MutableList<FrameImgBean>) {
        this.bitmaps = bitmaps
        val size = bitmaps.size
        val index = if (size > 5) 5 else size
        val time = System.currentTimeMillis()
        for (i in 0 until index) {
            try {
                val frameImgBean = bitmaps[i]
                val type = frameImgBean.imgType
                if (type == 2) {
                    //读取asset里的文件
                    val stream = mCreator!!.assets.open(frameImgBean.imgName)
                    val OPTIONS_DECODE = BitmapFactory.Options()
                    OPTIONS_DECODE.inSampleSize = 2
                    val bitmap = BitmapFactory.decodeStream(stream, null, OPTIONS_DECODE)
                    frameImgBean.bitmap = bitmap
                    frameImgBean.createTime = System.currentTimeMillis()
                    Logs.i("bitmap:" + bitmap!!)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        bitmapIndex = index - 1
        Logs.t(time)
    }

    private fun setCancelDialog() {
        if (null != mMoveTipDialog) {
            mMoveTipDialog!!.cancel()
            mMoveTipDialog = null
        }
    }


    /**
     * 结束动画，走动和音乐播放后
     */
    private fun afterPlayEnd() {
        // 关闭循环模式
        mCircleMode = false
        // 关闭Draglayer里的动画播放限制人物操作
        Logs.i("xia", "结束动作")
        // 动画指针移动首位
        playIndex = 0
        // 音乐播放状态还原
        mAudioPlaying = false
        mPreviewMode = false
        mActionMode = -1
        mAnimTriggered = false
        dialogtext = null
        mAudioPath = null
        mActionUrl = null
        /* 切换时，必须等到当前帧播完再回调 */
        Handler().postDelayed({
            removeAllBitmap()
            if (null != onActionFinishListener) {
                onActionFinishListener!!.finish()
            }
        }, CommomData.PLAY_TIME.toLong())
    }

    fun stopPlay() {
        Logs.i("stopPlay::::check")
        mHandler!!.removeMessages(0)
        afterPlayEnd()
    }


    @SuppressLint("NewApi")
    private fun removeTransaction() {
        val left = (viewLeft + translationX).toInt()
        val top = (viewTop + translationY).toInt()
        translationX = 0f
        translationY = 0f
        if (null != mParams) {
            mParams!!.leftMargin = left
            mParams!!.topMargin = top
            requestLayout()
            invalidate()
        }
    }

    private fun retriveActionBitmap(actionname: String?): Array<String>? {
        if (null == actionname)
            return null
        var actioninfo = ""
        var picinfo: Array<String>? = null
        if (actionname.contains("/")) {
            actioninfo = actionname.subSequence(actionname.lastIndexOf("/") + 1, actionname.lastIndexOf(".")) as String
            Logs.i("actionInfo:$actioninfo")
            picinfo = actioninfo.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            Logs.i(Arrays.toString(picinfo))
            mAudioPlaying = false
            return picinfo
        }
        return null
    }

    override fun onClick(v: View) {

    }

    private fun removeAllBitmap() {
        if (null != bitmaps) {

            for (img in bitmaps!!) {
                var bitmap: Bitmap? = img.bitmap
                if (null != bitmap) {
                    bitmap.recycle()
                    bitmap = null
                }
            }
            bitmaps!!.clear()
        }
    }

    override fun getLayoutParams(): android.view.ViewGroup.LayoutParams? {
        return mParams
    }

    interface OnActionFinishListener {
        fun finish()
    }

    private interface OnGetBitmapListener {
        fun onSucceed()

        fun onFailure()
    }

    private class MyHandler(activity: DragRelativeLayout) : Handler() {
        private val mCreator: WeakReference<DragRelativeLayout>

        init {
            mCreator = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val view = mCreator.get()
            if (null != view) {
                when (msg.what) {
                    0 -> {
                        view.setPhotoIndex()
                        view.circlePlay()
                    }
                    1 -> {
                    }
                    2 -> view.startCirclePlay()
                    3 -> {
                    }
                    4 -> {
                    }
                    5 -> {
                    }
                    6 -> view.circlePlay()
                    7 -> {
                    }
                }
            }
        }
    }

    companion object {

        /**
         * 文字类型
         */
        val ACTION_NAME_TEXT = "文字类型__"
    }

}

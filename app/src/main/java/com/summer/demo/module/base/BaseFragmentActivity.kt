package com.summer.demo.module.base

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.summer.demo.R
import com.summer.demo.constant.ApiConstants
import com.summer.demo.helper.PlayAudioHelper
import com.summer.demo.module.base.swipe.SwipeBackActivity
import com.summer.helper.recycle.MaterialRefreshLayout
import com.summer.helper.server.SummerParameter
import com.summer.helper.utils.*
import com.summer.helper.view.NRecycleView
import com.summer.helper.view.review.RRelativeLayout
import com.summer.helper.web.ActivitysManager
import java.lang.ref.WeakReference
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by xiaqiliang on 2017/3/24.
 */
abstract class BaseFragmentActivity : SwipeBackActivity() {
    internal var tvTitle: TextView? = null
    internal var viewBack: View? = null
    internal var line1: View? = null
    internal lateinit var rlBaseTop: RelativeLayout
    internal var rlTitle: RRelativeLayout? = null
    protected lateinit var flContainer: FrameLayout
    var context: Context? = null
    protected lateinit var myHandlder: MyHandler
    var pageIndex: Int = 0
    internal var isRefresh: Boolean = false
    protected var isStop: Boolean = false

    internal var scrollView: MaterialRefreshLayout? = null

    var baseHelper: BaseHelper? = null
    internal var receiverUtils: ReceiverUtils? = null
    internal var resources: Resources? = null

    internal var isEmptyViewShowing: Boolean = false
    internal var isActivityFinished: Boolean = false

    val titleParent: RelativeLayout?
        get() = rlTitle
    //OnShareListener listener;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        SUtils.initScreenDisplayMetrics(this)
        ActivitysManager.Add(this.javaClass.simpleName, this)
        myHandlder = MyHandler(this)
        baseHelper = BaseHelper(context!!, myHandlder)
        baseHelper!!.setMIUIStatusBarDarkMode(this)
        setContentView(R.layout.activity_base)
        initTitleView()
        checkView()

    }


    class Bind<V>(val id: Int) : ReadOnlyProperty<Activity, V> {
        override fun getValue(thisRef: Activity, property: KProperty<*>): V {
            var view :View = thisRef.findViewById(id)
            return view as V
        }
    }

    protected fun checkView() {
        /*    if (!SUtils.isNetworkAvailable(context)) {
            View view = LayoutInflater.from(this).inflate(R.layout.view_network_broken, null);
            flContainer.addView(view);
            TextView tvReload = view.findViewById(R.id.tv_reload);
            tvReload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    flContainer.removeAllViews();
                    checkView();
                }
            });
        } else {

        }*/
        initView()
        initData()
    }

    override fun setTitle(title: CharSequence) {
        tvTitle!!.text = title
    }

    @JvmOverloads
    fun showEmptyView(content: String = "") {
        isEmptyViewShowing = true
        flContainer.removeAllViews()
        val view = LayoutInflater.from(this).inflate(R.layout.view_empty, null)
        val tvEmpty = view.findViewById<View>(R.id.tv_hint_content) as TextView
        if (!TextUtils.isEmpty(content)) {
            tvEmpty.text = content
        }
        flContainer.addView(view)
    }

    fun stripEmptyView() {
        if (isEmptyViewShowing) {
            flContainer.removeAllViews()
            initView()
        }
    }

    protected fun setBlankTitleView() {
        setBlankTitleView(true)
    }

    protected fun setBlankTitleView(isTopMargin: Boolean): View {
        removeTitle()
        val view = LayoutInflater.from(context).inflate(R.layout.include_back, null) as RRelativeLayout
        val viewBack = view.findViewById<View>(R.id.view_back) as ImageView
        flContainer.addView(view)
        SViewUtils.setViewHeight(view, 49)
        setLayoutFullscreen()
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.topMargin = SUtils.getStatusBarHeight(this) + if (isTopMargin) SUtils.getDip(context!!, 20) else 0
        view.findViewById<View>(R.id.ll_back).setOnClickListener { onBackClick() }
        return view
    }

    /*    */

    /**
     * 初始化分享按钮
     *//*
    protected void initShareButton(final OnShareListener listener) {
        this.listener = listener;
        Button btnShare = (Button) findViewById(R.id.btn_share);
        btnShare.setVisibility(View.VISIBLE);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onShare();
                }
            }
        });
    }*/
    protected fun hideShareButton() {
        val btnShare = findViewById(R.id.btn_share) as Button
        btnShare.visibility = View.GONE
    }

    @TargetApi(Build.VERSION_CODES.M)
    open fun changeHeaderStyleTrans(color: Int) {
        if (rlTitle != null) {
            rlTitle!!.setBackgroundColor(context!!.resources.getColor(R.color.white))
        }
        if (viewBack != null) {
            //viewBack.setBackgroundResource(R.drawable.title_icon_return_black);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = color
        }
    }

    /**
     * 更改View状态
     *
     * @param res
     * @return
     */
    protected fun changeTitleView(res: Int): View {
        rlTitle = findViewById(R.id.title) as RRelativeLayout
        rlTitle!!.removeAllViews()
        val view = LayoutInflater.from(context).inflate(res, null)
        rlTitle!!.addView(view)
        return view
    }

    private fun initTitleView() {
        rlBaseTop = findViewById(R.id.rl_base_parent) as RelativeLayout
        tvTitle = findViewById(R.id.tv_title) as TextView
        if (setTitleId() != 0 && tvTitle != null) {
            tvTitle!!.text = getString(setTitleId())
        }
        rlTitle = findViewById(R.id.title) as RRelativeLayout
        flContainer = findViewById(R.id.fl_container) as FrameLayout
        val llBack = findViewById(R.id.ll_back) as LinearLayout
        viewBack = findViewById(R.id.view_back)
        SUtils.clickTransColor(llBack)
        line1 = findViewById(R.id.line1)
        llBack.setOnClickListener {
            Logs.i("-----------back")
            onBackClick()
        }
    }

    protected open fun onBackClick() {
        //. CUtils.onClick(getClass().getSimpleName() + "_onback");
        PlayAudioHelper.instance.stopPlayingAudio()
        this@BaseFragmentActivity.finish()
    }

    protected fun initBroadcast(vararg action: String) {
        if (receiverUtils != null) {
            receiverUtils!!.unRegisterReceiver()
        }
        receiverUtils = ReceiverUtils(this)
        receiverUtils!!.setActionsAndRegister(*action)
        receiverUtils!!.setOnReceiverListener(ReceiverUtils.ReceiverListener { action, intent ->
            if (context == null) {
                return@ReceiverListener
            }
            onMsgReceiver(action, intent)
        })
    }

    protected fun onMsgReceiver(type: String, intent: Intent) {

    }

    private fun initView() {
        if (setContentView() != 0) {
            val view = LayoutInflater.from(this).inflate(setContentView(), null)
            flContainer.addView(view)
        }
    }

    fun requestData(className: Class<*>, params: SummerParameter, url: String, post: Boolean) {
        requestData(0, className, params, url, post)
    }

    @JvmOverloads
    fun requestData(requestCode: Int, className: Class<*>, params: SummerParameter, url: String, post: Boolean, isArray: Boolean = false) {
        baseHelper!!.setIsRefresh(isRefresh)
        baseHelper!!.requestData(requestCode, 0, className, params, url, post, isArray)
    }

    fun requestData(requestCode: Int, limiteTime: Int, className: Class<*>, params: SummerParameter, url: String, post: Boolean) {
        baseHelper!!.setIsRefresh(isRefresh)
        baseHelper!!.requestData(requestCode, limiteTime, className, params, url, post)
    }

    fun requestData(requestCode: Int, limiteTime: Int, className: Class<*>, params: SummerParameter, url: String, post: Boolean, isArray: Boolean) {
        baseHelper!!.setIsRefresh(isRefresh)
        baseHelper!!.requestData(requestCode, limiteTime, className, params, url, post, isArray)
    }

    /**
     * 1.2接口put数据
     *
     * @param requestCode
     * @param className
     * @param params
     * @param url
     */
    fun getDataTwo(requestCode: Int, className: Class<*>, params: SummerParameter, url: String) {
        if (baseHelper == null) {
            return
        }
        baseHelper!!.requestData(requestCode, ApiConstants.hostVersion2, 0, className, params, url, 0, false)
    }

    /**
     * 1.2接口put数据
     *
     * @param requestCode
     * @param className
     * @param params
     * @param url
     */
    fun putDataTwo(requestCode: Int, className: Class<*>, params: SummerParameter, url: String) {
        if (baseHelper == null) {
            return
        }
        baseHelper!!.putData(requestCode, ApiConstants.hostVersion2, className, params, url)
    }

    /**
     * 1.2接口delete数据
     *
     * @param requestCode
     * @param className
     * @param params
     * @param url
     */
    fun deleteDataTwo(requestCode: Int, className: Class<*>, params: SummerParameter, url: String) {
        if (baseHelper == null) {
            return
        }

        baseHelper!!.deleteData(requestCode, ApiConstants.hostVersion2, className, params, url)
    }


    /**
     * 1.2接口get数据
     *
     * @param requestCode
     * @param className
     * @param params
     * @param url
     */
    fun postDataTwo(requestCode: Int, className: Class<*>, params: SummerParameter, url: String) {
        if (baseHelper == null) {
            return
        }
        baseHelper!!.requestData(requestCode, ApiConstants.hostVersion2, 0, className, params, url, 1, false)
    }

    /**
     * 设置沉浸式状态栏
     */
    fun setLayoutFullscreen() {
        baseHelper!!.setLayoutFullscreen(false)
    }


    /**
     * 设置沉浸式状态栏
     */
    fun setLayoutFullscreen(fullscreen: Boolean) {
        baseHelper!!.setLayoutFullscreen(fullscreen)
    }

    class MyHandler(activity: BaseFragmentActivity) : Handler() {
        private val mActivity: WeakReference<BaseFragmentActivity>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity = mActivity.get()
            if (null != activity) {
                when (msg.what) {
                    BaseHelper.MSG_SUCCEED -> {
                        activity.handleData(msg.arg1, msg.obj)
                        activity.cancelLoading()
                    }
                    BaseHelper.MSG_FINISHLOAD -> activity.finishLoad()
                    BaseHelper.MSG_CACHE -> activity.handleData(msg.arg1, msg.obj)
                    BaseHelper.MSG_ERRO -> {
                        Logs.i("requestCode:" + msg.arg1 + ",,," + msg.arg2)
                        activity.dealErrors(msg.arg1, msg.arg2.toString() + "", msg.obj as String, false)
                        activity.finishLoad()
                    }
                    else -> activity.handleMsg(msg.what, msg.obj)
                }
            }
        }
    }

    fun handleMsg(position: Int, `object`: Any) {}

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        //No call for super(). Bug on API Level > 11.
    }

    private fun handleData(requestCode: Int, `object`: Any) {
        if (this@BaseFragmentActivity.isFinishing) {
            return
        }
        dealDatas(requestCode, `object`)
    }

    override fun onStop() {
        super.onStop()
        if (baseHelper != null) {
            baseHelper!!.cancelLoading()
        }
        isStop = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (receiverUtils != null) {
            receiverUtils!!.unRegisterReceiver()
        }
        BitmapUtils.getInstance().clearBitmaps(javaClass.simpleName)
        context = null
    }

    override fun onResume() {
        super.onResume()
        isStop = false
        /*    MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getName());*/
        //CUtils.onClick(getClass().getSimpleName() + "_resume");
    }

    override fun onPause() {
        super.onPause()
        /*      MobclickAgent.onPause(this);
        CUtils.onClick(getClass().getSimpleName() + "_pause");
        MobclickAgent.onPageEnd(this.getClass().getName());*/
        if (tvTitle != null) {
            SUtils.hideSoftInpuFromWindow(tvTitle)
        }
    }

    /**
     * 获取颜色资源
     *
     * @param coloRes
     * @return
     */
    open fun getResColor(coloRes: Int): Int {
        if (resources == null) {
            resources = context!!.resources
        }
        return resources!!.getColor(coloRes)
    }

    /**
     * 获取颜色资源
     *
     * @param coloRes
     * @return
     */
    fun getResDrawable(coloRes: Int): Drawable {
        if (resources == null) {
            resources = context!!.resources
        }
        return resources!!.getDrawable(coloRes)
    }

    /**
     * 处理错误
     *
     * @param requstCode  请求数据标识码
     * @param requestType 返回错误码，根据requestCode判断是返回的Code还是@ErroCode
     * @param errString   错误信息
     * @param requestCode 如果是返回的Code则为true
     */
    protected fun dealErrors(requstCode: Int, requestType: String, errString: String, requestCode: Boolean) {
        if (baseHelper != null) {
            baseHelper!!.cancelLoading()
            finishLoad()
        }
    }

    private fun cancelLoading() {
        baseHelper!!.cancelLoading()
    }

    protected abstract fun loadData()

    /**
     * 分页加载时结束加载
     */
    protected abstract fun finishLoad()

    /**
     * 处理返回的数据
     */
    protected abstract fun dealDatas(requestCode: Int, obj: Any)

    /**
     * 设置当前界面标题
     */
    protected abstract fun setTitleId(): Int

    /**
     * 设置当前界面主体内容
     */
    protected abstract fun setContentView(): Int

    /**
     * 初始化界面与数据
     */
    protected abstract fun initData()


    protected fun setTitleString(titleStr: String) {
        if (tvTitle != null)
            tvTitle!!.text = titleStr
    }

    protected fun setScrollView(layout: MaterialRefreshLayout) {
        this.scrollView = layout
    }

    fun handleViewData(obj: Any, nRecycleView: NRecycleView) {
        baseHelper!!.handleViewData(obj, nRecycleView, scrollView, pageIndex)
    }

    fun setLoadMore(loadMore: Boolean) {
        if (scrollView != null) {
            scrollView!!.isPullUpRefreshable = loadMore
        }
    }


    /**
     * 如果不想要默认的头部，则移除，自己在Activity里写
     */
    fun removeTitle() {
        rlBaseTop.removeView(rlTitle)
        //changeHeaderStyleTrans(getResColor(R.color.half_greye1));
        line1!!.visibility = View.GONE
    }
}

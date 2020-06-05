package com.summer.demo.module.base

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.annotation.RequiresApi
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.summer.demo.R
import com.summer.demo.bean.BaseResp
import com.summer.demo.constant.ApiConstants
import com.summer.demo.listener.OnShareListener
import com.summer.demo.module.base.swipe.SwipeBackActivity
import com.summer.demo.utils.CUtils
import com.summer.demo.view.CommonSureView5
import com.summer.demo.view.LoadingDialog
import com.summer.helper.server.SummerParameter
import com.summer.helper.utils.BitmapUtils
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SUtils
import com.summer.helper.view.review.RRelativeLayout
import com.summer.helper.web.ActivitysManager
import java.lang.ref.WeakReference
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by xiaqiliang on 2017/3/24.
 */
abstract class BaseRequestActivity : SwipeBackActivity() ,View.OnClickListener {

    protected var TAG = "fsxq"
    internal var tvTitle: TextView? = null
    var flContainer: FrameLayout? = null
    internal var rlBaseTop: RelativeLayout? = null
    internal var rlTitle: RRelativeLayout? = null
    internal var viewBack: View? = null
    var context: Context? = null
    protected lateinit var myHandlder: MyHandler
    var pageIndex: Int = 0
    protected var fromId: Long = 0
    var lastId: String? = null
    protected var toastMessage = false//是否弹出错误信息
    var isReturnFailureMsg: Boolean = false//请求错误状态下是否返回
    internal var isRefresh: Boolean = false
    var baseHelper: BaseHelper? = null
    internal var listener: OnShareListener? = null
    internal var line1: View? = null
    internal var resources: Resources? = null
    lateinit var rightTv: TextView
    protected var llBack: LinearLayout?= null

    protected var BACK_TAG = "Other"
    protected var DEFAULT_LIMIT = 10

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        ActivitysManager.Add(this.javaClass.simpleName, this)
        SUtils.initScreenDisplayMetrics(this)
        myHandlder = MyHandler(this)
        baseHelper = BaseHelper(context!!, myHandlder)
        setContentView(R.layout.activity_base)

        initTitleView()
        checkView()
    }

    class Bind<V>(val id: Int,val shouldClick:Boolean = false) : ReadOnlyProperty<BaseRequestActivity, V> {
        override fun getValue(thisRef: BaseRequestActivity, property: KProperty<*>): V {
            var view :View = thisRef.findViewById(id)
            if(shouldClick){
                view.setOnClickListener(thisRef)
            }
            return view as V
        }
    }

    /**
     * @param backTag
     */
    fun setBackTag(backTag: String) {
        BACK_TAG = backTag
    }

    private fun checkView() {
        baseHelper!!.setMIUIStatusBarDarkMode(this)
        if (!SUtils.isNetworkAvailable(context!!)) {
            /*  View view = LayoutInflater.from(this).inflate(R.layout.view_network_broken, null);
            flContainer.addView(view);*/
        } else {

        }
        initContentView()
        initPresenter()
        initData()

    }

    /**
     * 自定义错误
     * @param iconRes
     * @param content
     * @param tryContent
     * @param listener
     */
    fun showErrorView(iconRes: Int, content: String, tryContent: String, listener: View.OnClickListener) {
        flContainer!!.removeAllViews()
        val view = LayoutInflater.from(this).inflate(R.layout.view_network_broken, null)
        val ivError = view.findViewById<ImageView>(R.id.iv_nav)
        SUtils.setPicResource(ivError, iconRes)
        val tvContent = view.findViewById<TextView>(R.id.tv_hint_content)
        tvContent.text = content
        val tvTry = view.findViewById<TextView>(R.id.tv_reload)
        tvTry.text = tryContent
        flContainer!!.addView(view)
        val tvReload = view.findViewById<TextView>(R.id.tv_reload)
        tvReload.setOnClickListener(listener)
    }

    /**
     * 初始化分享按钮
     */
    protected fun initShareButton(res: Int, onShareListener: OnShareListener?) {
        val btnShare = findViewById(R.id.btn_share) as Button
        btnShare.visibility = View.VISIBLE
        btnShare.setBackgroundResource(res)
        btnShare.setOnClickListener {
            onShareListener?.onShare()
            Logs.i("onshare:::::")
        }
    }

    /**
     * 初始化分享按钮
     */
    protected fun initShareButton(onShareListener: OnShareListener) {
        initShareButton(R.drawable.topic_share_top, onShareListener)
    }

    protected fun showRightView(content: String, listener: View.OnClickListener) {
        rightTv.visibility = View.VISIBLE
        rightTv.text = content
        rightTv.setOnClickListener(listener)
    }

    protected fun showBlueRightView(content: String, listener: View.OnClickListener) {
        val sureView = findViewById(R.id.btn_right) as CommonSureView5
        sureView.changeStyle(true)
        sureView.textSize = 12f
        sureView.setPadding(SUtils.getDip(context!!, 10), 0, SUtils.getDip(context!!, 10), 0)
        sureView.visibility = View.VISIBLE
        sureView.text = content
        sureView.setOnClickListener(listener)
    }
    /*
     */
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

    /*    protected void setShareListener(OnShareListener listener) {
        this.listener = listener;
    }*/

    /**
     * 如果不想要默认的头部，则移除，自己在Activity里写
     */
    fun removeTitle() {
        rlTitle!!.visibility = View.GONE
        line1!!.visibility = View.GONE
    }

    /*  protected void setBlankTitleView() {
        setBlankTitleView(true);
    }*/

    /*
    protected View setBlankTitleView(boolean isTopMargin) {
        removeTitle();
        RRelativeLayout view = (RRelativeLayout) LayoutInflater.from(context).inflate(R.layout.include_back, null);
        ImageView viewBack = (ImageView) view.findViewById(R.id.view_back);
        viewBack.setBackgroundResource(R.drawable.title_icon_return_default);
        flContainer.addView(view);
        SViewUtils.setViewHeight(view, 49);
        setLayoutFullscreen();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.topMargin = SUtils.getStatusBarHeight(this) + (isTopMargin ? SUtils.getDip(context, 20) : 0);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClick();
            }
        });
        return view;
    }
*/


    /*    */

    /**
     * 当此界面有EditText时，Full模式下不弹出 ，所以不能设为Full模式
     *//*
    protected void setBlankTitleViewWithEditMode() {
        removeTitle();
        RLinearLayout view = (RLinearLayout) LayoutInflater.from(context).inflate(R.layout.include_back, null);
        TextView viewBack = (TextView) view.findViewById(R.id.view_back);
        viewBack.setBackgroundResource(R.drawable.title_icon_return_default);
        flContainer.addView(view);
        view.reLayout(55, 45, 0, 0);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.topMargin = SUtils.getDip(context, 20);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClick();
            }
        });
    }*/
    private fun initTitleView() {
        tvTitle = findViewById(R.id.tv_title) as TextView
        if (setTitleId() != 0) {
            tvTitle!!.text = getString(setTitleId())
        }
        rlBaseTop = findViewById(R.id.rl_base_parent) as RelativeLayout
        rlTitle = findViewById(R.id.title) as RRelativeLayout
        flContainer = findViewById(R.id.fl_container) as FrameLayout
        llBack = findViewById(R.id.ll_back) as LinearLayout
        SUtils.clickTransColor(llBack)
        viewBack = findViewById(R.id.view_back)
        llBack!!.setOnClickListener { onBackClick() }
        line1 = findViewById(R.id.line1)
        rightTv = findViewById(R.id.tv_edit) as TextView
    }

    fun changeViewBackRes(res: Int) {
        if (viewBack == null) {
            return
        }
        viewBack!!.setBackgroundResource(res)
    }

    /**
     * 隐藏返回键
     */
    fun hideViewBack() {
        viewBack!!.visibility = View.GONE
    }

    override fun setTitle(title: CharSequence) {
        tvTitle!!.text = title
    }

    override fun setTitle(titleId: Int) {
        if (titleId != 0)
            title = getString(titleId)
    }

    protected fun onBackClick() {

        CUtils.onClick(context!!, javaClass.simpleName + "_back")
        this@BaseRequestActivity.finish()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            CUtils.onClick(context!!, BACK_TAG + "_back")
        }
        return super.onKeyUp(keyCode, event)
    }

    fun initContentView() {
        if (setContentView() != 0) {
            val view = LayoutInflater.from(this).inflate(setContentView(), null)
            flContainer!!.addView(view)
        }
    }

    fun addView(view: View) {
        flContainer!!.addView(view)
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun changeHeaderStyleTrans(color: Int) {
        if (rlTitle != null) {
            rlTitle!!.setBackgroundColor(context!!.resources.getColor(R.color.white))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                window.statusBarColor = color
            } catch (e: NoSuchElementException) {
                e.printStackTrace()
            }

        }
    }

    fun requestData(className: Class<*>, params: SummerParameter, url: String, post: Boolean) {
        baseHelper!!.setIsRefresh(isRefresh)
        baseHelper!!.requestData(0, className, params, url, post)
    }

    fun putData(requestType: Int, className: Class<*>, params: SummerParameter, url: String) {
        baseHelper!!.setIsRefresh(isRefresh)
        baseHelper!!.putData(requestType, className, params, url)
    }

    @JvmOverloads
    fun requestData(requestCode: Int, className: Class<*>, params: SummerParameter, url: String, post: Boolean, isArray: Boolean = false) {
        baseHelper!!.setIsRefresh(isRefresh)
        baseHelper!!.requestData(requestCode, 0, className, params, url, post, isArray)
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
        baseHelper!!.requestData(requestCode, ApiConstants.hostVersion2, 300, className, params, url, 0, false)
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


    override fun onStop() {
        super.onStop()
        /*  if (baseHelper != null) {
            baseHelper.cancelLoading();
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        context = null
        BitmapUtils.getInstance().clearBitmaps(javaClass.simpleName)
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
    fun setLayoutFullscreen(show: Boolean) {
        baseHelper!!.setLayoutFullscreen(show)
    }


    /**
     * 设置沉浸式状态栏
     *//*
    public void setLayoutFullscreen(boolean fullscreen) {
        baseHelper.setLayoutFullscreen(fullscreen);
    }*/

    class MyHandler(activity: BaseRequestActivity) : Handler() {
        private val mActivity: WeakReference<BaseRequestActivity> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            val activity = mActivity.get()
            if (null != activity) {
                when (msg.what) {
                    BaseHelper.MSG_SUCCEED -> activity.handleRequest(msg, false)
                    BaseHelper.MSG_FINISHLOAD -> {
                        activity.cancelBaseLoading()
                        activity.finishLoad()
                    }
                    BaseHelper.MSG_CACHE -> activity.handleRequest(msg, true)
                    BaseHelper.MSG_ERRO -> {
                        Logs.i("requestCode:" + msg.arg1 + ",,," + msg.arg2)
                        activity.dealErrors(msg.arg1, msg.arg2.toString() + "", msg.obj as String, if (msg.arg2 > 0) true else false)
                        activity.finishLoad()
                    }
                    BaseHelper.MSG_LOADING -> {
                        val content = msg.obj as String
                        activity.startLoading(content)
                    }
                    BaseHelper.MSG_CANCEL_LOADING -> activity.cancelBaseLoading()
                    else -> activity.handleMsg(msg.what, msg.obj)
                }
            }
        }
    }

    private fun startLoading(content: String) {
        LoadingDialog.cancelDialogForLoading()
        LoadingDialog.showDialogForLoading(this, content, true)
    }

    fun cancelLoading() {
        myHandlder.sendEmptyMessage(BaseHelper.MSG_CANCEL_LOADING)
    }

    fun showLoading() {
        myHandlder.obtainMessage(BaseHelper.MSG_LOADING, "正在加载中...").sendToTarget()
    }

    fun showLoading(content: String) {
        myHandlder.obtainMessage(BaseHelper.MSG_LOADING, content).sendToTarget()
    }

    fun showOtherViewFromEmpty(resLayout: Int) {
        flContainer!!.removeAllViews()
        val view = LayoutInflater.from(this).inflate(R.layout.view_empty, null)
        flContainer!!.addView(view)
    }

    /**
     * 处理MyHandler派发的消息
     *
     * @param position
     * @param object
     */
    fun handleMsg(position: Int, `object`: Any) {

    }

    /**
     * 返回数据给请求Activity
     *
     * @param msg
     * @param fromCache
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun handleRequest(msg: Message, fromCache: Boolean) {
        if (context == null) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                if (isDestroyed) {
                    return
                }
            } catch (e: NoSuchElementException) {
                e.printStackTrace()
            }

        }

        val `object` = msg.obj ?: return
        if (`object` is BaseResp) {
            val resp = `object`
            if (toastMessage && !isReturnFailureMsg) {
                //new CodeRespondUtils(context, resp);
            } else {
            }
            dealDatas(msg.arg1, msg.obj)
        }
        dealDatas(msg.arg1, msg.obj)
        if (fromCache) {
            fromId = 0
            pageIndex = 0
        }
    }

    /**
     * 处理错误
     *
     * @param requstCode  请求数据标识码
     * @param requestType 返回错误码，根据requestCode判断是返回的Code还是@ErroCode
     * @param errString   错误信息
     * @param isCode      如果是返回的Code则为true
     */
    protected fun dealErrors(requstCode: Int, requestType: String, errString: String, isCode: Boolean) {
        if (baseHelper != null) {
            baseHelper!!.cancelLoading()
            finishLoad()
        }
    }

    private fun cancelBaseLoading() {
        if (baseHelper != null) {
            baseHelper!!.cancelLoading()
        }
        LoadingDialog.cancelDialogForLoading()
    }

    /**
     * 显示空状态页面
     *
     * @param msg  显示文本信息
     * @param resp
     */
    protected fun showEmptyView(msg: String, resp: Int) {
        if (context == null) {
            return
        }
        flContainer!!.removeAllViews()
        val view = LayoutInflater.from(context).inflate(R.layout.view_empty, null)
        view.setOnClickListener {
            /*    if (baseHelper != null) {
                    baseHelper.setShowLoading(true);
                }*/
            //requestRankData();
        }
        flContainer!!.addView(view)
        val ivNav = view.findViewById<ImageView>(R.id.iv_nav)
        SUtils.setPicResource(ivNav, resp)

        val tvContent = view.findViewById<View>(R.id.tv_hint_content) as TextView
        if (!TextUtils.isEmpty(msg)) {
            tvContent.text = msg
        }
    }

    /**
     * 显示空状态页面
     *
     * @param msg  显示文本信息
     * @param resp
     */
    protected fun showEmptyView(msg: String, resp: Int, reloadContent: String, listener: View.OnClickListener) {
        flContainer!!.removeAllViews()
        val view = LayoutInflater.from(context).inflate(R.layout.view_empty, null)
        val tvReload = view.findViewById<TextView>(R.id.tv_reload)
        tvReload.text = reloadContent
        tvReload.visibility = View.VISIBLE
        tvReload.setOnClickListener(listener)
        flContainer!!.addView(view)
        val ivNav = view.findViewById<ImageView>(R.id.iv_nav)
        SUtils.setPicResource(ivNav, resp)

        val tvContent = view.findViewById<View>(R.id.tv_hint_content) as TextView
        if (!TextUtils.isEmpty(msg)) {
            tvContent.text = msg
        }
    }

    override fun onResume() {
        super.onResume()
        /*   MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getName());*/
        // CUtils.onClick(context, BACK_TAG + "_resume");
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
        /* MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getName());*/
        //CUtils.onClick(context, BACK_TAG + "_pause");
        if (tvTitle != null) {
            SUtils.hideSoftInpuFromWindow(tvTitle)
        }
    }

    fun showKeyboard(view: View) {
        myHandlder.postDelayed({ SUtils.showSoftInpuFromWindow(view, context) }, 300)
    }

    /**
     * 获取颜色资源
     *
     * @param coloRes
     * @return
     */
    fun getResColor(coloRes: Int): Int {
        if (resources == null) {
            resources = context!!.resources
        }
        return resources!!.getColor(coloRes)
    }

    protected abstract fun loadData()

    protected abstract fun finishLoad()

    protected abstract fun setTitleId(): Int

    protected abstract fun setContentView(): Int

    //简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
    abstract fun initPresenter()

    protected abstract fun initData()

    protected abstract fun dealDatas(requestCode: Int, obj: Any)


}

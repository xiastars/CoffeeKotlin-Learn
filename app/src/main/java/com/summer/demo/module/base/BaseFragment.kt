package com.summer.demo.module.base

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.Unbinder
import com.summer.demo.R
import com.summer.demo.bean.BaseResp
import com.summer.demo.constant.ApiConstants
import com.summer.helper.recycle.MaterialRefreshLayout
import com.summer.helper.recycle.SmartRecyclerView
import com.summer.helper.server.SummerParameter
import com.summer.helper.utils.ReceiverUtils
import java.lang.ref.WeakReference
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by xiaqiliang on 2018/7/31.
 */

abstract class BaseFragment : Fragment(),View.OnClickListener {
    protected var sRecycleView: SmartRecyclerView? = null
    lateinit var myHandlder: MyHandler
    var fromId: Long = 0
    var pageIndex: Int = 0
    internal var isRefresh: Boolean = false
    var lastId: String? = null
    var baseHelper: BaseHelper? = null

    internal var isOnRefresh: Boolean = false
    internal var resources: Resources? = null
    private var receiverUtils: ReceiverUtils? = null
    protected lateinit var mView: View
    protected var DEFAULT_LIMIT = 10

    internal lateinit var unbinder: Unbinder

    val handleTime: Long
        get() = baseHelper!!.handleTime

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myHandlder = MyHandler(this)
        baseHelper = BaseHelper(context!!, myHandlder)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = LayoutInflater.from(context).inflate(if (setContentView() == 0) R.layout.view_empty else setContentView(), null)
        unbinder = ButterKnife.bind(this, mView)
        initView(mView)
        return mView
    }


    class Bind<V>(val id: Int,val shouldClick:Boolean = false) : ReadOnlyProperty<BaseFragment, V> {
        override fun getValue(thisRef: BaseFragment, property: KProperty<*>): V {
            var view :View = thisRef.mView.findViewById(id)
            if(shouldClick){
                view.setOnClickListener(thisRef)
            }
            return view as V
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    open fun refresh() {
        pageIndex = 0
        lastId = null
        loadData()
    }


    fun setSmartRecyclerView(svContainer: SmartRecyclerView) {
        this.sRecycleView = svContainer

        svContainer.setOnRefreshListener { refreshLayout ->
            fromId = 0
            pageIndex = 0
            lastId = null
            isRefresh = true
            loadData()

        }
        svContainer.setOnLoadMoreListener { refreshLayout ->
            pageIndex++
            isRefresh = true
            loadData()
        }

    }

    protected fun initBroadcast(vararg action: String) {
        if (receiverUtils != null) {
            receiverUtils!!.unRegisterReceiver()
        }
        receiverUtils = ReceiverUtils(getActivity())
        receiverUtils!!.setActionsAndRegister(*action)
        receiverUtils!!.setOnReceiverListener { action, intent -> onMsgReceiver(action, intent) }
    }

    protected fun onMsgReceiver(action: String, intent: Intent) {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (receiverUtils != null) {
            receiverUtils!!.unRegisterReceiver()
        }
    }

    fun handleViewData(obj: Any) {
        baseHelper!!.handleViewData(obj, sRecycleView, pageIndex)
    }

    fun handleViewData(obj: Any, otherItemCount: Int) {
        baseHelper!!.handleViewData(obj, sRecycleView, pageIndex, otherItemCount)
    }


    class MyHandler(activity: BaseFragment) : Handler() {
        private val mActivity: WeakReference<BaseFragment>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity = mActivity.get()
            if (null != activity) {
                when (msg.what) {
                    BaseHelper.MSG_SUCCEED -> {
                        activity.handleData(msg.arg1, msg.obj, false)
                        activity.finishLoad()
                    }
                    BaseHelper.MSG_FINISHLOAD -> activity.finishLoad()
                    BaseHelper.MSG_CACHE -> activity.handleData(msg.arg1, msg.obj, true)
                    BaseHelper.MSG_ERRO -> activity.dealErrors(msg.arg1, msg.arg2.toString() + "", msg.obj as String)
                    else -> activity.handleMsg(msg.what, msg.obj)
                }
            }
        }
    }

    protected fun handleMsg(position: Int, `object`: Any) {

    }

    private fun handleData(requestType: Int, `object`: Any?, fromCache: Boolean) {
        myHandlder.postDelayed({ isOnRefresh = false }, 100)
        if (this.getActivity() == null || this.getActivity()!!.isFinishing) {
            return
        }
        if (`object` == null) {
            return
        }
        if (`object` is BaseResp) {
            val resp = `object` as BaseResp?
            val isResult = resp!!.isResult
            //如果不是成功状态不返回
            if (!isResult) {
                if (fromCache) {
                    return
                }
                dealErrors(requestType, resp.error.toString() + "", resp.msg)
                return
            }
        }
        isOnRefresh = false
        dealDatas(requestType, `object`)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (baseHelper != null) {
            baseHelper!!.cancelLoading()
        }
        unbinder.unbind()
    }

    protected fun finishLoad(svContainer: MaterialRefreshLayout?) {
        if (svContainer != null) {
            if (pageIndex == PAGE_FROM) {
                svContainer.finishPullDownRefresh()
            } else {
                svContainer.finishPullUpRefresh()
            }
        }
        if (baseHelper != null) {
            baseHelper!!.cancelLoading()
        }
    }

    protected open fun finishLoad() {
        if (sRecycleView != null) {
            if (pageIndex == PAGE_FROM) {
                sRecycleView!!.finishRefresh()
            } else {
                sRecycleView!!.finishLoadMore()
            }
        }
        if (baseHelper != null) {
            baseHelper!!.cancelLoading()
        }
    }

    fun requestData(className: Class<*>, params: SummerParameter, url: String, post: Boolean) {
        requestData(0, className, params, url, post)
    }

    fun postData(requestCode:Int,className: Class<*>, params: SummerParameter, url: String){
        requestData(requestCode, className, params, url, true)
    }

    /**
     * 1.2接口get数据
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
        baseHelper!!.setIsRefresh(isRefresh)
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
        baseHelper!!.setIsRefresh(isRefresh)
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
        baseHelper!!.setIsRefresh(isRefresh)
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
        baseHelper!!.setIsRefresh(isRefresh)
        baseHelper!!.requestData(requestCode, ApiConstants.hostVersion2, 0, className, params, url, 1, false)
    }

    fun requestData(requestCode: Int, className: Class<*>, params: SummerParameter, url: String, post: Boolean) {
        if (baseHelper == null) {
            return
        }
        baseHelper!!.setIsRefresh(isRefresh)
        baseHelper!!.requestData(requestCode, className, params, url, post)
    }

    fun requestData(requestCode: Int, limitTime: Int, className: Class<*>, params: SummerParameter, url: String, post: Boolean) {
        requestData(requestCode, className, params, url, post, false)
    }

    fun requestData(requestCode: Int, className: Class<*>, params: SummerParameter, url: String, post: Boolean, isArray: Boolean) {
        if (baseHelper == null) {
            return
        }
        baseHelper!!.setIsRefresh(isRefresh)
        baseHelper!!.requestData(requestCode, 0, className, params, url, post, isArray)
    }

    override fun onResume() {
        super.onResume()
        //MobclickAgent.onPageStart(this.getClass().getName());
    }

    /**
     * 处理错误
     *
     * @param requestType
     * @param errString
     */
    protected open fun dealErrors(requstCode: Int, requestType: String, errString: String) {
        isOnRefresh = false
        if (baseHelper != null) {
            baseHelper!!.cancelLoading()
            finishLoad()
        }
    }

    override fun onPause() {
        super.onPause()
        //MobclickAgent.onPageEnd(this.getClass().getName());
    }

    open fun onHide() {

    }

    /**
     * 添加一个子View
     * @param view
     */
    private fun addView(view: View) {
        (mView as ViewGroup).addView(view)
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

    protected open fun loadData() {

    }

    protected abstract fun initView(view: View)

    protected abstract fun dealDatas(requestType: Int, obj: Any)

    protected abstract fun setContentView(): Int

    companion object {
        var PAGE_FROM = 0
    }

}

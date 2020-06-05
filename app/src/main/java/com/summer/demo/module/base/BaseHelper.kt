package com.summer.demo.module.base

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.summer.demo.AppContext
import com.summer.demo.R
import com.summer.demo.bean.BaseResp
import com.summer.demo.constant.ApiConstants
import com.summer.demo.constant.SharePreConst
import com.summer.demo.utils.CodeRespondUtils
import com.summer.demo.utils.ServerFileUtils
import com.summer.demo.view.LoadingDialog
import com.summer.helper.adapter.SRecycleMoreAdapter
import com.summer.helper.db.CommonService
import com.summer.helper.db.DBType
import com.summer.helper.recycle.MaterialRefreshLayout
import com.summer.helper.recycle.SmartRecyclerView
import com.summer.helper.server.EasyHttp
import com.summer.helper.server.PostData
import com.summer.helper.server.RequestCallback
import com.summer.helper.server.SummerParameter
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SThread
import com.summer.helper.utils.SUtils
import com.summer.helper.view.NRecycleView
import java.util.*

/**
 * Created by xiaqiliang on 2017/5/2.
 */

class BaseHelper(internal var context: Context, internal var myHandlder: Handler) {
    internal var firstRequest: Boolean = false
    internal var loadingDialog: LoadingDialog? = null
    internal var isRefresh: Boolean = false
    var handleTime: Long = 0

    //当一个页面反复刷新时，只第一次插入
    internal var isFirstInsertDB: Boolean = false

    //特殊情况手动显示加载框
    var isShowLoading: Boolean = false

    //分页数量
    var loadCount = 0

    /**
     * 请求数据
     *
     * @param requestCode
     * @param className
     * @param params
     * @param url
     * @param post
     */
    fun requestData(requestCode: Int, className: Class<*>, params: SummerParameter, url: String, post: Boolean) {
        requestData(requestCode, 0, className, params, url, post)
    }

    internal fun setMIUIStatusBarDarkMode(activity: Activity) {
        Logs.i("PostData.MANUFACTURER" + PostData.MANUFACTURER)
        if (PostData.MANUFACTURER == "Xiaomi") {
            val clazz = activity.window.javaClass
            try {
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                val darkModeFlag = field.getInt(layoutParams)

                val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                extraFlagField.invoke(activity.window, darkModeFlag)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    @JvmOverloads
    fun requestData(requestCode: Int, limitTime: Int, className: Class<*>, params: SummerParameter, url: String, post: Boolean, isArray: Boolean = false) {
        requestData(requestCode, null, limitTime, className, params, url, if (post) 1 else 0, isArray)
    }

    fun putData(requestCode: Int, className: Class<*>, params: SummerParameter, url: String) {
        requestData(requestCode, null, 0, className, params, url, 2, false)
    }

    fun putData(requestCode: Int, version: String, className: Class<*>, params: SummerParameter, url: String) {
        requestData(requestCode, version, 0, className, params, url, 2, false)
    }

    fun deleteData(requestCode: Int, className: Class<*>, params: SummerParameter, url: String) {
        requestData(requestCode, null, 0, className, params, url, 3, false)
    }

    fun deleteData(requestCode: Int, version: String, className: Class<*>, params: SummerParameter, url: String) {
        requestData(requestCode, version, 0, className, params, url, 3, false)
    }


    /**
     * 请求数据
     *
     * @param limitTime 数据重新请求限定时间
     * @param className 要注入的类
     * @param params
     * @param url       链接
     * @param post      是否是Post
     * @param isArray   是不是Array类型
     */
    fun requestData(requestCode: Int, version: String?, limitTime: Int, className: Class<*>, params: SummerParameter?, url: String, post: Int, isArray: Boolean) {
        var url = url
        if (params == null) {
            return
        }
        val token = SUtils.getStringData(context, SharePreConst.TOKEN)
        Logs.i("token:$token")
        if(!url.startsWith("http")){
            url = ApiConstants.getHost(version) + url
        }
        val time = System.currentTimeMillis()
        //取得每页请求的数量，用来处理底部栏没有更多了
        Logs.i("contain:" + params.containsKey("count"))
        if (params.containsKey("count")) {
            val count = params.get("count") as String
            if (count != null) {
                loadCount = Integer.parseInt(count)
                Logs.i("loadCount:$loadCount")
            }
        }
        if (params.containsKey("limit")) {
            val count = params.get("limit") as String
            if (count != null) {
                loadCount = Integer.parseInt(count)
                Logs.i("loadCount:$loadCount")
            }
        }
        val toastMessage = params.isToastEnable
        val pageIndex = Integer.parseInt(params.getParamSecurity("page", 0.toString() + "") as String)
        val readCache = params.isCacheSupport
        val saveurl = params.encodeLogoUrl(url)
        //当为开发者模式且SummperParameter设置了虚拟数据时才启用
        var hasVirturData = false
        if (AppContext.DEBUGMODE) {
            if (params.isVirtualData) {
                val virtualCode = params.virtualCode
                //virtualdata.txt
                var data: String? = ServerFileUtils.readFileByLineOnAsset(virtualCode, VITURAL_DATA, context)
                try {
                    data = data!!.replace(">".toRegex(), "2")
                    data = data.replace("<".toRegex(), "2")
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                Logs.i("data:" + data!!)
                if (data != null) {
                    try {
                        val t = JSON.parseObject(data, BaseResp::class.java)
                        Logs.i("t:::" + t!!)
                        if (t != null) {
                            myHandlder.sendEmptyMessage(MSG_FINISHLOAD)
                            handleData(requestCode, t, saveurl, className, pageIndex, readCache, isArray, toastMessage)
                            isRefresh = false
                            hasVirturData = true
                            return
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }
        Logs.i("hasVirtual:"+hasVirturData)
        if (hasVirturData) {
            return
        }
        Logs.i("first:$firstRequest")
        //页面启动第一次时从缓存里获取数据
        if (!firstRequest && readCache) {
            firstRequest = true
            readCache(saveurl, requestCode, className, isArray)
        }
        Logs.i("readCache:$readCache")
        //频率操作限制
        if (isFrequentRequest(saveurl, if (limitTime == 0) 1000 else limitTime)) {
            Logs.i("isFrequentRequest频繁操作")
            if (readCache(saveurl, requestCode, className, isArray)) {
                Logs.i("readCache")
                return
            }
        }
        Logs.i("loading:"+isShowLoading)
        //当第一次请求数据时显示加载框
        if (isShowLoading) {
            if (!isRefresh && loadingDialog == null) {
                LoadingDialog.showDialogForLoading(context as Activity)

            } else {
                if (loadingDialog != null && isRefresh) {
                    LoadingDialog.showDialogForLoading(context as Activity)
                }
            }
        }

        //默认所有className继承自BaseResp，当为List数据时，为了方便，直接用List里的单个对象
        var injectClass: Class<*> = BaseResp::class.java
        if (BaseResp::class.java.isAssignableFrom(className) && className != BaseResp::class.java) {
            injectClass = className
        }
        val callBack = object : RequestCallback<Any>() {
            override fun done(hunkResp: Any) {
                handleData(requestCode, hunkResp, saveurl, className, pageIndex, readCache, isArray, toastMessage)
                isRefresh = false
            }

            override fun onError(errorCode: Int, errorStr: String) {
                Logs.e("summer", "errorStr,$errorStr")
                myHandlder.obtainMessage(MSG_ERRO, requestCode, errorCode, errorStr).sendToTarget()
            }
        }
        Logs.i("loading:"+post)
        if (post == 1) {
            EasyHttp.post(context, token, url, injectClass as Class<Any>, params, callBack)
        } else if (post == 0) {
            EasyHttp.get(context, token, url, injectClass as Class<Any>, params, callBack)
        } else if (post == 2) {
            EasyHttp.put(context, token, url, injectClass as Class<Any>, params, callBack)
        } else if (post == 3) {
            EasyHttp.delete(context, token, url, injectClass as Class<Any>, params, callBack)
        }

    }


    /**
     * 读取缓存
     *
     * @param saveurl
     * @param requestCode
     */
    private fun readCache(saveurl: String, requestCode: Int, cls: Class<*>, isArray: Boolean): Boolean {
        val o = CommonService(context).getObjectData(DBType.COMMON_DATAS, saveurl) ?: return false
        val hunkResp : Object = o as Object
        if (hunkResp != null) {
            //如果列表的单项类型不正确也重新请求
            if (hunkResp is List<*>) {
                if (hunkResp.size > 0) {
                    val item:Object = hunkResp[0] as Object
                    if (!cls.isAssignableFrom(item.javaClass)) {
                        return false
                    }
                }
            }
            myHandlder.sendEmptyMessage(MSG_FINISHLOAD)
            myHandlder.obtainMessage(MSG_CACHE, requestCode, 0, hunkResp).sendToTarget()
            cancelLoading()
            return true
        }
        return false
    }

    /**
     * 频繁请求提示
     *
     * @param saveurl
     * @return
     */
    private fun isFrequentRequest(saveurl: String, reqestTime: Int): Boolean {
        /*  final String saveLong = saveurl + "_long";
        final String saveInt = saveurl + "_int";
        long lastTime = SUtils.getLongData(context, saveLong);
        if (lastTime == 0) {
            SUtils.saveLongData(context, saveLong, System.currentTimeMillis());
            return false;
        }
        int requestIndex = SUtils.getIntegerData(context, saveInt);
        int divideTime = (int) (System.currentTimeMillis() - lastTime);
        myHandlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                SUtils.saveIntegerData(context, saveInt, 0);
            }
        }, 1000);

        if (requestIndex > 1) {
            //SUtils.makeToast(context, "操作频繁，请稍后重试");
            return true;
        }
        if (divideTime < reqestTime) {
            SUtils.saveLongData(context, saveLong, System.currentTimeMillis());
            SUtils.saveIntegerData(context, saveInt, requestIndex + 1);
            return true;
        }
        SUtils.saveLongData(context, saveLong, System.currentTimeMillis());*/
        return false
    }

    private fun handleData(requestCode: Int, hunkResp: Any?, url: String, classD: Class<*>, pageIndex: Int, supportCache: Boolean, isArray: Boolean = false, toastMessage: Boolean = true) {
        if (hunkResp != null) {
            val resp = hunkResp as BaseResp?
            handleTime = resp!!.time
            Logs.i("info:"+resp.info)
            if (resp.info != null) {
                val content = resp.info
                var datas: Any? = null
                Logs.i("info:"+classD)
                if (!BaseResp::class.java.isAssignableFrom(classD) && classD != BaseResp::class.java) {
                    Logs.i("info:"+content +",,"+(content is JSONArray))
                    if (content is JSONArray) {
                        datas = JSON.parseArray(content.toJSONString(), classD)
                    } else if (content is JSONObject) {
                        datas = JSON.parseObject(content.toJSONString(), classD)
                    } else if (content is String) {
                        datas = resp
                    }
                } else {
                    datas = resp.info
                }
                Logs.i("datas:$datas")

                if (datas != null) {
                    val result = resp.isResult
                    val code = resp.error
                    if (result) {
                        myHandlder.obtainMessage(BaseHelper.MSG_SUCCEED, requestCode, 0, datas).sendToTarget()
                        val finalDatas = datas
                        if (pageIndex == 0 && supportCache) {
                            SThread.getIntances().submit { CommonService(context).insert(DBType.COMMON_DATAS, url, finalDatas) }
                        }

                    } else {
                        if (toastMessage) {
                            CodeRespondUtils(context, resp)
                        }
                        myHandlder.obtainMessage(BaseHelper.MSG_ERRO, requestCode, code, resp.msg).sendToTarget()
                    }

                } else {
                    val result = resp.isResult
                    val code = resp.error
                    if (result) {
                        myHandlder.obtainMessage(BaseHelper.MSG_SUCCEED, requestCode, -1, null).sendToTarget()
                    } else {
                        if (toastMessage) {
                            CodeRespondUtils(context, resp)
                        }
                        myHandlder.obtainMessage(BaseHelper.MSG_ERRO, requestCode, code, resp.msg).sendToTarget()
                    }
                }
            } else {
                val code = resp.error
                val result = resp.isResult
                if ((BaseResp::class.java.isAssignableFrom(classD) || classD == BaseResp::class.java) && result) {
                    myHandlder.obtainMessage(BaseHelper.MSG_SUCCEED, requestCode, 0, resp).sendToTarget()
                } else {
                    if (result) {
                        if (isArray) {
                            myHandlder.obtainMessage(BaseHelper.MSG_SUCCEED, requestCode, 0, ArrayList<Any>()).sendToTarget()
                        } else {
                            if (!BaseResp::class.java.isAssignableFrom(classD) && classD != BaseResp::class.java) {
                                myHandlder.obtainMessage(BaseHelper.MSG_ERRO, requestCode, 0, null).sendToTarget()
                            } else {
                                myHandlder.obtainMessage(BaseHelper.MSG_SUCCEED, requestCode, 0, resp).sendToTarget()
                            }
                        }
                    } else {
                        myHandlder.obtainMessage(BaseHelper.MSG_ERRO, requestCode, code, resp.msg).sendToTarget()
                        if (toastMessage) {
                            CodeRespondUtils(context, resp)
                        }
                    }
                }
            }
        }
        myHandlder.sendEmptyMessage(MSG_FINISHLOAD)
    }

    fun cancelLoading() {
        if (loadingDialog != null) {
            loadingDialog!!.cancelDialogForLoading()
        }
    }

    /**
     * 设置沉浸式状态栏
     */
    fun setLayoutFullscreen(fullscreen: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            val activity = context as Activity
            val window = activity.window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (fullscreen) {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

                } else {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                val view = activity.findViewById<View>(R.id.title)
                if (view != null) {
                    val params = view.layoutParams as RelativeLayout.LayoutParams
                    params.height = SUtils.getDip(context, 45) + SUtils.getStatusBarHeight(activity)
                }

            } else {
                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            }
        }
    }

    /**
     * 处理RecyleView的数据，支持上拉加载显示
     *
     * @param obj
     * @param sRecycleView
     * @param pageIndex
     * @param otherItemCount obj里除了正常一页的的数据类型之外的数据数量，例如有时候会插入广告数据
     */
    @JvmOverloads
    fun handleViewData(obj: Any?, sRecycleView: SmartRecyclerView?, pageIndex: Int, otherItemCount: Int = 0) {
        if (sRecycleView == null) {
            return
        }

        val adapter = sRecycleView!!.adapter as SRecycleMoreAdapter?
                ?: return
        if (obj != null) {
            val resp = obj as List<*>?

            val size = resp!!.size - otherItemCount
            Logs.i("pageIndex:$pageIndex")
            if (size > 0) {
                if (pageIndex > 0 && adapter.items != null) {
                    //adapter.items.addAll(resp)
                } else {
                    adapter.items = resp
                }
                //如果当前加载的数据量小于设定的单页数量，则显示底部栏
                Logs.i("size<:::" + (size < loadCount))
                if (size < loadCount) {
                    adapter.notifyDataChanged(adapter.items, false)
                } else {
                    adapter.notifyDataChanged(adapter.items, true)
                }
                //sRecycleView.hideEmptyView()
                //如果不是第一页，且没有更多数据了，显示底部栏
            } else if (pageIndex > 0) {
                //sRecycleView.isPullUpRefreshable = false
                adapter.setBottomViewVisible()
            } else {
                adapter.notifyDataChanged(ArrayList<Any>())
                if (sRecycleView != null && adapter.headerCount > 0) {
                    adapter.showSRecycleEmptyView()
                } else {
                    adapter.showEmptyView()
                    //sRecycleView.showEmptyView()

                }
            }
        } else {
            adapter.notifyDataChanged(ArrayList<Any>())
            adapter.showEmptyView()
            //sRecycleView.showEmptyView()
        }
    }


    /**
     * 处理RecyleView的数据，支持上拉加载显示
     */
    fun handleViewData(obj: Any?, sRecycleView: NRecycleView?, topView: MaterialRefreshLayout?, pageIndex: Int) {
        if (topView != null) {
            topView.finishPullDownRefresh()
            topView.finishPullUpRefresh()
        }
        Logs.i("可刷新的视图是:$topView,分页pageIndex是:$pageIndex")
        if (sRecycleView == null) {
            return
        }
        if (obj != null) {
            val resp = obj as List<*>?
            val adapter = sRecycleView.adapter as SRecycleMoreAdapter?
            //是否显示空页面
            val isShowEmptyView = adapter!!.isShowEmptyView
            if (isShowEmptyView) {
                if (pageIndex == 0 && resp!!.size == 0) {
                    adapter.notifyDataChanged(resp, true)
                    return
                }
            }
            if (resp!!.size > 0) {
                if (pageIndex > 0 && adapter.items != null) {
                    //adapter.items.addAll(resp)
                } else {
                    adapter.items = resp
                }
                Logs.i("当前加载数据数量:" + resp.size + ",每页刷新个数是:" + loadCount)
                if (resp.size < loadCount) {
                    if (topView != null) {
                        topView.isPullUpRefreshable = false
                    }

                    adapter.notifyDataChanged(adapter.items, false)
                } else {
                    if (topView != null) {
                        topView.isPullUpRefreshable = true
                    }

                    adapter.notifyDataChanged(adapter.items, true)
                }

            } else if (pageIndex > 0) {
                if (topView != null) {
                    topView.isPullUpRefreshable = false
                }
                adapter.setBottomViewVisible()
            } else {
                adapter.notifyDataChanged(ArrayList<Any>())
            }
        }
    }

    fun setIsRefresh(isRefresh: Boolean) {
        this.isRefresh = isRefresh
    }

    companion object {
        val DEFAULT_LOAD_COUNT = 10//默认请求数量

        //请求数据失败
        val MSG_ERRO = -1
        //请求数据成功
        val MSG_SUCCEED = -2

        val MSG_FINISHLOAD = -3

        //从数据库读取目录成功
        val MSG_CACHE = -4


        //显示加载
        val MSG_LOADING = -5

        //取消加载
        val MSG_CANCEL_LOADING = -6

        //虚拟数据地址
        var VITURAL_DATA = "virtualdata.txt"//virtualdata.txt
    }
}
/**
 * 请求数据
 *
 * @param limitTime 数据重新请求限定时间
 * @param className 要注入的类
 * @param params
 * @param url       链接
 * @param post      是否是Post
 */
/**
 * 处理返回数据
 *
 * @param hunkResp
 * @param url
 * @param classD
 */
/**
 * 处理RecyleView的数据，支持上拉加载显示
 *
 * @param obj
 * @param sRecycleView
 * @param pageIndex
 */

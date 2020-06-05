package com.summer.demo.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.summer.demo.R
import com.summer.helper.view.CircleIndicator
import com.summer.helper.view.CustomerViewPager
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * 广告形式的ViewPager
 * @author Administrator
 */
class BannerFragment : BaseSimpleFragment() {
    internal lateinit var mBannerViewPager: CustomerViewPager
    //下面的小点
    internal lateinit var mCircleIndicator: CircleIndicator
    internal var pics: IntArray? = intArrayOf(R.drawable.xiehou04, R.drawable.xiehou03, R.drawable.xiehou03)

    //当前广告页面
    internal var mCurrentItem: Int = 0

    // 这是一个定时线程
    internal var scheduledExecutorService: ScheduledExecutorService? = null

    // Handler
    private val handler = object : Handler() {

        override fun handleMessage(msg: Message) {
            mBannerViewPager.currentItem = mCurrentItem
        }

    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_banner, null)
        initView(view)
        return view
    }

    /**
     * Fragment里面的findViewId由onCreateView返回的View来寻找
     * @param view
     */
    private fun initView(view: View) {
        mBannerViewPager = view.findViewById<View>(R.id.banners_viewpager) as CustomerViewPager
        mCircleIndicator = view.findViewById<View>(R.id.circle_indicator) as CircleIndicator
        initBanners()
    }

    fun initBanners() {
        val adBanner = context?.let { AdBanner(it, pics) }
        mBannerViewPager.adapter = adBanner
        //必须在ViewPager设置Adapter后加
        mCircleIndicator.setViewPager(mBannerViewPager)
        //创建随机数，每次进入时，随机显示一页
        val r = Random()
        mCurrentItem = r.nextInt(pics!!.size)
        mBannerViewPager.currentItem = mCurrentItem
        //开始轮播
        startPlay()
    }

    /**
     * 开始轮播图切换
     */
    private fun startPlay() {
        //如果在播放状态就先关掉
        stopPlay()
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        //第一个参数是要执行的线程，第二个为第一次进入等待时间，第三个轮播之间的时间，第四个为计时单位
        scheduledExecutorService!!.scheduleAtFixedRate(SlideShowTask(), 1, 4, TimeUnit.SECONDS)
    }

    /**
     * 停止轮播图切换
     */
    private fun stopPlay() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService!!.shutdown()
            scheduledExecutorService = null
        }
    }

    /**
     * 执行轮播图切换任务
     *
     */
    private inner class SlideShowTask : Runnable {

        override fun run() {
            synchronized(mBannerViewPager) {
                if (pics != null) {
                    mCurrentItem = (mCurrentItem + 1) % pics!!.size
                    handler.obtainMessage().sendToTarget()
                }
            }
        }
    }

    inner class AdBanner(private val context: Context, private val datas: IntArray?) : PagerAdapter() {
        private val mListViews = HashMap<Int, View>()

        init {
            notifyDataSetChanged()
        }

        override fun getCount(): Int {
            return datas?.size ?: 0
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(mListViews[position])// 删除页卡
        }

        @SuppressLint("NewApi")
        override fun instantiateItem(container: ViewGroup, position: Int): View { // 这个方法用来实例化页卡
            val view = LayoutInflater.from(context).inflate(R.layout.item_banner, null)
            //如果自定义View的高度，首先要获取当前View的父布局类型，然后得到这个类型的LayoutParameter
            val ivImageView = view.findViewById<View>(R.id.item_album) as ImageView
            ivImageView.setBackgroundResource(datas!![position])
            mListViews[position] = view
            container.addView(mListViews[position])// 添加页卡
            return mListViews[position]!!
        }

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1// 官方提示这样写
        }
    }

}

package com.summer.demo.module.base

import android.annotation.TargetApi
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.*
import butterknife.BindView
import com.summer.demo.R
import com.summer.demo.module.base.swipe.SwipeBackActivityHelper
import com.summer.helper.recycle.MaterialRefreshLayout
import com.summer.helper.recycle.MaterialRefreshListener
import com.summer.helper.recycle.NestefreshLayout
import com.summer.helper.utils.SUtils
import com.summer.helper.utils.SViewUtils
import com.summer.helper.view.PagerSlidingTabStrip
import com.summer.helper.view.ScrollableLayout

/**
 * Created by xiastars on 2018/3/16.
 */

abstract class BaseNestedFragmentActivity : BaseFragmentActivity() {

    private val mHelper: SwipeBackActivityHelper? = null
    @BindView(R.id.ll_nested_container)
    internal var llNestedContainer: LinearLayout? = null
    @BindView(R.id.pagerStrip)
    var pagerStrip: PagerSlidingTabStrip? = null
    @BindView(R.id.rl_pager)
    internal var rlPager: RelativeLayout? = null
    @BindView(R.id.viewpager)
    var viewpager: ViewPager? = null
    @BindView(R.id.scrollableLayout)
    var scrollableLayout: ScrollableLayout? = null
    @BindView(R.id.refreshlayout)
    internal var refreshlayout: NestefreshLayout? = null
    @BindView(R.id.iv_nav)
    internal var ivNav: ImageView? = null
    @BindView(R.id.tv_hint_content)
    internal var tvHintContent: TextView? = null
    @BindView(R.id.iv_back)
    internal var ivBack: ImageView? = null
    @BindView(R.id.ll_back)
    internal var llBack: LinearLayout? = null
    @BindView(R.id.btn_edit_profile)
    var btnEditProfile: Button? = null
    @BindView(R.id.btn_share)
    internal var btnShare: Button? = null
    @BindView(R.id.rl_back)
    internal var rlBack: RelativeLayout? = null
    @BindView(R.id.rl_main_top)
    internal var rlMainTop: RelativeLayout? = null
    @BindView(R.id.line_title)
    internal var lineTitle: View? = null
    @BindView(R.id.tv_nest_title)
    internal var tvNestTitle: TextView? = null


    internal var onScrollListener: ScrollableLayout.OnScrollListener? = null
    var onScrollDirectionListener = null

    internal var preY: Int = 0
    protected var headerHeight: Int = 0
    internal var showCustomTitle: Boolean = false

    protected abstract val containerView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(setContentView());
        //checkView();
        removeTitleAndFullscreen()
    }
    /*

    protected void checkView() {
        if (!SUtils.isNetworkAvailable(context)) {
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
            ButterKnife.bind(this);
            initData();
        }
    }
*/


    override fun loadData() {

    }

    override fun finishLoad() {
        refreshlayout!!.finishPullDownRefresh()
    }

    override fun dealDatas(requestCode: Int, obj: Any) {}

    override fun setTitleId(): Int {
        return 0
    }

    override fun setContentView(): Int {
        return R.layout.activity_base_nested
    }

    override fun initData() {
        refreshlayout!!.addRefreshView(scrollableLayout)
        llNestedContainer!!.addView(containerView)
        onRefresh()
        init()
        initFragmentPager(viewpager, pagerStrip, scrollableLayout)
        removeTitleAndFullscreen()
        if (showCustomTitle) {
            SViewUtils.setViewMargin(rlBack!!, SUtils.getStatusBarHeight(this), SViewUtils.SDirection.TOP)
            handleTitleView()
        }


    }

    protected fun removeTitleAndFullscreen() {
        showCustomTitle = true
        removeTitle()
        setLayoutFullscreen()
    }

    protected fun initFragmentPager(viewpager: ViewPager?, pagerStrip: PagerSlidingTabStrip?, scrollableLayout: ScrollableLayout?) {}

    protected fun init() {}

    private fun onRefresh() {
        refreshlayout!!.setMaterialRefreshListener(object : MaterialRefreshListener() {
            override fun onRefresh(materialRefreshLayout: MaterialRefreshLayout) {
                refresh()
            }
        })
    }

    protected fun refresh() {

    }

    private fun handleTitleView() {
        headerHeight = SUtils.getDip(context!!, 42) + SUtils.getStatusBarHeight(this)
        scrollableLayout!!.defaultMarginTop = headerHeight
        handleScroll()


        llBack!!.setOnClickListener { onBackClick() }

    }

    protected fun handleScroll() {
        scrollableLayout!!.setOnScrollListener { currentY, maxY ->
            if (preY == 0) {
                preY = currentY
            }
            if (preY < currentY) {//向上移动
                if (currentY > headerHeight) {
                    showTitleStyle()
                }
                if (onScrollDirectionListener != null) {
                    //onScrollDirectionListener.onScrollDown(true);
                }
            } else {
                if (currentY < headerHeight) {
                    hideTitleStyle()
                }
                if (onScrollDirectionListener != null) {
                    //onScrollDirectionListener.onScrollDown(false);
                }
            }
            if (onScrollListener != null) {
                onScrollListener!!.onScroll(currentY, maxY)
            }
            preY = currentY
        }
    }

    override fun onBackClick() {
        //. CUtils.onClick(getClass().getSimpleName() + "_onback");
        this@BaseNestedFragmentActivity.finish()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun hideTitleStyle() {
        rlBack!!.setBackgroundColor(getResColor(R.color.transparent))
        changeHeaderStyleTrans(getResColor(R.color.transparent))
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            btnEditProfile!!.backgroundTintList = ColorStateList.valueOf(getResColor(R.color.white))
            btnShare!!.backgroundTintList = ColorStateList.valueOf(getResColor(R.color.white))
            /*       ivBack.setBackgroundTintList(ColorStateList.valueOf(getResColor(R.color.white)));*/
        }
        lineTitle!!.visibility = View.GONE
        if (setTitleId() != 0) {
            tvNestTitle!!.text = ""
        }
        tvNestTitle!!.setTextColor(getResColor(R.color.white))
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun showTitleStyle() {
        changeHeaderStyleTrans(getResColor(R.color.grey_ba))
        rlBack!!.setBackgroundColor(getResColor(R.color.transparent))
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            ivBack!!.backgroundTintList = ColorStateList.valueOf(getResColor(R.color.blue_56))
            btnEditProfile!!.backgroundTintList = ColorStateList.valueOf(getResColor(R.color.blue_56))
            btnShare!!.backgroundTintList = ColorStateList.valueOf(getResColor(R.color.blue_56))
        }
        lineTitle!!.visibility = View.VISIBLE
        tvNestTitle!!.setTextColor(getResColor(R.color.black))
        if (setTitleId() != 0) {
            tvNestTitle!!.text = context!!.resources.getString(setTitleId())
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    override fun changeHeaderStyleTrans(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = color
        }
    }


    /**
     * 获取颜色资源
     *
     * @param coloRes
     * @return
     */
    override fun getResColor(coloRes: Int): Int {
        if (resources == null) {
            resources = context!!.resources
        }
        return resources!!.getColor(coloRes)
    }

    /**
     * 显示编辑按钮
     *
     * @param visible
     */
    fun showBtnEidt(visible: Int) {
        btnEditProfile!!.visibility = visible
    }

    fun setOnScrollListener(onScrollListener: ScrollableLayout.OnScrollListener) {
        this.onScrollListener = onScrollListener
    }

    /**
     * 显示分享按钮
     *
     * @param visible
     */
    fun showBtnShare(visible: Int) {
        btnShare!!.visibility = visible
    }

}

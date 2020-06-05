package com.summer.demo.module.base

import android.annotation.TargetApi
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.*
import com.summer.demo.R
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

    private val llNestedContainer: LinearLayout by Bind(R.id.ll_nested_container)
    private val pagerStrip: PagerSlidingTabStrip by Bind(R.id.pagerStrip)
    private val rlPager: RelativeLayout by Bind(R.id.rl_pager)
    private val scrollableLayout: ScrollableLayout by Bind(R.id.scrollableLayout)
    private val refreshlayout: NestefreshLayout by Bind(R.id.refreshlayout)
    private val ivBack: ImageView by Bind(R.id.iv_back)
    private val btnEditProfile: Button by Bind(R.id.btn_edit_profile)
    private val btnShare: Button by Bind(R.id.btn_share)
    private val rlBack: RelativeLayout by Bind(R.id.rl_back)
    private val rlMainTop: RelativeLayout by Bind(R.id.rl_main_top)
    private val lineTitle: View by Bind(R.id.line_title)
    private val tvNestTitle: TextView by Bind(R.id.tv_nest_title)
    private val viewpager: ViewPager  by Bind(R.id.viewpager)
    private val llBack: LinearLayout by Bind(R.id.ll_back)



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

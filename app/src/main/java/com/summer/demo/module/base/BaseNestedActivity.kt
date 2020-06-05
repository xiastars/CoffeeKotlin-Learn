package com.summer.demo.module.base

import android.content.res.ColorStateList
import android.os.Build
import android.support.annotation.RequiresApi
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

abstract class BaseNestedActivity : BaseRequestActivity() {

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

    internal var preY: Int = 0
    internal var headerHeight: Int = 0
    internal var showCustomTitle: Boolean = false

    abstract val containerView: View

    protected override fun finishLoad() {
        refreshlayout!!.finishPullDownRefresh()
    }

    protected override fun dealDatas(requestCode: Int, obj: Any) {}

    protected override fun setTitleId(): Int {
        return 0
    }

    protected override fun setContentView(): Int {
        return R.layout.activity_base_nested
    }

    protected override fun initData() {
        refreshlayout!!.addRefreshView(scrollableLayout)
        llNestedContainer!!.addView(containerView)
        onRefresh()
        init()
        initFragmentPager(pagerStrip, scrollableLayout)
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

    protected fun initFragmentPager(pagerStrip: PagerSlidingTabStrip?, scrollableLayout: ScrollableLayout?) {}

    protected fun init() {}

    private fun onRefresh() {
        refreshlayout!!.setMaterialRefreshListener(object : MaterialRefreshListener() {
            public override fun onRefresh(materialRefreshLayout: MaterialRefreshLayout) {
                refresh()
            }
        })
    }

    protected fun refresh() {

    }

    private fun handleTitleView() {
        headerHeight = SUtils.getDip(context!!, 42) + SUtils.getStatusBarHeight(this)
        scrollableLayout!!.setDefaultMarginTop(headerHeight)
        scrollableLayout!!.setOnScrollListener(object : ScrollableLayout.OnScrollListener {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public override fun onScroll(currentY: Int, maxY: Int) {
                if (preY == 0) {
                    preY = currentY
                }
                if (preY < currentY) {//向上移动
                    if (currentY > headerHeight) {
                        changeHeaderStyleTrans(getResColor(R.color.grey_ba))
                        rlBack!!.setBackgroundColor(getResColor(R.color.white))
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                            ivBack!!.setBackgroundTintList(ColorStateList.valueOf(getResColor(R.color.black)))
                            btnEditProfile!!.setBackgroundTintList(ColorStateList.valueOf(getResColor(R.color.black)))
                            btnShare!!.setBackgroundTintList(ColorStateList.valueOf(getResColor(R.color.black)))
                        }
                        lineTitle!!.setVisibility(View.VISIBLE)
                        tvNestTitle!!.setTextColor(getResColor(R.color.black))
                    } else {
                        rlBack!!.setAlpha(1f)
                    }
                } else {
                    if (currentY < headerHeight) {
                        rlBack!!.setBackgroundColor(getResColor(R.color.transparent))
                        changeHeaderStyleTrans(getResColor(R.color.half_greye1))
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                            btnEditProfile!!.setBackgroundTintList(ColorStateList.valueOf(getResColor(R.color.white)))
                            btnShare!!.setBackgroundTintList(ColorStateList.valueOf(getResColor(R.color.white)))
                            ivBack!!.setBackgroundTintList(ColorStateList.valueOf(getResColor(R.color.white)))
                        }
                        lineTitle!!.setVisibility(View.GONE)
                        tvNestTitle!!.setTextColor(getResColor(R.color.white))
                    }
                }
                preY = currentY
            }
        })
        if (setTitleId() != 0) {
            tvNestTitle!!.setText(context!!.getResources().getString(setTitleId()))
        }
        llBack!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                onBackClick()
            }
        })

    }

    /**
     * 显示编辑按钮
     *
     * @param visible
     */
    fun showBtnEidt(visible: Int) {
        btnEditProfile!!.setVisibility(visible)
    }


    /**
     * 显示分享按钮
     *
     * @param visible
     */
    fun showBtnShare(visible: Int) {
        btnShare!!.setVisibility(visible)
    }


}

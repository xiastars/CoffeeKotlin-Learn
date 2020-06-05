package com.summer.demo.module.base

import android.content.res.ColorStateList
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.*
import butterknife.BindView
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

abstract class BaseNestedActivity:BaseRequestActivity() {

@BindView(R.id.ll_nested_container)
internal var llNestedContainer:LinearLayout? = null
@BindView(R.id.pagerStrip)
 var pagerStrip:PagerSlidingTabStrip? = null
@BindView(R.id.rl_pager)
internal var rlPager:RelativeLayout? = null
@BindView(R.id.scrollableLayout)
 var scrollableLayout:ScrollableLayout? = null
@BindView(R.id.refreshlayout)
internal var refreshlayout:NestefreshLayout? = null
@BindView(R.id.iv_nav)
internal var ivNav:ImageView? = null
@BindView(R.id.tv_hint_content)
internal var tvHintContent:TextView? = null
@BindView(R.id.iv_back)
internal var ivBack:ImageView? = null
@BindView(R.id.btn_edit_profile)
internal var btnEditProfile:Button? = null
@BindView(R.id.btn_share)
internal var btnShare:Button? = null
@BindView(R.id.rl_back)
internal var rlBack:RelativeLayout? = null
@BindView(R.id.rl_main_top)
internal var rlMainTop:RelativeLayout? = null
@BindView(R.id.line_title)
internal var lineTitle:View? = null
@BindView(R.id.tv_nest_title)
internal var tvNestTitle:TextView? = null

internal var preY:Int = 0
internal var headerHeight:Int = 0
internal var showCustomTitle:Boolean = false

abstract val containerView:View

protected override fun finishLoad() {
refreshlayout!!.finishPullDownRefresh()
}

protected override fun dealDatas(requestCode:Int, obj:Any) {}

protected override fun setTitleId():Int {
return 0
}

protected override fun setContentView():Int {
return R.layout.activity_base_nested
}

protected override fun initData() {
refreshlayout!!.addRefreshView(scrollableLayout)
llNestedContainer!!.addView(containerView)
onRefresh()
init()
initFragmentPager(pagerStrip, scrollableLayout)
removeTitleAndFullscreen()
if (showCustomTitle)
{
SViewUtils.setViewMargin(rlBack!!, SUtils.getStatusBarHeight(this), SViewUtils.SDirection.TOP)
handleTitleView()
}

}

protected fun removeTitleAndFullscreen() {
showCustomTitle = true
removeTitle()
setLayoutFullscreen()
}

protected fun initFragmentPager(pagerStrip:PagerSlidingTabStrip?, scrollableLayout:ScrollableLayout?) {}

protected fun init() {}

private fun onRefresh() {
refreshlayout!!.setMaterialRefreshListener(object:MaterialRefreshListener() {
public override fun onRefresh(materialRefreshLayout:MaterialRefreshLayout) {
refresh()
}
})
}

protected fun refresh() {

}

private fun handleTitleView() {
headerHeight = SUtils.getDip(context!!, 42) + SUtils.getStatusBarHeight(this)
scrollableLayout!!.setDefaultMarginTop(headerHeight)
scrollableLayout!!.setOnScrollListener(object:ScrollableLayout.OnScrollListener {
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public override fun onScroll(currentY:Int, maxY:Int) {
if (preY == 0)
{
preY = currentY
}
if (preY < currentY)
{//向上移动
if (currentY > headerHeight)
{
changeHeaderStyleTrans(getResColor(R.color.grey_ba))
rlBack!!.setBackgroundColor(getResColor(R.color.white))
if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
{
ivBack!!.setBackgroundTintList(ColorStateList.valueOf(getResColor(R.color.black)))
btnEditProfile!!.setBackgroundTintList(ColorStateList.valueOf(getResColor(R.color.black)))
btnShare!!.setBackgroundTintList(ColorStateList.valueOf(getResColor(R.color.black)))
}
lineTitle!!.setVisibility(View.VISIBLE)
tvNestTitle!!.setTextColor(getResColor(R.color.black))
}
else
{
rlBack!!.setAlpha(1f)
}
}
else
{
if (currentY < headerHeight)
{
rlBack!!.setBackgroundColor(getResColor(R.color.transparent))
changeHeaderStyleTrans(getResColor(R.color.half_greye1))
if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
{
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
if (setTitleId() != 0)
{
tvNestTitle!!.setText(context!!.getResources().getString(setTitleId()))
}
llBack!!.setOnClickListener(object:View.OnClickListener {
public override fun onClick(v:View) {
onBackClick()
}
})

}

/**
 * 显示编辑按钮
 *
 * @param visible
 */
     fun showBtnEidt(visible:Int) {
btnEditProfile!!.setVisibility(visible)
}


/**
 * 显示分享按钮
 *
 * @param visible
 */
     fun showBtnShare(visible:Int) {
btnShare!!.setVisibility(visible)
}


}

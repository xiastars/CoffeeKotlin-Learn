package com.summer.demo.ui.fragment

import android.view.View
import com.summer.demo.R
import com.summer.demo.module.base.BaseFragment
import com.summer.demo.view.CanvasAnimView
import com.summer.helper.utils.SAnimUtils
import com.summer.helper.utils.SUtils

/**
 * @Description: 属性动画
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/6/10 12:10
 */
class ObjectAnimFragment : BaseFragment() {

    internal val viewLeft: View by Bind(R.id.view_left)

    internal val viewTop: View by Bind(R.id.view_top)

    internal val viewRight: View by Bind(R.id.view_right)

    internal val viewBottom: View by Bind(R.id.view_bottom)

    internal val canvasAnimView: CanvasAnimView by Bind(R.id.canvas_anim)

    internal val vRotate: View by Bind(R.id.view_rotate)

    override fun initView(view: View) {
        showLeftAnim()
        SAnimUtils.rotationRepeat(vRotate)
        cirlce()
    }

    private fun showLeftAnim() {
        viewLeft!!.postDelayed({ SAnimUtils.fromLeftToShow(viewLeft, SUtils.getDip(context!!, 100).toFloat()) { hideLeft() } }, 1000)


    }

    private fun cirlce() {
        canvasAnimView!!.postDelayed(Runnable {
            if (canvasAnimView == null) {
                return@Runnable
            }
            canvasAnimView!!.setIndex()
            cirlce()
        }, 50)
    }

    private fun hideLeft() {
        SAnimUtils.fromLeftToHide(viewLeft, SUtils.getDip(context!!, 100).toFloat()) { showTop() }
    }

    private fun showTop() {
        SAnimUtils.fromTopMoveToShow(viewTop, SUtils.getDip(context!!, 100).toFloat()) { hideTop() }
    }

    private fun hideTop() {
        SAnimUtils.fromTopMoveToHide(viewTop, SUtils.getDip(context!!, 100).toFloat()) { showRight() }
    }

    private fun showRight() {
        SAnimUtils.fromRightToShow(viewRight, SUtils.getDip(context!!, 100).toFloat()) { hideRight() }
    }

    /**
     * 在右侧，从出现到隐藏
     */
    private fun hideRight() {
        SAnimUtils.fromRightToHide(viewRight, SUtils.getDip(context!!, 100).toFloat()) { showBottom() }
    }

    private fun showBottom() {
        SAnimUtils.fromBottomToShow(viewBottom, SUtils.getDip(context!!, 100).toFloat()) { hideBottom() }
    }

    private fun hideBottom() {
        SAnimUtils.fromBottomToHide(viewBottom, SUtils.getDip(context!!, 100).toFloat()) { showLeftAnim() }
    }

    public override fun loadData() {

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.ac_object_anim
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

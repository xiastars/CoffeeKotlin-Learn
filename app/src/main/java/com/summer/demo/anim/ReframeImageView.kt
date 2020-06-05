package com.summer.demo.anim

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout

/**
 * 拥有四边框的ImageView
 * @author @xiastars@vip.qq.com
 */
class ReframeImageView : View {

    internal var reframeView: ReframeView? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, atri: AttributeSet) : super(context, atri) {}

    override fun onDraw(canvas: Canvas) {
        if (reframeView == null) {
            reframeView = ReframeView(context)
            reframeView!!.setData(left, right, top, bottom)
            (parent as RelativeLayout).addView(reframeView)
        }
        super.onDraw(canvas)
    }

}

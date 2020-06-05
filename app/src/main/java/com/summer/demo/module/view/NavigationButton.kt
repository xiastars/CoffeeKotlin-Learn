package com.summer.demo.module.view

import android.content.Context
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import com.summer.demo.R

class NavigationButton : FrameLayout {
    var fragment: Fragment? = null
    var clx: Class<*>? = null
        private set
    private var mIconView: ImageView? = null
    private var mTitleView: TextView? = null
    private var mDot: TextView? = null
    private var mTag: String? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_layout_nav_item, this, true)

        mIconView = findViewById(R.id.nav_iv_icon)
        mTitleView = findViewById(R.id.nav_tv_title)
        mDot = findViewById(R.id.nav_tv_dot)
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        mIconView!!.isSelected = selected
        mTitleView!!.isSelected = selected
    }

    fun showRedDot(isShow: Boolean) {
        mDot!!.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    fun init(@DrawableRes resId: Int, @StringRes strId: Int, clx: Class<*>) {
        mIconView!!.setImageResource(resId)
        mTitleView!!.setText(strId)
        this.clx = clx
        if (this.clx != null) {
            mTag = this.clx!!.name
        }
    }

    override fun getTag(): String? {
        return mTag
    }


}

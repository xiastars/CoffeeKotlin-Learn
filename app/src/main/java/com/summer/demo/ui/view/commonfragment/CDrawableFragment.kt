package com.summer.demo.ui.view.commonfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.summer.demo.R
import com.summer.demo.ui.fragment.BaseSimpleFragment

/**
 * 自定义Drawable的一些用法，看layout布局
 * @author Administrator
 */
class CDrawableFragment : BaseSimpleFragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cdrawable, null)
    }

}

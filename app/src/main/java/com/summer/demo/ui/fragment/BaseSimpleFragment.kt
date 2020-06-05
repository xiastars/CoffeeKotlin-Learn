package com.summer.demo.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

open class BaseSimpleFragment : Fragment() {
    protected lateinit var llParent: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (container != null) {
            llParent = container
        }
        initView()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    protected open fun initView() {}

}

package com.summer.demo.ui.main

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import com.summer.demo.module.base.BaseFragment

abstract class BaseMainFragment : BaseFragment() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    fun showTouristView(): Boolean {
        return false
    }

    fun firstRefreshView() {

    }


}

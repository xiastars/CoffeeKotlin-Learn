package com.summer.demo.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.summer.demo.R

/**
 * Created by xiaqiliang on 2016年12月29日 17:38.
 */
class DialogWeixin( context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.dialog_weixin)
    }

}
package com.summer.demo.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.summer.demo.R

/**
 * Toast的用法
 * @author Administrator
 */
class ToastFragment : BaseSimpleFragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_toast, null)
        initView(view)
        return view
    }

    /**
     * Fragment里面的findViewId由onCreateView返回的View来寻找
     * @param view
     */
    private fun initView(view: View) {
        val btnCommon = view.findViewById<View>(R.id.btn_common) as Button
        btnCommon.setOnClickListener {
            /* 第三个参数是时间长短，Toast.LENGTH_SHORT为300毫秒，Toast.LENGTH_LONG为1000毫秒
				 * 这里的时间可以自定义 ，比如直接传100*/
            Toast.makeText(context, "这是一个Toast", Toast.LENGTH_SHORT).show()
        }

        val btnLonger = view.findViewById<View>(R.id.btn_longer) as Button
        btnLonger.setOnClickListener { Toast.makeText(context, "这是一个Toast", Toast.LENGTH_LONG).show() }

        val btnSpecial = view.findViewById<View>(R.id.btn_special) as Button
        btnSpecial.setOnClickListener { makeSpeciaToast("这是自定义的Toast") }
    }

    @SuppressLint("NewApi")
    private fun makeSpeciaToast(content: String) {
        val toast = Toast(context)
        val textView = TextView(context)
        textView.background = context!!.resources.getDrawable(R.drawable.so_rede5_90)
        textView.text = content
        textView.setPadding(40, 15, 40, 15)
        toast.view = textView
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }

}

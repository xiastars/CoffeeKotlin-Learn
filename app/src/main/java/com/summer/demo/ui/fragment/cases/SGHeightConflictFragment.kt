package com.summer.demo.ui.fragment.cases

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.summer.demo.R
import com.summer.demo.ui.fragment.BaseSimpleFragment
import com.summer.demo.view.CustomScrollView
import com.summer.helper.utils.SUtils
import com.summer.helper.view.NRecycleView

/**
 * *Created by summer on 2016年12月14日 16:24.
 */

class SGHeightConflictFragment : BaseSimpleFragment(), View.OnClickListener {
    internal lateinit var rlDownload: LinearLayout
    internal lateinit var llDelete: LinearLayout
    internal lateinit var scrollView: CustomScrollView
    internal lateinit var llTop: LinearLayout
    internal lateinit var svBooks: NRecycleView
    internal lateinit var shelfAdapter: SGHeightConflictAdapter
    internal lateinit var tvCount: TextView
    internal lateinit var tvDelete: TextView
    internal var size = 102

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sgconflict, null)
        initView(view)
        return view
    }

    override fun onPause() {
        super.onPause()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initView(view: View) {
        rlDownload = view.findViewById<View>(R.id.bookshelf_download) as LinearLayout
        SUtils.clickTransColor(rlDownload)
        tvCount = view.findViewById<View>(R.id.tv_book_count) as TextView
        rlDownload.setOnClickListener(this)
        scrollView = view.findViewById<View>(R.id.scrollview) as CustomScrollView
        llTop = view.findViewById<View>(R.id.ll_top) as LinearLayout
        svBooks = view.findViewById<View>(R.id.bookshelf_gridview) as NRecycleView
        svBooks.setGridView(4)
        shelfAdapter = SGHeightConflictAdapter(context!!)
        svBooks.adapter = shelfAdapter
        tvDelete = view.findViewById<View>(R.id.tv_delete) as TextView
        llDelete = view.findViewById<View>(R.id.ll_delete) as LinearLayout
        SUtils.clickTransColor(llDelete)
        llDelete.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ll_delete -> {
                var downloadstate = shelfAdapter.deleteState
                if (!downloadstate) {
                    downloadstate = true
                    tvDelete.text = "取消"
                } else {
                    downloadstate = false
                    tvDelete.text = "删除"
                }
                shelfAdapter.cancelDeleteState(downloadstate)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val addone = size % 4
        var lines = size / 4
        if (addone != 0) {
            lines++
        }
        val pa = svBooks.layoutParams as LinearLayout.LayoutParams
        val lastHeight = (lines * context!!.resources.getDimension(R.dimen.size_355_5) + context!!.resources.getDimension(R.dimen.size_25)).toInt()
        /* 不让GridView自动滚动的方法就是先设置一个比较短的高度，再设置全部高度，测试方法，注释下面一行代码 */
        pa.height = 400
        Handler().postDelayed({
            pa.height = lastHeight
            svBooks.requestLayout()
            svBooks.invalidate()
        }, 20)

        shelfAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        scrollView.setTopView(llTop)
    }
}

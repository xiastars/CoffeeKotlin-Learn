package com.summer.demo.ui.view.customfragment

import android.view.View
import butterknife.BindView
import com.summer.demo.R
import com.summer.demo.bean.BookBean
import com.summer.demo.module.base.BaseFragment
import com.summer.demo.ui.view.adapter.FunQuestionGalleryAdapter
import com.summer.demo.view.GalleryView
import java.util.*

/**
 * @Description: 横向滚动Gallery
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/10 17:07
 */
class GalleryFragment : BaseFragment() {
    @BindView(R.id.gallery_view)
    internal var galleryView: GalleryView? = null

    override fun initView(view: View) {
        val mFunQuestionAdapter = FunQuestionGalleryAdapter(context!!)
        galleryView!!.adapter = mFunQuestionAdapter
        val bookBeans = ArrayList<BookBean>()
        for (i in 0..9) {
            bookBeans.add(BookBean())
        }
        mFunQuestionAdapter.notifyDataSetChanged(bookBeans)

        myHandlder.postDelayed({ galleryView!!.scrollToPosition(2) }, 300)
    }

    public override fun loadData() {


    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.fragment_gallery
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

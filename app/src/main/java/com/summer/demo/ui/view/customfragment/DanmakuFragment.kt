package com.summer.demo.ui.view.customfragment

import android.view.View
import com.summer.demo.R
import com.summer.demo.bean.DanmakuInfo
import com.summer.demo.module.base.BaseFragment
import com.summer.demo.ui.module.comment.CommentDialog
import com.summer.demo.ui.module.comment.OnCommentedListener
import com.summer.demo.ui.module.comment.StarCommentInfo
import com.summer.demo.view.SimpleDanmakuView
import com.summer.helper.utils.Logs
import java.util.*

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/11 10:26
 */
class DanmakuFragment : BaseFragment() {
    internal val rlDanmaku: SimpleDanmakuView by Bind(R.id.rl_danmaku)

    override fun initView(view: View) {
        val danmakuInfos = ArrayList<DanmakuInfo>()
        for (i in 0..99) {
            val danmakuInfo = DanmakuInfo()
            danmakuInfo.img = "https://img02.sogoucdn.com/app/a/100520021/34dc6e45a0908f6d5554ab655748bafb"
            danmakuInfo.name = "丁香空结雨中愁"
            danmakuInfos.add(danmakuInfo)
        }
        rlDanmaku!!.addDatas(danmakuInfos, 0)

        val commentDialog = CommentDialog(context!!, "", object : OnCommentedListener {
            override fun onSucceed(info: StarCommentInfo) {

            }
        })
        commentDialog.show()
    }

    public override fun loadData() {

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.fragment_danmaku
    }

    override fun onStop() {
        super.onStop()
        rlDanmaku!!.stopPlay()
        Logs.i("停止0--")
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

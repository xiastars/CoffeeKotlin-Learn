package com.summer.demo.ui.mine

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.summer.demo.R
import com.summer.demo.ui.SettingActivity
import com.summer.demo.ui.main.BaseMainFragment
import com.summer.helper.utils.JumpTo
import com.summer.helper.view.RoundAngleImageView

/**
 * @Description: 个人页面
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/9 10:35
 */
class MineFragment : BaseMainFragment(), View.OnClickListener {
    private val rlGotoAccount: RelativeLayout by Bind(R.id.rl_goto_account)
    private val ivSetting: ImageView by Bind(R.id.iv_setting)
    private val tvName: TextView by Bind(R.id.tv_name)
    private val tvIntro: TextView by Bind(R.id.tv_intro)
    private val ivArrow: ImageView by Bind(R.id.iv_arrow)
    private val ivAvatar: RoundAngleImageView by Bind(R.id.iv_avatar)
    private val rlUserInfo: RelativeLayout by Bind(R.id.rl_user_info)
    private val llEdit: LinearLayout by Bind(R.id.ll_edit)
    private val tvRealStatus: TextView by Bind(R.id.tv_real_status)
    private val tvTopicCount: TextView by Bind(R.id.tv_topic_count)
    private val llTopic: LinearLayout by Bind(R.id.ll_topic)

    override fun initView(view: View) {

    }

    public override fun loadData() {

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.fragment_mine
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_setting -> JumpTo.getInstance().commonJump(getContext(), SettingActivity::class.java)
        }
    }
}

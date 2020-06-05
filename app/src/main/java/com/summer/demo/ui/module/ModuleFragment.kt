package com.summer.demo.ui.module

import android.view.View
import com.summer.demo.R
import com.summer.demo.adapter.CommonGridAdapter
import com.summer.demo.bean.ModuleInfo
import com.summer.demo.ui.main.BaseMainFragment
import com.summer.helper.listener.OnSimpleClickListener
import com.summer.helper.utils.JumpTo
import com.summer.helper.view.NRecycleView
import java.util.*

/**
 * @Description: 模块Fragment
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/9 10:32
 */
class ModuleFragment : BaseMainFragment() {
    val moduleInfos = ArrayList<ModuleInfo>()
    private val svContainer: NRecycleView by Bind(R.id.sv_container)
    internal lateinit var adapter: CommonGridAdapter

    override fun initView(view: View) {
        svContainer.setGridView(3)
        svContainer.setDivider()
        adapter = CommonGridAdapter(context!!, OnSimpleClickListener { position -> clickChild(position) })
        svContainer.adapter = adapter

        moduleInfos.add(ModuleInfo(R.drawable.ic_network, "网络请求", ModulePos.POS_NETWORK))
        moduleInfos.add(ModuleInfo(R.drawable.ic_module_animation, "帧动画", ModulePos.POS_FRAME))
        moduleInfos.add(ModuleInfo(R.drawable.ic_module_transition, "属性动画", ModulePos.POS_ANIM))
        moduleInfos.add(ModuleInfo(R.drawable.ic_module_dialog, "弹窗", ModulePos.POS_DIALOG))
        moduleInfos.add(ModuleInfo(R.drawable.ic_view_text, "视频裁剪", ModulePos.POS_VIDEO_CUTTER))
        moduleInfos.add(ModuleInfo(R.drawable.ic_module_navigatior, "Webview网页", ModulePos.POS_WEBVIEW))
        moduleInfos.add(ModuleInfo(R.drawable.ic_module_picker, "颜色选择器", ModulePos.POS_COLOR_PICKER))
        moduleInfos.add(ModuleInfo(R.drawable.ic_module_chat, "聊天", ModulePos.POS_CHAT))
        moduleInfos.add(ModuleInfo(R.drawable.ic_biaoqing, "表情", ModulePos.POS_EMOJI))
        moduleInfos.add(ModuleInfo(R.drawable.ic_view_list, "yasuo", ModulePos.POS_COMPRESS_IMG))
        moduleInfos.add(ModuleInfo(R.drawable.ic_module_audio, "音频播放", ModulePos.POS_AUDIO_PLAY))
        moduleInfos.add(ModuleInfo(R.drawable.ic_module_socket, "Socket通讯", ModulePos.POS_SOCKET))
        moduleInfos.add(ModuleInfo(R.drawable.ic_module_vibrate, "振动", ModulePos.POS_VIBRATE))
        adapter.notifyDataChanged(moduleInfos)
    }

    private fun clickChild(position: Int) {
        when (position) {
            ModulePos.POS_COLOR_PICKER -> {

            }
            else -> JumpTo.getInstance().commonJump(getContext(), ModuleContainerActivity::class.java, moduleInfos[position].pos)
        }

    }

    public override fun loadData() {

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.view_nrecyleview
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

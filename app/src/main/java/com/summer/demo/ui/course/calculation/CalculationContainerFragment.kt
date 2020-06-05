package com.summer.demo.ui.course.calculation

import android.view.View
import butterknife.BindView
import com.summer.demo.R
import com.summer.demo.adapter.CommonGridAdapter
import com.summer.demo.bean.ModuleInfo
import com.summer.demo.ui.main.BaseMainFragment
import com.summer.demo.ui.module.ModuleContainerActivity
import com.summer.demo.ui.module.ModulePos
import com.summer.demo.ui.module.colorpicker.AmbilWarnaDialog
import com.summer.demo.ui.module.colorpicker.AmbilWarnaDialog.OnAmbilWarnaListener
import com.summer.helper.listener.OnSimpleClickListener
import com.summer.helper.utils.JumpTo
import com.summer.helper.view.NRecycleView
import java.util.*

/**
 * @Description: 计算模块
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/9 10:32
 */
class CalculationContainerFragment : BaseMainFragment() {
    @BindView(R.id.sv_container)
    internal var svContainer: NRecycleView? = null

    internal lateinit var adapter: CommonGridAdapter

    override fun initView(view: View) {
        svContainer!!.setGridView(3)
        svContainer!!.setDivider()

        adapter = CommonGridAdapter(view.context, OnSimpleClickListener { position -> clickChild(position) })
        svContainer!!.adapter = adapter
        val moduleInfos = ArrayList<ModuleInfo>()
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
                val ambilWarnaDialog = AmbilWarnaDialog(getContext(), getResColor(R.color.red_d3), OnAmbilWarnaListener { })
                ambilWarnaDialog.show()
            }
            else -> JumpTo.getInstance().commonJump(getContext(), ModuleContainerActivity::class.java, position)
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

package com.summer.demo.ui.module

import android.app.Activity
import android.content.Intent
import com.summer.demo.module.album.AlbumActivity
import com.summer.demo.ui.FragmentContainerActivity
import com.summer.demo.ui.fragment.MyDialogFragment
import com.summer.demo.ui.fragment.ObjectAnimFragment
import com.summer.demo.ui.module.comment.ChatFragment
import com.summer.demo.ui.module.fragment.*
import com.summer.demo.ui.module.fragment.socket.SocketFragment
import com.summer.helper.utils.JumpTo
import com.summer.helper.utils.SFileUtils

/**
 * @Description: Fragment容器
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/9 11:44
 */
class ModuleContainerActivity : FragmentContainerActivity() {

    override fun showViews(type: Int) {
        when (type) {
            ModulePos.POS_FRAME -> {
                title = "帧动画"
                showFragment(FrameAnimFragment())
            }
            ModulePos.POS_ANIM -> {
                title = "属性动画"
                showFragment(ObjectAnimFragment())
            }
            ModulePos.POS_DIALOG -> {
                title = "弹窗"
                showFragment(MyDialogFragment())
            }
            ModulePos.POS_VIDEO_CUTTER -> {
                val intent = Intent(context, AlbumActivity::class.java)
                intent.putExtra(JumpTo.TYPE_INT, 1)
                intent.putExtra(JumpTo.TYPE_STRING, SFileUtils.FileType.FILE_MP4)
                (context as Activity).startActivityForResult(intent, 12)
            }
            ModulePos.POS_WEBVIEW -> {
                title = "Webview网页"
                showFragment(WebLeanFragment())
            }
            ModulePos.POS_CHAT -> {
                title = "周杰伦"
                showFragment(ChatFragment())
            }
            ModulePos.POS_EMOJI -> showFragment(EmojiFragment())
            ModulePos.POS_COMPRESS_IMG -> showFragment(CompressImgFragment())
            ModulePos.POS_AUDIO_PLAY -> {
                title = "音频播放"
                showFragment(AudioPlayerFragment())
            }
            ModulePos.POS_SOCKET -> {
                title = "原生Socket"
                showFragment(SocketFragment())
            }
            ModulePos.POS_VIBRATE -> {
                title = "振动"
                showFragment(VibratorFragment())
            }
            ModulePos.POS_NETWORK ->{
                title = "网络请求"
                showFragment(NetworkFragment())
            }
        }
    }

}

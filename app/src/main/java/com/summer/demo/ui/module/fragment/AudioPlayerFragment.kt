package com.summer.demo.ui.module.fragment

import android.view.View
import android.widget.Button
import com.summer.demo.R
import com.summer.demo.helper.PlayAudioHelper
import com.summer.demo.listener.OnAudioPlayListener
import com.summer.demo.module.base.BaseFragment

/**
 * 音频播放示例
 *
 * @author xiastars@vip.qq.com
 */
class AudioPlayerFragment : BaseFragment(), View.OnClickListener {
    private val btnCommonRaw: Button by Bind(R.id.btn_common_raw, true)
    private val btnCommon: Button by Bind(R.id.btn_common, true)
    private val btnLonger: Button by Bind(R.id.btn_longer, true)
    private val btnSpecial: Button by Bind(R.id.btn_special, true)

    internal var demoUrl = "http://cdn.ishuidi.com.cn/xsd/%E5%88%9B%E8%AF%BE%E8%B5%84%E6%BA%90/%E5%A3%B0%E9%9F%B3/%E8%8B%B1%E6%96%87%E5%84%BF%E6%AD%8C/%E3%80%8AHead,%20Shoulders,%20Knees%20and%20Toes%E3%80%8B/%E3%80%8AHead,%20Shoulders,%20Knees%20and%20Toes%E3%80%8B.mp3"
    internal lateinit var playAudioHelper: PlayAudioHelper

    override fun setContentView(): Int {
        return R.layout.fragment_mediaplayer
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_common_raw -> playAudioHelper.playRaw(R.raw.bi)
            R.id.btn_common//播放
            -> playAudioHelper.playMediaFile(demoUrl)
            R.id.btn_longer//暂停
            -> playAudioHelper.pausePlayingAudio()
            R.id.btn_special//继续播放
            -> playAudioHelper.restartPlayingAudio()
        }//playMediaFile(demoUrl);
    }


    override fun initView(view: View) {
        playAudioHelper = PlayAudioHelper(object : OnAudioPlayListener {
            override fun onStart() {

            }

            override fun onComplete() {

            }
        })
    }

    public override fun loadData() {

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    /**
     * 当前Activity关闭时，停止音乐播放
     */
    override fun onStop() {
        super.onStop()
        playAudioHelper.stopPlayingAudio()
    }


}

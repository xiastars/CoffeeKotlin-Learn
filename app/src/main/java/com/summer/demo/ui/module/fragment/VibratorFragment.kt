package com.summer.demo.ui.module.fragment

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.Button
import com.summer.demo.AppContext
import com.summer.demo.R
import com.summer.demo.module.base.BaseFragment

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/31 18:28
 */
class VibratorFragment : BaseFragment(), View.OnClickListener {
    internal lateinit var vibrator: Vibrator

    private val btnVibrate : Button by Bind(R.id.btn_virate,true)
    private val btnCancel : Button by Bind(R.id.btn_cancel,true)

    internal var mVibratePattern1 = longArrayOf(0, 90, 10, 90, 10, 90, 10, 90, 10, 90, 10, 90, 10, 90, 10, 90)
    internal var mVibratePattern2 = longArrayOf(0, 80, 20, 80, 20, 80, 20, 80, 20, 80, 20, 80, 20, 80, 20, 80)
    internal var mVibratePattern3 = longArrayOf(0, 70, 30, 70, 30, 70, 30, 70, 30, 70, 30, 70, 30, 70, 30, 70)
    internal var mVibratePattern4 = longArrayOf(0, 60, 40, 60, 40, 60, 40, 60, 40, 60, 40, 60, 40, 60, 40, 60)
    internal var mVibratePattern5 = longArrayOf(0, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50)
    internal var mVibratePattern6 = longArrayOf(0, 40, 60, 40, 60, 40, 60, 40, 60, 40, 60, 40, 60, 40, 60, 40)
    internal var mVibratePattern7 = longArrayOf(0, 30, 70, 30, 70, 30, 70, 30, 70, 30, 70, 30, 70, 30, 70, 30)
    internal var mVibratePattern8 = longArrayOf(0, 20, 80, 20, 80, 20, 80, 20, 80, 20, 80, 20, 80, 20, 80, 20)
    internal var mVibratePattern9 = longArrayOf(0, 10, 90, 10, 90, 10, 90, 10, 90, 10, 90, 10, 90, 10, 90, 10)

    internal var mVibratePattern10 = longArrayOf(0, 3000, 2000, 3000, 2000, 3000, 2000, 3000, 2000, 3000, 2000, 3000, 2000, 3000, 2000, 3000, 2000, 3000, 2000, 3000, 2000, 3000, 2000, 3000, 2000)
    internal var mAmplitudes2 = intArrayOf(0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0)

    internal var mAmplitudes = intArrayOf(0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255)

    override fun initView(view: View) {
        vibrator = AppContext.instance!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    public override fun loadData() {

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.fragment_vibrate
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_virate -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(createWaveShot(true, mVibratePattern9))
            } else {
                vibrator.vibrate(mVibratePattern1, 0)
            }
            R.id.btn_cancel -> vibrator.cancel()
        }
    }

    /**
     * 创建波形振动
     * @param repeat 是否重复，
     * @param timings
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createWaveShot(repeat: Boolean, timings: LongArray): VibrationEffect {
        return VibrationEffect.createWaveform(timings, if (repeat) 0 else -1)
    }

    /**
     * 创建波形振动
     * @param repeat 是否重复，
     * @param timings
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createWaveShot(repeat: Boolean, timings: LongArray, amplitude: IntArray): VibrationEffect {
        return VibrationEffect.createWaveform(timings, amplitude, if (repeat) 0 else -1)
    }

    /**
     * 创建一次振动
     *
     * @param secondTime 振动时间
     * @param amplitude  振动幅度1-255
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createOneShot(secondTime: Int, amplitude: Int): VibrationEffect {
        return VibrationEffect.createOneShot(secondTime.toLong(), amplitude)
    }

    override fun onStop() {
        super.onStop()
        vibrator.cancel()
    }
}

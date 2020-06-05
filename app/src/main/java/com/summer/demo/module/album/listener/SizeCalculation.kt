package com.summer.demo.module.album.listener

import com.ghnor.flora.spec.calculation.Calculation

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/21 17:45
 */
open class SizeCalculation : Calculation() {

    override fun calculateInSampleSize(srcWidth: Int, srcHeight: Int): Int {

        return 10
    }
}

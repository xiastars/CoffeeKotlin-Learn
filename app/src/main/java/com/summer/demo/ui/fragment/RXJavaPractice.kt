package com.summer.demo.ui.fragment


import com.summer.demo.R
import com.summer.demo.view.DragLayer
import rx.Observable

import rx.Subscriber

/**     #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #                                                   # */

/**
 * View的拖动示例
 */
class RXJavaPractice : BaseSimpleFragment() {
    internal lateinit var dragLayer: DragLayer

    override fun initView() {
        dragLayer = context?.let { DragLayer(it) }!!
        llParent.addView(dragLayer)
        dragLayer.addBackgroundView()
        dragLayer.setBackgroundResource(R.drawable.background1)

        val observable = rx.Observable.create(rx.Observable.OnSubscribe<String> { subscriber ->
            subscriber.onNext("....")
            subscriber.onCompleted()
        })

        val mySubscribe = object : Subscriber<String>() {
            override fun onCompleted() {

            }

            override fun onError(e: Throwable) {

            }

            override fun onNext(s: String) {
                print(s)
            }
        }
        observable.subscribe(mySubscribe)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        dragLayer.removeAllViews()
    }

}

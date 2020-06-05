package com.summer.helper.utils;

import android.view.View;

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/6/13 16:30
 */
public class AnimManager {
    View view;


    public AnimManager onView(View view){
        this.view = view;
        return this;
    }




    /**
     * 向左偏移
     *
     * @param view
     * @param offset
     */
    public static void hideTop(View view, float offset) {
        //showPropertyAnim(view, View.GONE, "translationY", offset, 0f, 0f, 300);
    }

}

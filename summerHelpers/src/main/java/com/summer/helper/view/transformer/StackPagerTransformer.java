package com.summer.helper.view.transformer;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by xiaqiliang on 2017/4/13.
 */
public class StackPagerTransformer extends BasePageTransformer {

    private int maxTranslateOffsetX;
    private ViewPager viewPager;
    float curPosition;

    public StackPagerTransformer() {
    }

    public void transformPage(View view, float position) {


    }

    @Override
    protected void pageTransform(View view, float position) {
        if(position != curPosition)
        view.setTranslationX(position < 0 ? 0f : -view.getWidth() * position);
        curPosition = position;
    }

}

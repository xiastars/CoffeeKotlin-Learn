package com.summer.demo.module.base.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.summer.helper.view.PagerSlidingTabStrip;

import java.util.List;

public class VFragmentPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.ViewTabProvider {

    private List<Fragment> mFragments;
    private List<? extends View> mTabViews;

    public VFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<? extends View> tabViews) {
        super(fm);
        mFragments = fragments;
        mTabViews = tabViews;
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public int getCount() {
        return mFragments==null ? 0 : mFragments.size();
    }

    @Override
    public View getPageView(int position) {
        return mTabViews.get(position);
    }

}

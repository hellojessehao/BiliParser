package com.android.jesse.biliparser.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @Description: 通用tablayout+viewpager适配器
 * @author: zhangshihao
 * @date: 2020/4/27
 */
public class CommonTabFragmentAdapter extends FragmentPagerAdapter {

    private static final String TAG = CommonTabFragmentAdapter.class.getSimpleName();

    private List<Fragment> fragments;
    private String[] titleArr;

    public CommonTabFragmentAdapter(List<Fragment> fragments, FragmentManager fm) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return fragments==null?0:fragments.size();
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(titleArr == null || titleArr.length == 0){
            return super.getPageTitle(position);
        }
        return titleArr[position];
    }

    public String[] getTitleArr() {
        return titleArr;
    }

    public void setTitleArr(String[] titleArr) {
        this.titleArr = titleArr;
    }
}

package com.wanglijun.xiami.xiami;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by wanglijun on 2018/4/28.
 */

public class XiamiPagerAdapter extends FragmentPagerAdapter {


    public List<String> getTitles() {
        return mTitles;
    }

    private List<String> mTitles;
    private List<Fragment> mlist;

    public XiamiPagerAdapter(FragmentManager fm, List<Fragment> list, List<String> titles) {
        super(fm);
        this.mlist = list;
        this.mTitles = titles;

        if (list == null || titles == null || list.size() != titles.size()) {
            throw  new IllegalArgumentException("fragments not match titles");
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mlist.get(position);//显示第几个页面
    }

    @Override
    public int getCount() {
        return mlist.size();//有几个页面
    }
}

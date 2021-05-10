package com.atom.shuoshuo.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.ui.fragment.DuanziFragment;
import com.atom.shuoshuo.ui.fragment.MeiziFragment;
import com.atom.shuoshuo.ui.fragment.PictureFragment;
import com.atom.shuoshuo.ui.fragment.QiubaiFragment;
import com.atom.shuoshuo.ui.fragment.VideoFragment;
import com.atom.shuoshuo.ui.fragment.ZhihuFragment;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/7/31  16:05
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class HomePagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES;
    private Fragment[] mFragments;

    public HomePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        TITLES = context.getResources().getStringArray(R.array.titles);
        mFragments = new Fragment[TITLES.length];
    }

    @Override
    public Fragment getItem(int position) {
       if(mFragments[position] == null){
           switch (position){
               case 0:
                   mFragments[position] = ZhihuFragment.newInstance();
                   break;
               case 1:
                   mFragments[position] = PictureFragment.newInstance();
                   break;
               case 2:
                   mFragments[position] = MeiziFragment.newInstance();
                   break;
               case 3:
                   mFragments[position] = VideoFragment.newInstance();
                   break;
               case 4:
                   mFragments[position] = QiubaiFragment.newInstance();
                   break;
               case 5:
                   mFragments[position] = DuanziFragment.newInstance();
                   break;
               default:
                   break;
           }
       }
       return mFragments[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }
}

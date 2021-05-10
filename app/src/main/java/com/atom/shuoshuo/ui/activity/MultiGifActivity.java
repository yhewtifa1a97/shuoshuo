package com.atom.shuoshuo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.base.AbsBaseActivity;
import com.atom.shuoshuo.ui.fragment.MultiGifFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.ui.activity
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/8/1  14:12
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class MultiGifActivity extends AbsBaseActivity {

    private static final String MULTI_IMAGE_URL = "MULTI_IMAGE_URL";
    private static final String MULTI_IMAGE_POS = "MULTI_IMAGE_POS";
    private static final String MULTI_IMAGE_CURRENT_POS = "MULTI_IMAGE_CURRENT_POS";
    private static final String MULTI_IMAGE_WIDTH = "MULTI_IMAGE_WIDTH";
    private static final String MULTI_IMAGE_HEIGHT = "MULTI_IMAGE_HEIGHT";

    @BindView(R.id.picture_multi_pager)
    ViewPager mPictureMultiPager;
    @BindView(R.id.picture_multi_pager_bottom)
    TextView mPictureMultiPagerBottom;

    private int currentPos;
    private int width;
    private int height;
    private List<String> mListUrl;


    public static void startActivity(Context context, int pos, ArrayList<String> listurl, int width, int height) {
        Intent intent = new Intent(context, MultiGifActivity.class);

        intent.putExtra(MULTI_IMAGE_POS, pos);
        intent.putStringArrayListExtra(MULTI_IMAGE_URL, listurl);
        intent.putExtra(MULTI_IMAGE_WIDTH, width);
        intent.putExtra(MULTI_IMAGE_HEIGHT, height);
        context.startActivity(intent);
        ((MainActivity) context).overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.activity_multi_picture;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentPos = savedInstanceState.getInt(MULTI_IMAGE_CURRENT_POS);
            width = savedInstanceState.getInt(MULTI_IMAGE_WIDTH);
            height = savedInstanceState.getInt(MULTI_IMAGE_HEIGHT);
        } else {
            currentPos = getIntent().getIntExtra(MULTI_IMAGE_POS, 0);
            width = getIntent().getIntExtra(MULTI_IMAGE_WIDTH, 0);
            height = getIntent().getIntExtra(MULTI_IMAGE_HEIGHT, 0);
        }

        mListUrl = getIntent().getStringArrayListExtra(MULTI_IMAGE_URL);

        mPictureMultiPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return MultiGifFragment.newInstance(mListUrl.get(position), width, height);
            }

            @Override
            public int getCount() {
                return mListUrl.size();
            }
        });

        mPictureMultiPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mPictureMultiPagerBottom.setText(getString(R.string.picture_conut, position + 1, mListUrl.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mPictureMultiPager.setCurrentItem(currentPos);
        mPictureMultiPagerBottom.setText(getString(R.string.picture_conut, currentPos + 1, mListUrl.size()));
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(MULTI_IMAGE_CURRENT_POS, currentPos);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }
}

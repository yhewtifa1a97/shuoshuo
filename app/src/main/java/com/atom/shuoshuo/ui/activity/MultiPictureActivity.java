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
import com.atom.shuoshuo.ui.fragment.MultiPictureFragment;
import com.atom.shuoshuo.utils.SnackbarUtil;

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
 * createTime: 2017/8/1  15:07
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class MultiPictureActivity extends AbsBaseActivity {


    private static final String MULTI_IMAGE_URL = "MULTI_IMAGE_URL";
    private static final String MULTI_IMAGE_POS = "MULTI_IMAGE_POS";
    private static final String MULTI_IMAGE_CURRENT_POS = "MULTI_IMAGE_CURRENT_POS";

    public static void startActivity(Context context, int pos, ArrayList<String> list) {
        Intent intent = new Intent(context, MultiPictureActivity.class);
        intent.putExtra(MULTI_IMAGE_POS, pos);
        intent.putStringArrayListExtra(MULTI_IMAGE_URL, list);
        context.startActivity(intent);
        ((MainActivity) context).overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    private int currentPos;
    private List<String> mListUrl;

    @BindView(R.id.picture_multi_pager)
    ViewPager mViewPager;

    @BindView(R.id.picture_multi_pager_bottom)
    TextView mTextView;

    @Override
    protected int getResLayoutId() {
        return R.layout.activity_multi_picture;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentPos = savedInstanceState.getInt(MULTI_IMAGE_CURRENT_POS);
        } else {
            currentPos = getIntent().getIntExtra(MULTI_IMAGE_POS, 0);
        }

        mListUrl = getIntent().getStringArrayListExtra(MULTI_IMAGE_URL);

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return MultiPictureFragment.newInstance(mListUrl.get(position));
            }

            @Override
            public int getCount() {
                return mListUrl.size();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mTextView.setText(getString(R.string.picture_conut, position + 1, mListUrl.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mViewPager.setCurrentItem(currentPos);
        mTextView.setText(getString(R.string.picture_conut, currentPos + 1, mListUrl.size()));

        SnackbarUtil.showMessage(mViewPager, "单击图片返回, 双击放大, 长按图片保存");
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

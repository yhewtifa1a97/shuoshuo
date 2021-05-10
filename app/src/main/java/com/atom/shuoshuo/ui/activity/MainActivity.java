package com.atom.shuoshuo.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.atom.shuoshuo.adapter.HomePagerAdapter;
import com.atom.shuoshuo.R;
import com.atom.shuoshuo.base.AbsBaseActivity;
import com.flyco.tablayout.SlidingTabLayout;

import butterknife.BindView;

public class MainActivity extends AbsBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tablayout)
    SlidingTabLayout mTablayout;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.main_cl)
    CoordinatorLayout mMainCl;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.root_dl)
    DrawerLayout mRootDl;

    private HomePagerAdapter mHomeAdapter;

    @Override
    protected int getResLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mRootDl, mToolbar, R.string.open, R.string.close);
        actionBarDrawerToggle.syncState();
        mRootDl.addDrawerListener(actionBarDrawerToggle);
        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });


        mHomeAdapter = new HomePagerAdapter(getSupportFragmentManager(), this);

        mViewpager.setOffscreenPageLimit(5);
        mViewpager.setAdapter(mHomeAdapter);
        mTablayout.setViewPager(mViewpager);
    }

    private void selectDrawerItem(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.nav_clean:
                cleanCache();
                break;

            case R.id.nav_setting:
                AboutMeActivity.startActivity(this);
                mRootDl.closeDrawers();
                break;
        }
    }

    private void cleanCache() {

    }

    private long exitTime;

    @Override
    public void onBackPressed() {
        if (mRootDl.isDrawerOpen(GravityCompat.START)) {
            mRootDl.closeDrawers();
            return;
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再点一次，退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }
}

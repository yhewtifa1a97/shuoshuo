package com.atom.shuoshuo.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.base.AbsBaseFragment;
import com.atom.shuoshuo.ui.activity.MultiGifActivity;
import com.atom.shuoshuo.utils.AlxGifHelper;
import com.atom.shuoshuo.utils.AppUtil;

import butterknife.BindView;
import pl.droidsonroids.gif.GifImageView;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.ui.fragment
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/8/1  15:43
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class MultiGifFragment extends AbsBaseFragment {

    @BindView(R.id.gif_photo_view)
    GifImageView mGifPhotoView;
    @BindView(R.id.tv_progress)
    TextView mTvProgress;
    @BindView(R.id.progress_wheel)
    ProgressBar mProgressWheel;

    private String imageUrl;
    private int width;
    private int height;

    public static Fragment newInstance(String imageUrl, int width, int height) {
        Bundle bundle = new Bundle();
        bundle.putString("url", imageUrl);
        bundle.putInt("width", width);
        bundle.putInt("height", height);
        MultiGifFragment fragment = new MultiGifFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUrl = getArguments() != null ? getArguments().getString("url") : null;
        width = getArguments() != null ? getArguments().getInt("width", 0) : 0;
        height = getArguments() != null ? getArguments().getInt("height", 0) : 0;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_multi_gif;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mGifPhotoView.getLayoutParams();
        lp.height = AppUtil.getScreenWidth() * height / width;
        mGifPhotoView.setLayoutParams(lp);

        final String path = AlxGifHelper.displayImage(imageUrl, mGifPhotoView, mProgressWheel, mTvProgress);

        mGifPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MultiGifActivity) getActivity()).onBackPressed();
            }
        });

        mGifPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialog();
                return true;
            }
        });
    }

    private void createDialog() {
        new AlertDialog.Builder(getContext())
                .setMessage("保存到手机?")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlxGifHelper.saveGIF(mGifPhotoView, imageUrl, mProgressWheel);
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    @Override
    protected void lazyLoad() {

    }
}

package com.atom.shuoshuo.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.base.AbsBaseActivity;
import com.atom.shuoshuo.utils.SnackbarUtil;
import com.atom.shuoshuo.widget.ZoomImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import butterknife.BindView;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.ui.activity
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/8/1  18:00
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class MeiziActivity extends AbsBaseActivity {

    public static final String EXTRA_IMAGE_URL = "image";
    public static final String TRANSIT_LOCATIONX = "locationX";
    public static final String TRANSIT_LOCATIONY = "locationY";
    public static final String TRANSIT_WIDTH = "width";
    public static final String TRANSIT_HEIGHT = "hieght";

    @BindView(R.id.zoom_iv)
    ZoomImageView mPicture;

    private Bitmap mBitmap; // 保存图片时使用
    private String mImageUrl;
    private int mLocationX;
    private int mLocationY;
    private int mWidth;
    private int mHeight;

    @Override
    protected int getResLayoutId() {
        return R.layout.activity_meizi;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mImageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        mLocationX = getIntent().getIntExtra(TRANSIT_LOCATIONX, 0);
        mLocationY = getIntent().getIntExtra(TRANSIT_LOCATIONY, 0);
        mWidth = getIntent().getIntExtra(TRANSIT_WIDTH, 0);
        mHeight = getIntent().getIntExtra(TRANSIT_HEIGHT, 0);

        mPicture.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
        mPicture.transformIn();
        setContentView(mPicture);
        setupPhotoAttacher();
        Glide.with(this)
                .load(mImageUrl)
                .asBitmap()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mBitmap = resource;
                        mPicture.setImageBitmap(resource);
                    }
                });
        SnackbarUtil.showMessage(mPicture, "单击图片返回, 双击图片放大, 长按图片保存");
    }

    public static void newIntent(Context context, String url, int locationX, int locationY, int width, int height) {
        Intent intent = new Intent(context, MeiziActivity.class);
        intent.putExtra(EXTRA_IMAGE_URL, url);
        intent.putExtra(TRANSIT_LOCATIONX, locationX);
        intent.putExtra(TRANSIT_LOCATIONY, locationY);
        intent.putExtra(TRANSIT_WIDTH, width);
        intent.putExtra(TRANSIT_HEIGHT, height);
        context.startActivity(intent);
    }


    private void setupPhotoAttacher() {
        mPicture.setOnClickListener(new ZoomImageView.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mPicture.setOnLongClickListener(new ZoomImageView.OnLongClickListener() {
            @Override
            public void onLongClick(View v) {
                createDialog();
            }
        });
    }

    private void createDialog() {
        new AlertDialog.Builder(MeiziActivity.this).setMessage("保存到手机?")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveImage();
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void saveImage() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File directory = new File(externalStorageDirectory, getString(R.string.app_name));
        if (!directory.exists())
            directory.mkdir();
        try {
            File file = new File(directory, new Date().getTime() + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            // 通知图库刷新
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            sendBroadcast(intent);
            SnackbarUtil.showMessage(mPicture, "已保存到" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "saveImage: " + e.getMessage());
            SnackbarUtil.showMessage(mPicture, "啊偶, 出错了", "再试试", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveImage();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        mPicture.setOnTransformListener(new ZoomImageView.TransformListener() {
            @Override
            public void onTransformComplete(int mode) {
                if (mode == 2) {
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });
        mPicture.transformOut();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPicture != null) {
            mPicture = null;
        }
        if (mBitmap != null) {
            mBitmap = null;
        }
    }
}

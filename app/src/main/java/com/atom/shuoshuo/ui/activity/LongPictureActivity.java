package com.atom.shuoshuo.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.base.AbsBaseActivity;
import com.atom.shuoshuo.glide.ProgressTarget;
import com.atom.shuoshuo.utils.SnackbarUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.shizhefei.view.largeimage.LargeImageView;
import com.shizhefei.view.largeimage.factory.FileBitmapDecoderFactory;

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
 * createTime: 2017/8/1  15:13
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class LongPictureActivity extends AbsBaseActivity {


    private static final String LONG_IMAGE_URL = "LONG_IMAGE_URL";
    private static final String TAG = LongPictureActivity.class.getSimpleName();


    private String long_image_url;
    private Bitmap mBitmap;

    @BindView(R.id.picture_pager)
    LargeImageView mImageView;

    public static void startActivity(Context context, String url) {
        Intent intent = new Intent(context, LongPictureActivity.class);
        intent.putExtra(LONG_IMAGE_URL, url);
        context.startActivity(intent);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            ((MainActivity) context).overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
        }

    }


    @Override
    protected int getResLayoutId() {
        return R.layout.activity_picture;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        long_image_url = getIntent().getStringExtra(LONG_IMAGE_URL);

        mImageView.setEnabled(true);
        mImageView.setCriticalScaleValueHook(new LargeImageView.CriticalScaleValueHook() {
            @Override
            public float getMinScale(LargeImageView largeImageView, int imageWidth, int imageHeight, float suggestMinScale) {
                return 1;
            }

            @Override
            public float getMaxScale(LargeImageView largeImageView, int imageWidth, int imageHeight, float suggestMaxScale) {
                return 2;
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialog();
                return true;
            }
        });

        Glide.with(this)
                .load(long_image_url)
                .downloadOnly(new ProgressTarget<String, File>(long_image_url, null) {

                    @Override
                    public void onProgress(int progress) {

                    }

                    @Override
                    public void onResourceReady(File resource, GlideAnimation<? super File> animation) {
                        super.onResourceReady(resource, animation);
                        mImageView.setImage(new FileBitmapDecoderFactory(resource));
                        mBitmap = BitmapFactory.decodeFile(resource.getPath());
                    }

                    @Override
                    public void getSize(SizeReadyCallback cb) {
                        cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                    }
                });
    }


    private void createDialog() {
        new AlertDialog.Builder(this)
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
            SnackbarUtil.showMessage(mImageView, "已保存到" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "saveImage: " + e.getMessage());
            SnackbarUtil.showMessage(mImageView, "啊偶, 出错了", "再试试", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveImage();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }
}

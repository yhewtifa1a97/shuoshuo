package com.atom.shuoshuo.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.base.AbsBaseFragment;
import com.atom.shuoshuo.glide.ProgressTarget;
import com.atom.shuoshuo.ui.activity.MultiPictureActivity;
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

import static fm.jiecao.jcvideoplayer_lib.JCVideoPlayer.TAG;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.ui.fragment
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/8/1  16:10
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class MultiPictureFragment extends AbsBaseFragment {

    @BindView(R.id.large_ImageView)
    LargeImageView mImageView;


    private String mImageUrl;
    private Bitmap mBitmap;

    public static Fragment newInstance(String imageUrl) {
        MultiPictureFragment f = new MultiPictureFragment();
        Bundle args = new Bundle();
        args.putString("url", imageUrl);
        f.setArguments(args);
        return f;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_multi_picture_item;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

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

        Glide.with(getContext())
                .load(mImageUrl)
                .downloadOnly(new ProgressTarget<String, File>(mImageUrl, null) {
                    @Override
                    public void onProgress(int progress) {
                    }

                    @Override
                    public void onResourceReady(File resource, GlideAnimation<? super File> animation) {
                        super.onResourceReady(resource, animation);
                        mBitmap = BitmapFactory.decodeFile(resource.getPath());
                        mImageView.setImage(new FileBitmapDecoderFactory(resource));
                    }

                    @Override
                    public void getSize(SizeReadyCallback cb) {
                        cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                    }
                });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MultiPictureActivity) getActivity()).onBackPressed();
            }
        });

        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialog();
                return true;
            }
        });
    }

    private void createDialog() {
        new AlertDialog.Builder(getContext()).setMessage("保存到手机?")
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
            getContext().sendBroadcast(intent);
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
    public void onDestroyView() {
        super.onDestroy();
        if (mImageView != null) {
            mImageView = null;
        }
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    @Override
    protected void lazyLoad() {

    }

}

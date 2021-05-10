package com.atom.shuoshuo.adapter.MulitProvider;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.atom.shuoshuo.R;
import com.atom.shuoshuo.bean.picture.gifvideo.GifVideoBean;
import com.atom.shuoshuo.ui.activity.MultiGifActivity;
import com.atom.shuoshuo.utils.AlxGifHelper;
import com.atom.shuoshuo.utils.AppUtil;
import com.atom.shuoshuo.utils.MD5Utils;
import com.atom.shuoshuo.widget.SunVideoPlayer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.adapter
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/8/1  14:27
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class GifVideoProvider extends PictureFrameProvider<GifVideoBean, GifVideoProvider.ViewHolder> {



    @Override
    protected ContentHolder onCreateContentViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.fragment_picture_gifvideo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindContentViewHolder(@NonNull final ViewHolder holder, @NonNull final GifVideoBean content) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.mGifVideo.getLayoutParams();
        lp.height = AppUtil.getScreenWidth() * content.getGifvideo().getVideo_height() / content.getGifvideo().getVideo_width();
        holder.mGifVideo.setLayoutParams(lp);

        Glide.with(holder.mGifVideo.getContext()).load(content.getMiddle_image().getUrl_list().get(0).getUrl())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.mGifVideo.thumbImageView);
        holder.mGifVideo.looping = true; // 循环播放
        holder.mGifVideo.audio = false; // 不获取音频服务

        // TODO: 2016/12/22
        String url = content.getGifvideo().getMp4_url();
        String path = holder.mGifVideo.getContext().getExternalCacheDir().getAbsolutePath()
                + File.separator + MD5Utils.getURLMd5(url) + ".mp4";
        File fileMp4 = new File(path);
        if (fileMp4.exists()) {
            holder.mProgressBar.setVisibility(View.INVISIBLE);
            holder.mGifVideo.setUp(path,
                    JCVideoPlayerStandard.SCREEN_LAYOUT_LIST, "");
        } else {
            AlxGifHelper.startDownLoad(url, fileMp4, new AlxGifHelper.DownLoadTask() {
                @Override
                protected void onStart() {
                    holder.mProgressBar.setVisibility(View.VISIBLE);
                    holder.mProgressBar.setProgress(0);
                }

                @Override
                protected void onLoading(long total, long current) {
                    holder.mProgressBar.setProgress((int) (current * 100 / total));
                }

                @Override
                protected void onSuccess(File target) {
                    holder.mProgressBar.setVisibility(View.INVISIBLE);
                    holder.mGifVideo.setUp(target.getAbsolutePath(),
                            JCVideoPlayerStandard.SCREEN_LAYOUT_LIST, "");
                }

                @Override
                protected void onFailure(Throwable e) {
                    holder.mProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }

        final ArrayList<String> listurl = new ArrayList();
        listurl.add(content.getLarge_image().getUrl_list().get(0).getUrl());

        holder.mGifVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiGifActivity.startActivity(holder.mGifVideo.getContext(), 0, listurl,
                        content.getMiddle_image().getWidth(), content.getMiddle_image().getHeight());
            }
        });
    }


    static class ViewHolder extends ContentHolder {

        @BindView(R.id.fragment_picture_gifmp4)
        SunVideoPlayer mGifVideo;
        @BindView(R.id.fragment_picture_gifmp4_pb)
        ProgressBar mProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

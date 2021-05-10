package com.atom.shuoshuo.bean.picture;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atom.shuoshuo.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifImageView;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.bean.picture
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/8/1  10:41
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class GsonProvider {

    private GsonProvider(){}

    public final static Gson gson = new GsonBuilder()
            .registerTypeAdapter(PictureContent.class,new PictureContentDeserializer())
            .create();
}

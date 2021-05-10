package com.atom.shuoshuo.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.glide
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/8/1  11:42
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class CircleImageTransform extends BitmapTransformation {

    private Context mContext;

    public CircleImageTransform(Context context) {
        super(context);
        mContext = context;
    }


    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        int size = Math.min(toTransform.getWidth(), toTransform.getHeight());
        int width = (toTransform.getWidth() - size) / 2;
        int height = (toTransform.getHeight() - size) / 2;

        //获取资源
        Bitmap sourceBitmap = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (null == sourceBitmap) {
            sourceBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        //绘制圆形图片
        Canvas canvas = new Canvas(sourceBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        BitmapShader shader = new BitmapShader(toTransform, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        if (width != 0 || height != 0) {
            Matrix matrix = new Matrix();
            matrix.setTranslate(-width, -height);
            shader.setLocalMatrix(matrix);
        }
        paint.setShader(shader);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        return sourceBitmap;
    }

    @Override
    public String getId() {
        return "CircleImageTransform";
    }
}

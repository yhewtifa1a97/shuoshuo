package com.atom.shuoshuo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * 正方形的ImageView
 */
public class SquareCenterImageView extends ImageView {
    public SquareCenterImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SquareCenterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareCenterImageView(Context context) {
        super(context);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        Log.d("roamer", "bm.getWidth():" + bm.getWidth());
        Log.d("roamer", "bm.getHeight():" + bm.getHeight());

        super.setImageBitmap(bm);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        // Children are just made to fill our space.
        int childWidthSize = getMeasuredWidth();
        //高度和宽度一样
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}



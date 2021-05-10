package com.atom.shuoshuo.widget;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

/**
 * 2d平滑变化的显示图片的ImageView
 * 仅限于用于:从一个ScaleType==CENTER_CROP的ImageView，切换到另一个ScaleType=
 * FIT_CENTER的ImageView，或者反之 (当然，得使用同样的图片最好)
 */
public class ZoomImageView extends ImageView implements ScaleGestureDetector.OnScaleGestureListener, ViewTreeObserver.OnGlobalLayoutListener, View.OnTouchListener {
    private static final int STATE_NORMAL = 0;
    private static final int STATE_TRANSFORM_IN = 1;
    private static final int STATE_TRANSFORM_OUT = 2;
    private int mOriginalWidth;
    private int mOriginalHeight;
    private int mOriginalLocationX;
    private int mOriginalLocationY;
    private int mState = STATE_NORMAL;
    private Matrix mSmoothMatrix;
    private Bitmap mBitmap;
    private boolean mTransformStart = false;
    private Transfrom mTransfrom;
    private final int mBgColor = 0xFF000000;
    private int mBgAlpha = 0;
    private Paint mPaint;

    private boolean mOnce = true;
    /**
     * 初始化时缩放的值
     */
    private float mMinScale;
    /**
     * 双击放大到达的值
     */
    private float mMidScale;
    /**
     * 放大的最大值
     */
    private float mMaxScale;

    private Matrix mScaleMatrix;
    /**
     * 捕获用户多点触控时缩放的比例
     */
    private ScaleGestureDetector mScaleGestureDetector = null;
    /**
     * 记录上一次多点触控的数量
     */
    private int mLastPointerCount;
    private float mLastX;
    private float mLastY;

    private int mTouchSlop;
    private boolean isCanDrag;

    private boolean isCheckLeftAndRight = true;
    private boolean isCheckTopAndBottom = true;

    /**
     * 双击放大缩小
     */
    private GestureDetector mGestureDetector;

    private boolean isAutoScale;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mSmoothMatrix = new Matrix();
        mPaint = new Paint();
        mPaint.setColor(mBgColor);
        mPaint.setStyle(Style.FILL);
        //		setBackgroundColor(mBgColor);

        mScaleMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);

        mScaleGestureDetector = new ScaleGestureDetector(context, this);

        setOnTouchListener(this);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public boolean onDoubleTap(MotionEvent e) {

                if (isAutoScale == true) {
                    return true;
                }

                float X = e.getX();
                float Y = e.getY();

                if (getScale() < mMidScale) {

                    postDelayed(new AutoScaleRunnable(mMidScale, X, Y), 16);
                    isAutoScale = true;

                } else if ((getScale() >= mMidScale) && (getScale() < mMaxScale)) {

                    postDelayed(new AutoScaleRunnable(mMaxScale, X, Y), 16);
                    isAutoScale = true;

                } else {

                    postDelayed(new AutoScaleRunnable(mMinScale, X, Y), 5);
                    isAutoScale = true;

                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mClickListener != null)
                    mClickListener.onClick(ZoomImageView.this);
                return super.onSingleTapUp(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (mLongClickListener != null)
                    mLongClickListener.onLongClick(ZoomImageView.this);
            }
        });
    }

    /**
     * 自动放大与缩小
     *
     * @author Sun
     */
    private class AutoScaleRunnable implements Runnable {

        /**
         * 缩放的目标值
         */
        private float mTargetScale;
        // 缩放的中心点
        private float X;
        private float Y;

        private final float BIGGER = 1.07f;
        private final float SMALL = 0.93f;

        private float tmpScale;

        /**
         * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小
         *
         * @param
         */
        public AutoScaleRunnable(float mTargetScale, float x, float y) {

            this.mTargetScale = mTargetScale;
            this.X = x;
            this.Y = y;

            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            }

            if (getScale() > mTargetScale) {
                tmpScale = SMALL;
            }
        }

        public void run() {

            // 进行缩放
            mScaleMatrix.postScale(tmpScale, tmpScale, X, Y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);

            float currentScale = getScale();
            // 如果值在合法范围内，继续缩放
            if (((tmpScale > 1.0f) && (currentScale < mTargetScale)) || ((tmpScale < 1.0f) && (currentScale > mTargetScale))) {

                postDelayed(this, 16);
            } else {
                // 设置为目标的缩放比例
                float scale = mTargetScale / currentScale;
                mScaleMatrix.postScale(scale, scale, X, Y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }
        }
    }

    /**
     * 注册onGlobalLayout的监听
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    /**
     * 解除onGlobalLayout的监听
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // getViewTreeObserver().removeOnGlobalLayoutListener(this);
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    /**
     * 获取ImageView加载完成的图片
     */
    @Override
    public void onGlobalLayout() {

        if (mOnce) {

            // 得到图片，以及宽和高
            Drawable d = getDrawable();
            if (d == null) {
                return;
            }

            // 得到控件的宽和高
            int width = getWidth();
            int height = getHeight();

            int dWidth = d.getIntrinsicWidth();
            int dHeight = d.getIntrinsicHeight();

            float scale = 1.0f;

            // 如果图片的宽度大于控件的宽度，但是高度小于控件高度，我们将其缩小
            if (dWidth > width && dHeight <= height) {
                scale = width * 1.0f / dWidth;
            }

            // 如果图片的高度大于控件的高度，但是宽度小于控件宽度，我们将其缩小
            if (dHeight > height && dWidth <= width) {
                scale = height * 1.0f / dHeight;
            }

            // 如果图片的宽度大于控件的宽度，并且高度大于控件高度，我们将其缩小
            // 如果图片的宽度小于控件的宽度，但是高度小于控件高度，我们将其放大
            if ((dWidth > width && dHeight > height) || (dWidth < width && dHeight < height)) {

                scale = Math.min(width * 1.0f / dWidth, height * 1.0f / dHeight);
            }

            // 得到初始化时缩放的比例值
            mMinScale = scale;
            mMidScale = mMinScale * 2f;
            mMaxScale = mMinScale * 4f;

            // 计算图片需要移动的X轴距离和Y轴距离
            int dX = (width - dWidth) / 2;
            int dY = (height - dHeight) / 2;
            // 将图片移动至控件中心
            mScaleMatrix.postTranslate(dX, dY);
            mScaleMatrix.postScale(mMinScale, mMinScale, width / 2, height / 2);
            setImageMatrix(mScaleMatrix);

            mOnce = false;
        }
    }

    /**
     * 获取缩放值
     *
     * @return
     */
    public float getScale() {

        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        // 缩放区间：mMinScale~mMaxScale

        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null)
            return true;

        // 缩放范围的控制
        if ((scale < mMaxScale && scaleFactor > 1.0f) || (scale > mMinScale && scaleFactor < 1.0f)) {

            if (scale * scaleFactor < mMinScale) {

                scaleFactor = mMinScale / scale;
            }

            if (scale * scaleFactor > mMaxScale) {

                scale = mMaxScale / scale;
            }
            // 缩放
            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());

            checkBorderAndCenterWhenScale();

            setImageMatrix(mScaleMatrix);
        }

        return true;
    }

    /**
     * 获得图片放大缩小以后的宽和高，以及l,r,t,b
     *
     * @return
     */
    private RectF getMatrixRectF() {

        Matrix matrix = mScaleMatrix;
        RectF rectF = new RectF();
        Drawable d = getDrawable();

        if (d != null) {

            rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }
        return rectF;
    }

    /**
     * 在缩放的时候进行边界和位置的控制
     */
    private void checkBorderAndCenterWhenScale() {

        RectF rectF = getMatrixRectF();

        float delatX = 0;
        float delatY = 0;

        int width = getWidth();
        int height = getHeight();

        // 缩放时进行边界检测
        if (rectF.width() >= width) {

            if (rectF.left > 0) {
                delatX = -rectF.left;
            }

            if (rectF.right < width) {
                delatX = width - rectF.right;
            }
        }

        if (rectF.height() >= height) {

            if (rectF.top > 0) {
                delatY = -rectF.top;
            }

            if (rectF.bottom < height) {
                delatY = height - rectF.bottom;
            }
        }

        // 如果图片的宽度或者高度小于控件的宽度或者高度，则将其居中
        if (rectF.width() < width) {
            delatX = width * 0.5f - rectF.right + rectF.width() * 0.5f;
        }

        if (rectF.height() < height) {
            delatY = height * 0.5f - rectF.bottom + rectF.height() * 0.5f;
        }

        mScaleMatrix.postTranslate(delatX, delatY);

    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {

        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (mGestureDetector.onTouchEvent(event))
            return true;

        mScaleGestureDetector.onTouchEvent(event);

        float x = 0;
        float y = 0;
        // 得到多点触控的数量
        final int mPointerCount = event.getPointerCount();
        for (int i = 0; i < mPointerCount; i++) {

            x += event.getX(i);
            y += event.getY(i);
        }

        x = x / mPointerCount;
        y = y / mPointerCount;

        /**
         * 每当触摸点发生变化时，重置mLasX , mLastY
         */
        if (mLastPointerCount != mPointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }

        mLastPointerCount = mPointerCount;

        RectF rectF = getMatrixRectF();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (rectF.width() > getWidth() + 0.01 || rectF.height() > getHeight() + 0.01) {
                    if (getParent() instanceof ViewPager)
                        getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (rectF.width() > getWidth() + 0.01 || rectF.height() > getHeight() + 0.01) {
                    if (getParent() instanceof ViewPager)
                        getParent().requestDisallowInterceptTouchEvent(true);
                }

                float dX = x - mLastX;
                float dY = y - mLastY;

                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dX, dY);
                }

                if (isCanDrag) {
                    if (getDrawable() != null) {

                        isCheckLeftAndRight = isCheckTopAndBottom = true;

                        // 如果宽度小于控件宽度，不允许横向移动
                        if (rectF.width() < getWidth()) {
                            dX = 0;
                            isCheckLeftAndRight = false;
                        }
                        // 如果高度小于控件高度，不允许纵向移动
                        if (rectF.height() < getHeight()) {
                            dY = 0;
                            isCheckTopAndBottom = false;
                        }

                        mScaleMatrix.postTranslate(dX, dY);

                        checkBorderWhenTranslate();

                        setImageMatrix(mScaleMatrix);

                    }
                }
                mLastX = x;
                mLastY = y;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;
        }
        return true;
    }

    /**
     * 当移动时，进行边界检查
     */
    private void checkBorderWhenTranslate() {

        RectF rectF = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rectF.top > 0 && isCheckTopAndBottom) {
            deltaY = -rectF.top;
        }

        if (rectF.bottom < height && isCheckTopAndBottom) {
            deltaY = height - rectF.bottom;
        }

        if (rectF.left > 0 && isCheckLeftAndRight) {
            deltaX = -rectF.left;
        }

        if (rectF.right < width && isCheckLeftAndRight) {
            deltaX = width - rectF.right;
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);
        //		setImageMatrix(mScaleMatrix);

    }

    /**
     * 判读是否是move
     *
     * @param dX
     * @param dY
     * @return
     */
    private boolean isMoveAction(float dX, float dY) {
        return Math.sqrt((dX * dX) + (dY * dY)) >= mTouchSlop;
    }

    public void setOriginalInfo(int width, int height, int locationX, int locationY) {
        mOriginalWidth = width;
        mOriginalHeight = height;
        mOriginalLocationX = locationX;
        mOriginalLocationY = locationY;
        // 因为是屏幕坐标，所以要转换为该视图内的坐标，因为我所用的该视图是MATCH_PARENT，所以不用定位该视图的位置,如果不是的话，还需要定位视图的位置，然后计算mOriginalLocationX和mOriginalLocationY
        mOriginalLocationY = mOriginalLocationY - getStatusBarHeight(getContext());
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    private int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        int statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 用于开始进入的方法。 调用此方前，需已经调用过setOriginalInfo
     */
    public void transformIn() {
        mState = STATE_TRANSFORM_IN;
        mTransformStart = true;
        invalidate();
    }

    /**
     * 用于开始退出的方法。 调用此方前，需已经调用过setOriginalInfo
     */
    public void transformOut() {
        mState = STATE_TRANSFORM_OUT;
        mTransformStart = true;
        invalidate();
    }

    private class Transfrom {
        float startScale;// 图片开始的缩放值
        float endScale;// 图片结束的缩放值
        float scale;// 属性ValueAnimator计算出来的值
        LocationSizeF startRect;// 开始的区域
        LocationSizeF endRect;// 结束的区域
        LocationSizeF rect;// 属性ValueAnimator计算出来的值

        void initStartIn() {
            scale = startScale;
            try {
                rect = (LocationSizeF) startRect.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        void initStartOut() {
            scale = endScale;
            try {
                rect = (LocationSizeF) endRect.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 初始化进入的变量信息
     */
    private void initTransform() {
        if (getDrawable() == null) {
            return;
        }
        if (mBitmap == null || mBitmap.isRecycled()) {
            mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        }
        //防止mTransfrom重复的做同样的初始化
        if (mTransfrom != null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        mTransfrom = new Transfrom();

        /** 下面为缩放的计算 */
        /* 计算初始的缩放值，初始值因为是CENTR_CROP效果，所以要保证图片的宽和高至少1个能匹配原始的宽和高，另1个大于 */
        float xSScale = mOriginalWidth / ((float) mBitmap.getWidth());
        float ySScale = mOriginalHeight / ((float) mBitmap.getHeight());
        float startScale = xSScale > ySScale ? xSScale : ySScale;
        mTransfrom.startScale = startScale;
        /* 计算结束时候的缩放值，结束值因为要达到FIT_CENTER效果，所以要保证图片的宽和高至少1个能匹配原始的宽和高，另1个小于 */
        float xEScale = getWidth() / ((float) mBitmap.getWidth());
        float yEScale = getHeight() / ((float) mBitmap.getHeight());
        float endScale = xEScale < yEScale ? xEScale : yEScale;
        mTransfrom.endScale = endScale;

        /**
         * 下面计算Canvas Clip的范围，也就是图片的显示的范围，因为图片是慢慢变大，并且是等比例的，所以这个效果还需要裁减图片显示的区域
         * ，而显示区域的变化范围是在原始CENTER_CROP效果的范围区域
         * ，到最终的FIT_CENTER的范围之间的，区域我用LocationSizeF更好计算
         * ，他就包括左上顶点坐标，和宽高，最后转为Canvas裁减的Rect.
         */
		/* 开始区域 */
        mTransfrom.startRect = new LocationSizeF();
        mTransfrom.startRect.left = mOriginalLocationX;
        mTransfrom.startRect.top = mOriginalLocationY;
        mTransfrom.startRect.width = mOriginalWidth;
        mTransfrom.startRect.height = mOriginalHeight;
		/* 结束区域 */
        mTransfrom.endRect = new LocationSizeF();
        float bitmapEndWidth = mBitmap.getWidth() * mTransfrom.endScale;// 图片最终的宽度
        float bitmapEndHeight = mBitmap.getHeight() * mTransfrom.endScale;// 图片最终的宽度
        mTransfrom.endRect.left = (getWidth() - bitmapEndWidth) / 2;
        mTransfrom.endRect.top = (getHeight() - bitmapEndHeight) / 2;
        mTransfrom.endRect.width = bitmapEndWidth;
        mTransfrom.endRect.height = bitmapEndHeight;

        mTransfrom.rect = new LocationSizeF();
    }

    private class LocationSizeF implements Cloneable {
        float left;
        float top;
        float width;
        float height;

        @Override
        public String toString() {
            return "[left:" + left + " top:" + top + " width:" + width + " height:" + height + "]";
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            // TODO Auto-generated method stub
            return super.clone();
        }

    }

    /* 下面实现了CENTER_CROP的功能 的Matrix，在优化的过程中，已经不用了 */
    private void getCenterCropMatrix() {
        if (getDrawable() == null) {
            return;
        }
        if (mBitmap == null || mBitmap.isRecycled()) {
            mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        }
		/* 下面实现了CENTER_CROP的功能 */
        float xScale = mOriginalWidth / ((float) mBitmap.getWidth());
        float yScale = mOriginalHeight / ((float) mBitmap.getHeight());
        float scale = xScale > yScale ? xScale : yScale;
        mSmoothMatrix.reset();
        mSmoothMatrix.setScale(scale, scale);
        mSmoothMatrix.postTranslate(-(scale * mBitmap.getWidth() / 2 - mOriginalWidth / 2), -(scale * mBitmap.getHeight() / 2 - mOriginalHeight / 2));
    }

    private void getBmpMatrix() {
        if (getDrawable() == null) {
            return;
        }
        if (mTransfrom == null) {
            return;
        }
        if (mBitmap == null || mBitmap.isRecycled()) {
            mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        }
		/* 下面实现了CENTER_CROP的功能 */
        mSmoothMatrix.setScale(mTransfrom.scale, mTransfrom.scale);
        mSmoothMatrix.postTranslate(-(mTransfrom.scale * mBitmap.getWidth() / 2 - mTransfrom.rect.width / 2), -(mTransfrom.scale * mBitmap.getHeight() / 2 - mTransfrom.rect.height / 2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return; // couldn't resolve the URI
        }

        if (mState == STATE_TRANSFORM_IN || mState == STATE_TRANSFORM_OUT) {
            if (mTransformStart) {
                initTransform();
            }
            if (mTransfrom == null) {
                super.onDraw(canvas);
                return;
            }

            if (mTransformStart) {
                if (mState == STATE_TRANSFORM_IN) {
                    mTransfrom.initStartIn();
                } else {
                    mTransfrom.initStartOut();
                }
            }

            if (mTransformStart) {
                Log.d("Dean", "mTransfrom.startScale:" + mTransfrom.startScale);
                Log.d("Dean", "mTransfrom.startScale:" + mTransfrom.endScale);
                Log.d("Dean", "mTransfrom.scale:" + mTransfrom.scale);
                Log.d("Dean", "mTransfrom.startRect:" + mTransfrom.startRect.toString());
                Log.d("Dean", "mTransfrom.endRect:" + mTransfrom.endRect.toString());
                Log.d("Dean", "mTransfrom.rect:" + mTransfrom.rect.toString());
            }

            mPaint.setAlpha(mBgAlpha);
            canvas.drawPaint(mPaint);

            int saveCount = canvas.getSaveCount();
            canvas.save();
            // 先得到图片在此刻的图像Matrix矩阵
            getBmpMatrix();
            canvas.translate(mTransfrom.rect.left, mTransfrom.rect.top);
            canvas.clipRect(0, 0, mTransfrom.rect.width, mTransfrom.rect.height);
            canvas.concat(mSmoothMatrix);
            getDrawable().draw(canvas);
            canvas.restoreToCount(saveCount);
            if (mTransformStart) {
                mTransformStart = false;
                startTransform(mState);
            }
        } else {
            //当Transform In变化完成后，把背景改为黑色，使得Activity不透明
            mPaint.setAlpha(255);
            canvas.drawPaint(mPaint);
            super.onDraw(canvas);
        }
    }

    private void startTransform(final int state) {
        if (mTransfrom == null) {
            return;
        }
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        if (state == STATE_TRANSFORM_IN) {
            PropertyValuesHolder scaleHolder = PropertyValuesHolder.ofFloat("scale", mTransfrom.startScale, mTransfrom.endScale);
            PropertyValuesHolder leftHolder = PropertyValuesHolder.ofFloat("left", mTransfrom.startRect.left, mTransfrom.endRect.left);
            PropertyValuesHolder topHolder = PropertyValuesHolder.ofFloat("top", mTransfrom.startRect.top, mTransfrom.endRect.top);
            PropertyValuesHolder widthHolder = PropertyValuesHolder.ofFloat("width", mTransfrom.startRect.width, mTransfrom.endRect.width);
            PropertyValuesHolder heightHolder = PropertyValuesHolder.ofFloat("height", mTransfrom.startRect.height, mTransfrom.endRect.height);
            PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt("alpha", 0, 255);
            valueAnimator.setValues(scaleHolder, leftHolder, topHolder, widthHolder, heightHolder, alphaHolder);
        } else {
            PropertyValuesHolder scaleHolder = PropertyValuesHolder.ofFloat("scale", mTransfrom.endScale, mTransfrom.startScale);
            PropertyValuesHolder leftHolder = PropertyValuesHolder.ofFloat("left", mTransfrom.endRect.left, mTransfrom.startRect.left);
            PropertyValuesHolder topHolder = PropertyValuesHolder.ofFloat("top", mTransfrom.endRect.top, mTransfrom.startRect.top);
            PropertyValuesHolder widthHolder = PropertyValuesHolder.ofFloat("width", mTransfrom.endRect.width, mTransfrom.startRect.width);
            PropertyValuesHolder heightHolder = PropertyValuesHolder.ofFloat("height", mTransfrom.endRect.height, mTransfrom.startRect.height);
            PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt("alpha", 255, 0);
            valueAnimator.setValues(scaleHolder, leftHolder, topHolder, widthHolder, heightHolder, alphaHolder);
        }

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public synchronized void onAnimationUpdate(ValueAnimator animation) {
                mTransfrom.scale = (Float) animation.getAnimatedValue("scale");
                mTransfrom.rect.left = (Float) animation.getAnimatedValue("left");
                mTransfrom.rect.top = (Float) animation.getAnimatedValue("top");
                mTransfrom.rect.width = (Float) animation.getAnimatedValue("width");
                mTransfrom.rect.height = (Float) animation.getAnimatedValue("height");
                mBgAlpha = (Integer) animation.getAnimatedValue("alpha");
                invalidate();
                ((Activity) getContext()).getWindow().getDecorView().invalidate();
            }
        });
        valueAnimator.addListener(new ValueAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
				/*
				 * 如果是进入的话，当然是希望最后停留在center_crop的区域。但是如果是out的话，就不应该是center_crop的位置了
				 * ， 而应该是最后变化的位置，因为当out的时候结束时，不回复视图是Normal，要不然会有一个突然闪动回去的bug
				 */
                // TODO 这个可以根据实际需求来修改
                if (state == STATE_TRANSFORM_IN) {
                    mState = STATE_NORMAL;
                }
                if (mTransformListener != null) {
                    mTransformListener.onTransformComplete(state);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        valueAnimator.start();
    }

    public void setOnTransformListener(TransformListener listener) {
        mTransformListener = listener;
    }

    private TransformListener mTransformListener;

    public static interface TransformListener {
        /**
         * @param mode STATE_TRANSFORM_IN 1 ,STATE_TRANSFORM_OUT 2
         */
        void onTransformComplete(int mode);// mode 1
    }

    public interface OnClickListener {
        void onClick(View v);
    }

    private OnClickListener mClickListener;

    public void setOnClickListener(@Nullable OnClickListener l) {
        this.mClickListener = l;
    }

    public interface OnLongClickListener {
        void onLongClick(View v);
    }

    private OnLongClickListener mLongClickListener;

    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        mLongClickListener = l;
    }

}

package com.example.dashview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

import android.util.Log;
import android.view.View;

public class DashView extends View {


    private static final String TAG = "== DashView ==";

    public float mStartAngle = 135;
    public float mSweepAngle = 270;

    /**
     * 刻度画笔
     */
    private Paint mSmallScalePaint;
    private Paint mBigScalePaint;

    /**
     * 圆弧画笔
     */
    private Paint mArcPaint;

    /**
     * 指针画笔
     */
    private Paint mPointerPaint;

    /**
     * 圆弧的宽度
     */
    private float mArcWidth;

    /**
     * 仪表盘title文字 画笔
     */
    private Paint mTitlePoint;

    /**
     * 文字 画笔
     */
    private Paint mTextPoint;

    /**
     * 刻度值画笔
     */
    private Paint mScaleTextPoint;

    /**
     * 小刻度 画笔参数 宽 高 和颜色
     */
    private float mSmallScaleWidth;
    private float mSmallScaleHeight;
    private int mSmallScaleColor;

    /**
     * 大刻度 画笔参数 宽 高 和颜色
     */
    private float mBigScaleWidth;
    private float mBigScaleHeight;
    private int mBigScaleColor;

    /**
     * 仪表盘名字参数
     */
    private int mDashTitleColor;
    private float mDashTitleSize;

    /**
     * 普通文字参数
     */
    private int mDashTextColor;
    private float mDashTextSize;

    /**
     * 正常圆弧颜色
     */
    private int mNormalArcColor;
    /**
     * 危险值范围 圆弧颜色
     */
    private int mDangerArcColor;
    /**
     * 报警值范围 圆弧颜色
     */
    private int mWarningArcColor;

    /**
     * 指针 画笔参数
     */
    private int mPointerColor;
    private int mPointerSize;

    private Rect mDashRect;

    /**
     * 刻度文字
     */
    private int mScaleTextSize;
    private int mScaleTextRadius;
    private int mScaleTextColor;

    /**
     * 仪表盘的内部padding值 最小是10dp
     */
    private int mDashPadding;


    private String mTitle;
    private String mStandard;
    private String mTime;

    /**
     * 仪表盘的最小值
     */
    private float mStartValue = 0;
    /**
     * 结束值
     */
    private float mEndValue = 100;
    /**
     * 危险线
     */
    private float mDangerValue;
    /**
     * 报警线
     */
    private float mWarningValue;
    private String[] mScaleValues = new String[6];

    private float mNormalAngle;
    private float mDangerAngle;
    private float mWarningAngle;

    private float currentAngle;

    public float getCurrentAngle() {
        return currentAngle;
    }

    public void setCurrentAngle(float currentAngle) {
        this.currentAngle = currentAngle;
        invalidate();
    }

    public void setCurrentValue(float currentValue) {
        float maxRange = mEndValue - mStartValue;
        currentAngle  = (currentValue - mStartValue) / maxRange * 270;
    }

    public void setDatas(float start, float end) {
        mStartValue = start;
        mEndValue = end;
        if (mEndValue < mStartValue) {
            throw new IllegalArgumentException("end must be than satrt ");
        }

        //计算刻度
        float scale = (mEndValue - mStartValue) / 5;
        for (int i = 0; i < mScaleValues.length; i++) {
            //向下取整
            double floor = Math.floor(mStartValue + i * scale);
            if (floor == mStartValue + i * scale) {
                mScaleValues[i] = (int) (floor) + "";
            } else {
                mScaleValues[i] = (mStartValue + i * scale) + "";
            }
        }
    }

    public void setDangerValue(float dangerValue) {
        mDangerValue = dangerValue;
    }

    public void setWarningValue(float warningValue) {
        mWarningValue = warningValue;
    }

    public void startRender() {
        //计算危险值范围和报警范围
        float maxRange = mEndValue - mStartValue;
        mNormalAngle = 270;
        if (mDangerValue != 0 && mDangerValue > mStartValue && mDangerValue < mEndValue) {


            if (mWarningValue != 0
                    && mWarningValue > mDangerValue
                    && mWarningValue > mStartValue
                    && mWarningValue < mEndValue) {
                //判断报警范围是否合法
                mNormalAngle =(mDangerValue - mStartValue) / maxRange * 270;
                mDangerAngle = (mWarningValue - mDangerValue) / maxRange * 270;
                mWarningAngle = (mEndValue - mWarningValue) / maxRange * 270;
            } else {
                //没有报警值范围
                mNormalAngle = (mDangerValue - mStartValue) / maxRange * 270;
                mDangerAngle = (mEndValue - mDangerValue) / maxRange * 270;
            }
        } else {
            //只有正常范围 报警范围
            if (mWarningValue != 0
                    && mWarningValue > mDangerValue
                    && mWarningValue > mStartValue
                    && mWarningValue < mEndValue) {
                mNormalAngle = (mWarningValue - mStartValue) / maxRange * 270;
                mWarningAngle = (mEndValue - mWarningValue) / maxRange * 270;
            }
        }

        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(this,"currentAngle",
                0, currentAngle);
        valueAnimator.setDuration(5000);
        valueAnimator.start();

        postInvalidate();
    }

    public void setTextInfo(@NonNull String title, String standard, @NonNull String time) {
        mTitle = title;
        mStandard = standard;
        mTime = time;
    }

    public DashView(Context context) {
        this(context, null);
    }

    public DashView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DashView);
        //圆弧的属性
        mArcWidth = ta.getDimensionPixelSize(R.styleable.DashView_arc_width, dp2px(30));
        mNormalArcColor = ta.getColor(R.styleable.DashView_normal_arc_color, Color.parseColor("#00B8F1"));
        mDangerArcColor = ta.getColor(R.styleable.DashView_danger_arc_color, Color.parseColor("#FFFF9800"));
        mWarningArcColor = ta.getColor(R.styleable.DashView_warning_arc_color, Color.parseColor("#F10F0F"));

        //指针的颜色
        mPointerColor = ta.getColor(R.styleable.DashView_pointer_color, Color.parseColor("#00B8F1"));

        //小刻度的属性 长宽颜色
        mSmallScaleWidth = ta.getDimensionPixelSize(R.styleable.DashView_small_scale_width, dp2px(2));
        mSmallScaleHeight = ta.getDimension(R.styleable.DashView_small_scale_height, dp2px(10));
        mSmallScaleColor = ta.getColor(R.styleable.DashView_small_scale_color, Color.parseColor("#FFFF5722"));

        //大刻度的属性 长宽颜色
        mBigScaleWidth = ta.getDimension(R.styleable.DashView_big_scale_width, dp2px(4));
        mBigScaleHeight = ta.getDimension(R.styleable.DashView_big_scale_height, dp2px(20));
        mBigScaleColor = ta.getColor(R.styleable.DashView_big_scale_color, Color.parseColor("#FFFF5722"));

        //刻度值 文字颜色 字体
        mScaleTextSize = ta.getDimensionPixelSize(R.styleable.DashView_scale_text_size, dp2px(14));
        mScaleTextColor = ta.getColor(R.styleable.DashView_scale_text_color, Color.parseColor("#999999"));

        //仪表盘的title颜色 和字体大小
        mDashTitleColor = ta.getColor(R.styleable.DashView_dash_title_color, Color.parseColor("#333333"));
        mDashTitleSize = ta.getDimension(R.styleable.DashView_dash_title_size, dp2px(16));


        //仪表盘 其他文字的颜色和字体大小
        mDashTextColor = ta.getColor(R.styleable.DashView_dash_text_color, Color.parseColor("#999999"));
        mDashTextSize = ta.getDimension(R.styleable.DashView_dash_text_size, dp2px(14));

        mDashPadding = ta.getDimensionPixelSize(R.styleable.DashView_dash_padding, dp2px(10));
        if (mDashPadding < dp2px(10)) {
            mDashPadding = dp2px(10);
        }
        ta.recycle();  //注意回收

        init();
    }

    private void init() {
        currentAngle = 0f;
        mTitle = "出水COD";
        mTime = "05-07 16:30:30";
        mStandard = "报警：580~";

        //title文字画笔
        mTitlePoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePoint.setTextSize(mDashTitleSize);
        mTitlePoint.setColor(mDashTitleColor);
        mTitlePoint.setTypeface(Typeface.DEFAULT_BOLD);
        mTitlePoint.setTextAlign(Paint.Align.CENTER);

        //其他文字画笔
        mTextPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPoint.setTextSize(mDashTextSize);
        mTextPoint.setColor(mDashTextColor);
        mTextPoint.setTextAlign(Paint.Align.CENTER);

        //刻度值画笔
        mScaleTextPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScaleTextPoint.setTextSize(mScaleTextSize);
        mScaleTextPoint.setColor(mScaleTextColor);
        mScaleTextPoint.setTextAlign(Paint.Align.CENTER);

        //小刻度画笔
        mSmallScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallScalePaint.setStrokeWidth(dp2px(1));
        mSmallScalePaint.setStyle(Paint.Style.STROKE);
        mSmallScalePaint.setColor(mSmallScaleColor);
        mSmallScalePaint.setStrokeJoin(Paint.Join.BEVEL);

        //大刻度画笔
        mBigScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBigScalePaint.setStrokeWidth(dp2px(1));
        mBigScalePaint.setStyle(Paint.Style.STROKE);
        mBigScalePaint.setColor(mBigScaleColor);
        mBigScalePaint.setStrokeJoin(Paint.Join.BEVEL);

        //圆弧画笔
        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStrokeWidth(mArcWidth);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setColor(mNormalArcColor);
        mArcPaint.setStrokeJoin(Paint.Join.BEVEL);


        //指针画笔
        mPointerPaint = new Paint();
        mPointerPaint.setStyle(Paint.Style.FILL);
        mPointerPaint.setColor(mPointerColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 如果父布局是RelativeLayout 会实效
        int measuredHeight = getMeasuredHeight();
        int measuredWidth = getMeasuredWidth();
        int min = Math.min(measuredHeight, measuredWidth);
        setMeasuredDimension(min, min);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDashRect = new Rect();
        mDashRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());

        Log.d(TAG, "onDraw: width = " + getWidth());
        Log.d(TAG, "onDraw: height = " + getHeight());

        // 画外环大圆弧
        mArcPaint.setStrokeWidth(mArcWidth);
        //实际的画笔位置在  padding + 1/2*mArcWidth
        float padding = mArcWidth / 2 + mDashPadding;
        @SuppressLint("DrawAllocation")
        RectF arcRectF = new RectF(padding, padding,
                mDashRect.width() - padding, getHeight() - padding);
        Log.d(TAG, "onDraw: mNormalAngle = " + mNormalAngle + " ; mDangerAngle = " + mDangerAngle + " ; mWarningAngle = " + mWarningAngle);
        float startAngle = mStartAngle;
        if (mNormalAngle > 0) {
            mArcPaint.setColor(mNormalArcColor);
            canvas.drawArc(arcRectF, startAngle, mNormalAngle, false, mArcPaint);
            startAngle += mNormalAngle;
        }

        if (mDangerAngle > 0) {
            mArcPaint.setColor(mDangerArcColor);
            canvas.drawArc(arcRectF, startAngle, mDangerAngle, false, mArcPaint);
            startAngle += mDangerAngle;
        }

        if (mWarningAngle > 0) {
            mArcPaint.setColor(mWarningArcColor);
            canvas.drawArc(arcRectF, startAngle, mWarningAngle, false, mArcPaint);
            startAngle += mWarningAngle;
        }


        //画指针
        float scalePadding = mArcWidth + mDashPadding;
        @SuppressLint("DrawAllocation")
        RectF rectF = new RectF(scalePadding, scalePadding, mDashRect.width() - scalePadding, mDashRect.height() - scalePadding);

        //获取弧线的长度
        Path arcPath = new Path();
        arcPath.addArc(rectF, mStartAngle, mSweepAngle);
        PathMeasure measure = new PathMeasure(arcPath, false);
        float advance = (measure.getLength() - mBigScaleWidth) / 20;

        //画短的刻度
        Path path = new Path();
        path.addRect(0, 0, mSmallScaleWidth, mSmallScaleHeight, Path.Direction.CCW);
        @SuppressLint("DrawAllocation")
        PathDashPathEffect effect = new PathDashPathEffect(path, advance, 0, PathDashPathEffect.Style.ROTATE);
        mSmallScalePaint.setPathEffect(effect);
        canvas.drawArc(rectF, mStartAngle, mSweepAngle, false, mSmallScalePaint);
        mSmallScalePaint.setPathEffect(null);

        //画长的刻度
        float advance2 = (measure.getLength() - mBigScaleWidth) / 5;
        path.addRect(0, 0, mBigScaleWidth, mBigScaleHeight, Path.Direction.CCW);
        @SuppressLint("DrawAllocation")
        PathDashPathEffect effect2 = new PathDashPathEffect(path, advance2, 0, PathDashPathEffect.Style.ROTATE);
        mBigScalePaint.setPathEffect(effect2);
        canvas.drawArc(rectF, mStartAngle, mSweepAngle, false, mBigScalePaint);
        mBigScalePaint.setPathEffect(null);

        //绘制刻度值
        drawScaleValue(canvas);

        //绘制指针
        drawPointer(canvas);

        //绘制文字
        drawText(canvas);
    }

    private void drawScaleValue(Canvas canvas) {


        canvas.save();
        canvas.translate(mDashRect.width() / 2, mDashRect.height() / 2);
        Paint.FontMetrics fontMetrics = mScaleTextPoint.getFontMetrics();
        float textHeight = fontMetrics.bottom - fontMetrics.top;
        //因为在y轴上写字  所以需要
        canvas.rotate(mStartAngle + 90);
        int count = 5; //总刻度数
        mScaleTextRadius = (int) (mDashRect.width() / 2 - mArcWidth - mDashPadding - mBigScaleHeight - textHeight);
        //绘制刻度和百分比
        for (int i = 0; i <= count; i++) {
            canvas.drawText(String.valueOf(mScaleValues[i]), 0, -mScaleTextRadius, mScaleTextPoint);
            canvas.rotate(54);
        }
        canvas.restore();
    }


    private void drawPointer(Canvas canvas) {
        canvas.save();
        canvas.translate(mDashRect.width() / 2, mDashRect.height() / 2);
        canvas.rotate(mStartAngle + currentAngle);
        mPointerSize = mScaleTextRadius - dp2px(10);
        Path pointerPath = new Path();
        pointerPath.moveTo(-dp2px(10), 0);
        pointerPath.lineTo(0, -dp2px(5));
        pointerPath.lineTo(mPointerSize, 0);
        pointerPath.lineTo(0, dp2px(5));
        pointerPath.close();
        canvas.drawPath(pointerPath, mPointerPaint);

        canvas.restore();
    }

    private void drawText(Canvas canvas) {
        int bootomPadding = dp2px(10);
        Paint.FontMetrics fontMetrics = mTextPoint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        float leading = fontMetrics.leading;
        float baseLineY = bottom - leading;//文字底部到基线的位置

        //绘制时间文字
        if (!TextUtils.isEmpty(mTime)) {
            canvas.drawText(mTime, mDashRect.width() / 2, mDashRect.height() - bootomPadding - baseLineY, mTextPoint);
        }


        //绘制标准字样文字
        float textHeight = bottom - top;
        if (!TextUtils.isEmpty(mStandard)) {
            canvas.drawText(mStandard, mDashRect.width() / 2, mDashRect.height() - bootomPadding - textHeight - baseLineY, mTextPoint);
        }

        //绘制 title文字
        Paint.FontMetrics titleFontMetrics = mTitlePoint.getFontMetrics();
        float titleBaseLineY = titleFontMetrics.bottom - titleFontMetrics.leading;
        if (!TextUtils.isEmpty(mTitle)) {
            //在控件 4/5 高度位置画 title值
            // canvas.drawText(mTitle, mDashRect.width() / 2, mDashRect.height()  - bootomPadding - textHeight * 2 - titleBaseLineY - bootomPadding, mTitlePoint);
            canvas.drawText(mTitle, mDashRect.width() / 2, mDashRect.height() / 5 * 4 - titleBaseLineY, mTitlePoint);
        }

    }


    /**
     * @param dpValue The value of dp.
     * @return value of px
     */
    public static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}

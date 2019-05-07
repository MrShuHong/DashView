package com.example.dashview;

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
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import android.util.Log;
import android.view.View;

public class DashView extends View {


    private static final String TAG = "== DashView ==";
    public static final float PADDING = dp2px(50);

    public float startAngle = 135;
    public float sweepAngle = 270;

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
    private float currentValue;
    private String mTitle;
    private String mStandard;
    private String mTime;

    /**
     * 仪表盘title文字 画笔
     */
    private Paint mTitlePoint;

    /**
     * 文字 画笔
     */
    private Paint mTextPoint;
    private float mSmallScaleWidth;
    private float mSmallScaleHeight;
    private int mSmallScaleColor;
    private float mBigScaleWidth;
    private float mBigScaleHeight;
    private int mBigScaleColor;
    private int mDashTitleColor;
    private float mDashTitleSize;
    private int mDashTextColor;
    private float mDashTextSize;
    private int mNormalArcColor;
    private int mDangerArcColor;
    private int mWarningArcColor;
    private int mPointerColor;
    private Rect mDashRect;
    private int mPointerSize;

    public DashView(Context context) {
        this(context, null);
    }

    public DashView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DashView);
        //圆弧的属性
        mArcWidth = ta.getDimensionPixelSize(R.styleable.DashView_arc_width, dp2px(30));
        mNormalArcColor = ta.getColor(R.styleable.DashView_normal_arc_color, Color.parseColor("#00B8F1"));
        mDangerArcColor = ta.getColor(R.styleable.DashView_danger_arc_color, Color.parseColor("#00B8F1"));
        mWarningArcColor = ta.getColor(R.styleable.DashView_warning_arc_color, Color.parseColor("#00B8F1"));

        //指针的颜色
        mPointerColor = ta.getColor(R.styleable.DashView_pointer_color, Color.parseColor("#00B8F1"));
        mPointerSize = ta.getDimensionPixelSize(R.styleable.DashView_pointer_size, dp2px(30));

        //小刻度的属性 长宽颜色
        mSmallScaleWidth = ta.getDimensionPixelSize(R.styleable.DashView_small_scale_width, dp2px(2));
        mSmallScaleHeight = ta.getDimension(R.styleable.DashView_small_scale_height, dp2px(10));
        mSmallScaleColor = ta.getColor(R.styleable.DashView_small_scale_color, Color.parseColor("#FFFF5722"));

        //大刻度的属性 长宽颜色
        mBigScaleWidth = ta.getDimension(R.styleable.DashView_big_scale_width, dp2px(4));
        mBigScaleHeight = ta.getDimension(R.styleable.DashView_big_scale_height, dp2px(20));
        mBigScaleColor = ta.getColor(R.styleable.DashView_big_scale_color, Color.parseColor("#FFFF5722"));

        //仪表盘的title颜色 和字体大小
        mDashTitleColor = ta.getColor(R.styleable.DashView_dash_title_color, Color.parseColor("#333333"));
        mDashTitleSize = ta.getDimension(R.styleable.DashView_dash_title_size, dp2px(16));

        //仪表盘 其他文字的颜色和字体大小
        mDashTextColor = ta.getColor(R.styleable.DashView_dash_text_color, Color.parseColor("#999999"));
        mDashTextSize = ta.getDimension(R.styleable.DashView_dash_text_size, dp2px(14));

        ta.recycle();  //注意回收

        init();
    }

    private void init() {
        currentValue = 0f;
        mTitle = "出水COD";
        mTime = "05-07 16:30:30";
        mStandard = "报警：580~";

        mTitlePoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePoint.setTextSize(mDashTitleSize);
        mTitlePoint.setColor(mDashTitleColor);
        mTitlePoint.setTypeface(Typeface.DEFAULT_BOLD);
        mTitlePoint.setTextAlign(Paint.Align.CENTER);

        mTextPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPoint.setTextSize(mDashTextSize);
        mTextPoint.setColor(mDashTextColor);
        mTextPoint.setTextAlign(Paint.Align.CENTER);

        mSmallScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallScalePaint.setStrokeWidth(dp2px(1));
        mSmallScalePaint.setStyle(Paint.Style.STROKE);
        mSmallScalePaint.setColor(mSmallScaleColor);
        mSmallScalePaint.setStrokeJoin(Paint.Join.BEVEL);

        mBigScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBigScalePaint.setStrokeWidth(dp2px(1));
        mBigScalePaint.setStyle(Paint.Style.STROKE);
        mBigScalePaint.setColor(mBigScaleColor);
        mBigScalePaint.setStrokeJoin(Paint.Join.BEVEL);

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStrokeWidth(mArcWidth);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setColor(mNormalArcColor);
        mArcPaint.setStrokeJoin(Paint.Join.BEVEL);

        mPointerPaint = new Paint();
        mPointerPaint.setStyle(Paint.Style.FILL);
        mPointerPaint.setColor(mPointerColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = getMeasuredHeight();
        int measuredWidth = getMeasuredWidth();

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "measuredHeight = " + height);
        Log.d(TAG, "measuredWidth = " + width);
        int min = Math.min(width, height);
        setMeasuredDimension(getDefaultSize(min,widthMeasureSpec), getDefaultSize(min,widthMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDashRect = new Rect();
        mDashRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());

        Log.d(TAG, "onDraw: width = " + getWidth());
        Log.d(TAG, "onDraw: height = " + getHeight());

        @SuppressLint("DrawAllocation")
        RectF rectF = new RectF(PADDING, PADDING, mDashRect.width() - PADDING, mDashRect.height() - PADDING);

        //获取弧线的长度
        Path arcPath = new Path();
        arcPath.addArc(rectF, startAngle, sweepAngle);
        PathMeasure measure = new PathMeasure(arcPath, false);
        float advance = (measure.getLength() - dp2px(4)) / 20;

        //画短的刻度
        Path path = new Path();
        path.addRect(0, 0, mSmallScaleWidth, mSmallScaleHeight, Path.Direction.CCW);
        @SuppressLint("DrawAllocation")
        PathDashPathEffect effect = new PathDashPathEffect(path, advance, 0, PathDashPathEffect.Style.ROTATE);
        mSmallScalePaint.setPathEffect(effect);
        canvas.drawArc(rectF, startAngle, sweepAngle, false, mSmallScalePaint);
        mSmallScalePaint.setPathEffect(null);

        //画长的刻度
        float advance2 = (measure.getLength() - dp2px(4)) / 5;
        path.addRect(0, 0, mBigScaleWidth, mBigScaleHeight, Path.Direction.CCW);
        @SuppressLint("DrawAllocation")
        PathDashPathEffect effect2 = new PathDashPathEffect(path, advance2, 0, PathDashPathEffect.Style.ROTATE);
        mBigScalePaint.setPathEffect(effect2);
        canvas.drawArc(rectF, startAngle, sweepAngle, false, mBigScalePaint);
        mBigScalePaint.setPathEffect(null);

        //绘制刻度值
        drawScaleValue(canvas);

        // 画外环大圆弧
        mArcPaint.setStrokeWidth(mArcWidth);
        float arcWidth = mArcWidth / 2;
        @SuppressLint("DrawAllocation")
        RectF arcRectF = new RectF(PADDING - arcWidth, PADDING - arcWidth,
                mDashRect.width() - PADDING + arcWidth, getHeight() - PADDING + arcWidth);
        canvas.drawArc(arcRectF, startAngle, sweepAngle, false, mArcPaint);

        //绘制指针
        drawPointer(canvas);

        //绘制文字
        drawText(canvas);
    }

    private void drawScaleValue(Canvas canvas) {
        canvas.save();
        canvas.translate(mDashRect.width() / 2, mDashRect.height() / 2);

        //因为在y轴上写字  所以需要
        canvas.rotate(startAngle + 90);
        int count = 5; //总刻度数

        //绘制刻度和百分比
        for (int i = 0; i <= count; i++) {
            canvas.drawText(String.valueOf(i), 0, -mDashRect.width() / 4 + dp2px(10), mTextPoint);
            canvas.rotate(54);
        }
        canvas.restore();
    }


    private void drawPointer(Canvas canvas) {
        canvas.save();
        canvas.translate(mDashRect.width() / 2, mDashRect.height() / 2);
        canvas.rotate(startAngle + currentValue);

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
        //绘制时间文字
        int bootomPadding = dp2px(10);
        Paint.FontMetrics fontMetrics = mTextPoint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        float leading = fontMetrics.leading;
        float baseLineY = bottom - leading;//文字底部到基线的位置
        canvas.drawText(mTime, mDashRect.width() / 2, mDashRect.height() - bootomPadding - baseLineY, mTextPoint);

        //绘制标准字样文字
        float textHeight = bottom - top;
        canvas.drawText(mStandard, mDashRect.width() / 2, mDashRect.height() - bootomPadding - textHeight - baseLineY, mTextPoint);

        //绘制 title文字
        Paint.FontMetrics titleFontMetrics = mTitlePoint.getFontMetrics();
        float titleBaseLineY = titleFontMetrics.bottom - titleFontMetrics.leading;
        //在控件 4/5 高度位置画 title值
        // canvas.drawText(mTitle, mDashRect.width() / 2, mDashRect.height()  - bootomPadding - textHeight * 2 - titleBaseLineY - bootomPadding, mTitlePoint);
        canvas.drawText(mTitle, mDashRect.width() / 2, mDashRect.height() / 5 * 4 - titleBaseLineY, mTitlePoint);

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

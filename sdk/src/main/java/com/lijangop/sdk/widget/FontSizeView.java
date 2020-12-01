package com.lijangop.sdk.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.SizeUtils;
import com.lijangop.sdk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2020/4/9 09:33
 */
public class FontSizeView extends View {
    private int defaultLineColor = Color.rgb(33, 33, 33);
    private int defaultLineWidth;
    private int defaultCircleRadius;
    private int currentProgress;

    /**
     * 设置默认的起始位置
     *
     * @param position
     */
    public void setDefaultPosition(int position) {
        this.defaultPosition = position;
    }

    // 默认位置
    private int defaultPosition = 1;

    // 一共有多少格
    private int max       = 8;
    // 线条颜色
    private int lineColor = Color.BLACK;
    // 线条粗细
    private int lineWidth;

    //字体颜色
    private int textColor = Color.BLACK;

    //字体大小
    private int smallSize   = 13;
    private int standerSize = 16;
    private int bigSize     = 28;

    // 圆半径
    private int         circleRadius;
    private int         circleColor = Color.WHITE;
    // 一段的宽度，根据总宽度和总格数计算得来
    private int         itemWidth;
    // 控件的宽高
    private int         height;
    // 画笔
    private Paint       mLinePaint;
    private Paint       mTextPaint;
    private Paint       mText1Paint;
    private Paint       mText2Paint;
    private Paint       mCirclePaint;
    // 滑动过程中x坐标
    private float       currentX    = 0;
    // 有效数据点
    private List<Point> points      = new ArrayList<>();

    private float circleY;
    private float textScaleX;

    public FontSizeView(Context context) {
        this(context, null);
    }

    public FontSizeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        // initDefault
        defaultLineWidth = SizeUtils.dp2px(2);
        defaultCircleRadius = SizeUtils.dp2px(35);
        lineWidth = SizeUtils.dp2px(1);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FontSizeView);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            initCustomAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
        // 初始化画笔
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(lineColor);
        mLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mLinePaint.setStrokeWidth(lineWidth);

        //文字画笔
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(textColor);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        //        mTextPaint.setStrokeWidth(DensityUtils.dp2px(context, 1));
        mTextPaint.setTextSize(SizeUtils.sp2px(smallSize));
        textScaleX = mTextPaint.measureText("A");
        //文字画笔
        mText1Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mText1Paint.setColor(textColor);
        mText1Paint.setStyle(Paint.Style.FILL_AND_STROKE);
        mText1Paint.setTextSize(SizeUtils.sp2px(bigSize));

        //文字画笔
        mText2Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mText2Paint.setColor(textColor);
        mText2Paint.setStyle(Paint.Style.FILL_AND_STROKE);
        mText2Paint.setTextSize(SizeUtils.sp2px(standerSize));

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(circleColor);
        mCirclePaint.setStyle(Paint.Style.FILL);

        // 设置阴影效果
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mCirclePaint.setShadowLayer(2, 0, 0, Color.rgb(33, 33, 33));
    }

    private void initCustomAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.FontSizeView_lineColor) {
            lineColor = typedArray.getColor(attr, defaultLineColor);
        } else if (attr == R.styleable.FontSizeView_circleColor) {
            int defaultCircleColor = Color.WHITE;
            circleColor = typedArray.getColor(attr, defaultCircleColor);
        } else if (attr == R.styleable.FontSizeView_lineWidth) {
            lineWidth = typedArray.getDimensionPixelSize(attr, defaultLineWidth);
        } else if (attr == R.styleable.FontSizeView_circleRadius) {
            circleRadius = typedArray.getDimensionPixelSize(attr, defaultCircleRadius);
        } else if (attr == R.styleable.FontSizeView_totalCount) {
            int defaultMax = 5;
            max = typedArray.getInteger(attr, defaultMax);
        } else if (attr == R.styleable.FontSizeView_textFontColor) {
            textColor = typedArray.getColor(attr, textColor);
        } else if (attr == R.styleable.FontSizeView_smallSize) {
            smallSize = typedArray.getInteger(attr, smallSize);
        } else if (attr == R.styleable.FontSizeView_standerSize) {
            standerSize = typedArray.getInteger(attr, standerSize);
        } else if (attr == R.styleable.FontSizeView_bigSize) {
            bigSize = typedArray.getInteger(attr, bigSize);
        } else if (attr == R.styleable.FontSizeView_defaultPosition) {
            defaultPosition = typedArray.getInteger(attr, defaultPosition);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
        circleY = height / 2;
        // 横线宽度是总宽度-2个圆的半径
        itemWidth = (w - getPaddingLeft() - getPaddingRight() - 2 * circleRadius) / max;
        // 把可点击点保存起来
        for (int i = 0; i <= max; i++) {
            points.add(new Point(circleRadius + i * itemWidth + getPaddingLeft(), height / 2));
        }
        //初始刻度
        currentX = points.get(defaultPosition).x;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int startX = points.get(0).x;

        int endX = points.get(points.size() - 1).x;

        int halfHeight = height / 2;

        float halfTextScaleX = textScaleX / 2;

        //画字
        canvas.drawText("A", startX - halfTextScaleX, halfHeight - 50, mTextPaint);

        //画字
        canvas.drawText("标准", points.get(2).x - halfTextScaleX - getPaddingLeft(), halfHeight - 50, mText2Paint);

        //画字
        canvas.drawText("A", endX - textScaleX, halfHeight - 50, mText1Paint);

        // 先画中间的横线
        canvas.drawLine(startX, halfHeight, endX, halfHeight, mLinePaint);
        // 绘制刻度
        for (Point point : points) {
            canvas.drawLine(point.x + 1, halfHeight - 15, point.x + 1, halfHeight + 15, mLinePaint);
        }

        // 画圆
        if (currentX < startX) {//起始点x小于刻度
            currentX = startX;
        }
        if (currentX > endX) {//结束x点大于刻度
            currentX = endX;
        }

        // 实体圆
        canvas.drawCircle(currentX + 1, circleY, circleRadius, mCirclePaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        currentX = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //回到最近的一个刻度点
                Point targetPoint = getNearestPoint(currentX);
                if (targetPoint != null) {
                    // 最终
                    currentX = points.get(currentProgress).x;
                    invalidate();
                }
                if (onChangeCallbackListener != null) {
                    onChangeCallbackListener.onChangeListener(currentProgress);
                }
                break;
        }
        return true;
    }

    /**
     * 获取最近的刻度
     */
    private Point getNearestPoint(float x) {
        int halfItemWidth = itemWidth / 2;
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            if (Math.abs(point.x - x) < halfItemWidth) {
                currentProgress = i;
                return point;
            }
        }
        return null;
    }

    public void setChangeCallbackListener(OnChangeCallbackListener listener) {
        this.onChangeCallbackListener = listener;
    }

    private OnChangeCallbackListener onChangeCallbackListener;

    public interface OnChangeCallbackListener {
        void onChangeListener(int position);
    }
}

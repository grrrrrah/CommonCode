package com.lijangop.sdk.widget.animatebutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.lijangop.sdk.R;

/**
 * @Author lijiangop
 * @CreateTime 2020/8/20 09:51
 */
public class MaskShapeImageView extends MaskImageView {

    private Drawable shape;
    private Matrix   matrix, drawMatrix;

    public MaskShapeImageView(Context context) {
        super(context);
        setup(context, null, 0);
    }

    public MaskShapeImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs, 0);
    }

    public MaskShapeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs, defStyleAttr);
    }

    private void setup(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PorterImageView, defStyle, 0);
            shape = typedArray.getDrawable(R.styleable.PorterImageView_siShape);
            typedArray.recycle();
        }
        matrix = new Matrix();
    }

    @Override
    protected void paintMaskCanvas(Canvas maskCanvas, Paint maskPaint, int width, int height) {
        if (shape != null) {
            if (shape instanceof BitmapDrawable) {
                drawMatrix = null;
                int viewWidth = getWidth();
                int viewHeight = getHeight();
                int drawableWidth = shape.getIntrinsicWidth();
                int drawableHeight = shape.getIntrinsicHeight();
                boolean fits = viewWidth == drawableWidth && viewHeight == drawableHeight;
                if (drawableWidth > 0 && drawableHeight > 0 && !fits) {
                    shape.setBounds(0, 0, drawableWidth, drawableHeight);
                    float widthRatio = (float) viewWidth / (float) drawableWidth;
                    float heightRatio = (float) viewHeight / (float) drawableHeight;
                    float scale = Math.min(widthRatio, heightRatio);
                    float dx = (int) ((viewWidth - drawableWidth * scale) * 0.5f + 0.5f);
                    float dy = (int) ((viewHeight - drawableHeight * scale) * 0.5f + 0.5f);
                    matrix.setScale(scale, scale);
                    matrix.postTranslate(dx, dy);
                }
                if (drawMatrix != null) {
                    int drawableSaveCount = maskCanvas.getSaveCount();
                    maskCanvas.save();
                    maskCanvas.concat(matrix);
                    shape.draw(maskCanvas);
                    maskCanvas.restoreToCount(drawableSaveCount);
                    return;
                }
            }
            shape.setBounds(0, 0, getWidth(), getHeight());
            shape.draw(maskCanvas);
        }
    }

    //---------------------------------------------------------------------------------------------------------
    void setShape(Drawable drawable) {
        shape = drawable;
        invalidate();
    }
}

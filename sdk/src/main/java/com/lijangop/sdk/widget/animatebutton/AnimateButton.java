package com.lijangop.sdk.widget.animatebutton;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;

import com.lijangop.sdk.R;
import com.lijangop.sdk.utils.ScreenUtils;


/**
 * @Author lijiangop
 * @CreateTime 2020/8/20 09:41
 */
public class AnimateButton extends MaskShapeImageView {

    private static final String TAG = "AnimateButton";

    Activity                activity;
    AnimateView             shineView;
    OnButtonClickListener   onButtonClickListener;
    AnimateView.ShineParams shineParams = new AnimateView.ShineParams();

    int                     bottomHeight;
    ValueAnimator           shakeAnimator;
    OnCheckedChangeListener mOnCheckedChangeListener;
    DisplayMetrics          metrics = new DisplayMetrics();

    private int     btnColor;
    private int     btnFillColor;
    private boolean isChecked = false;

    public AnimateButton(Context context) {
        super(context);
        initButton(context, null);
    }

    public AnimateButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initButton(context, attrs);
    }

    public AnimateButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initButton(context, attrs);
    }

    private void initButton(Context context, AttributeSet attrs) {
        if (context instanceof Activity) {
            this.activity = (Activity) context;
            onButtonClickListener = new OnButtonClickListener();
            setOnClickListener(onButtonClickListener);
        }
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShineButton);
            btnColor = a.getColor(R.styleable.ShineButton_btn_color, Color.GRAY);
            btnFillColor = a.getColor(R.styleable.ShineButton_btn_fill_color, Color.BLACK);
            shineParams.allowRandomColor = a.getBoolean(R.styleable.ShineButton_allow_random_color, false);
            shineParams.animDuration = a.getInteger(R.styleable.ShineButton_shine_animation_duration, (int) shineParams.animDuration);
            shineParams.bigShineColor = a.getColor(R.styleable.ShineButton_big_shine_color, shineParams.bigShineColor);
            shineParams.clickAnimDuration = a.getInteger(R.styleable.ShineButton_click_animation_duration, (int) shineParams.clickAnimDuration);
            shineParams.enableFlashing = a.getBoolean(R.styleable.ShineButton_enable_flashing, false);
            shineParams.shineCount = a.getInteger(R.styleable.ShineButton_shine_count, shineParams.shineCount);
            shineParams.shineDistanceMultiple = a.getFloat(R.styleable.ShineButton_shine_distance_multiple, shineParams.shineDistanceMultiple);
            shineParams.shineTurnAngle = a.getFloat(R.styleable.ShineButton_shine_turn_angle, shineParams.shineTurnAngle);
            shineParams.smallShineColor = a.getColor(R.styleable.ShineButton_small_shine_color, shineParams.smallShineColor);
            shineParams.smallShineOffsetAngle = a.getFloat(R.styleable.ShineButton_small_shine_offset_angle, shineParams.smallShineOffsetAngle);
            shineParams.shineSize = a.getDimensionPixelSize(R.styleable.ShineButton_shine_size, shineParams.shineSize);
            a.recycle();
            setSrcColor(btnColor);
        }
    }

    @Override
    public void setOnClickListener(View.OnClickListener l) {
        if (l instanceof OnButtonClickListener) {
            super.setOnClickListener(l);
        } else {
            if (onButtonClickListener != null) {
                onButtonClickListener.setListener(l);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (activity != null && metrics != null) {
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int[] location = new int[2];
            getLocationInWindow(location);
            if (ScreenUtils.hasNotchInScreen(activity))
                bottomHeight = metrics.heightPixels - location[1] + getStatusBarHeight();
            else
                bottomHeight = metrics.heightPixels - location[1];
        }
    }

    public int getStatusBarHeight() {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    //-------------------------------------------------------------------内部方法----------------------------------------------------------------
    //开始动画
    public void showAnim() {
        if (activity != null) {
            final ViewGroup rootView = activity.findViewById(Window.ID_ANDROID_CONTENT);
            shineView = new AnimateView(activity, this, shineParams);
            rootView.addView(shineView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            shakeAnimator = ValueAnimator.ofFloat(0.4f, 1f, 0.9f, 1f);
            shakeAnimator.setInterpolator(new LinearInterpolator());
            shakeAnimator.setDuration(500);
            shakeAnimator.setStartDelay(180);
            invalidate();
            shakeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    setScaleX((float) valueAnimator.getAnimatedValue());
                    setScaleY((float) valueAnimator.getAnimatedValue());
                }
            });
            shakeAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    setSrcColor(btnFillColor);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    setSrcColor(isChecked ? btnFillColor : btnColor);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    setSrcColor(btnColor);
                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            shakeAnimator.start();
        } else {
            Log.e(TAG, "Please init.");
        }
    }

    //结束动画
    public void setCancel() {
        setSrcColor(btnColor);
        if (shakeAnimator != null) {
            shakeAnimator.end();
            shakeAnimator.cancel();
        }
    }

    //监听
    private void onListenerUpdate(boolean checked) {
        if (mOnCheckedChangeListener != null)
            mOnCheckedChangeListener.onCheckedChanged(this, checked);
    }

    //--------------------------------------------------------------------内部类----------------------------------------------------------------
    public class OnButtonClickListener implements View.OnClickListener {
        public void setListener(View.OnClickListener listener) {
            this.listener = listener;
        }

        View.OnClickListener listener;

        @Override
        public void onClick(View view) {
            if (!isChecked) {
                isChecked = true;
                showAnim();
            } else {
                isChecked = false;
                setCancel();
            }
            onListenerUpdate(isChecked);
            if (listener != null) {
                listener.onClick(view);
            }
        }
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(View view, boolean checked);
    }

    //------------------------------------------------------------------供外部使用-----------------------------------------------------
    public int getFillColor() {
        return btnFillColor;
    }

    public int getBottomHeight() {
        return bottomHeight;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void removeView(View view) {
        if (activity != null) {
            final ViewGroup rootView = activity.findViewById(Window.ID_ANDROID_CONTENT);
            rootView.removeView(view);
        } else {
            Log.e(TAG, "Please init.");
        }
    }

}

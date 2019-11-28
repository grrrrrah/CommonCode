package com.lijangop.sdk.utils;

import android.animation.Animator;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.lijangop.sdk.definterface.SimpleAnimatorListener;

/**
 * @Author lijiangop
 * @CreateTime 2019/11/20 16:58
 */
public class AnimateUtils {

    /**
     * 一个以目标view为中心,将其向四周圆形展开/收起的动画(默认展开后铺满屏幕)
     * @param centerX 展开中心点x
     * @param centerY 展开中心点y
     * @param animateView 目标view
     */
    public static void handleCircleAnimate(int centerX, int centerY, final View animateView) {
        if (animateView.getVisibility() == View.GONE || animateView.getVisibility() == View.INVISIBLE) {
            //显示
            //这里需要说明的是，如果你的布局文件里面animateView设置的不占位隐藏，也就是gone，你第一次获取animateView的长宽的时候，获取不到，会是0。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Animator animatorShow = ViewAnimationUtils.createCircularReveal(animateView, centerX, centerY, 0,
                        (float) Math.hypot(animateView.getWidth(), animateView.getHeight()));
                animatorShow.addListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationStart() {
                        animateView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd() {
                    }
                });
                animatorShow.setDuration(450);
                animatorShow.start();
            } else
                animateView.setVisibility(View.VISIBLE);
            animateView.setEnabled(true);
        } else {
            //隐藏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                /**
                 * createCircularReveal 方法参数
                 * view 执行动画的view
                 * centerX 圆心横坐标
                 * centerY 圆心纵坐标
                 * startRadius 动画开始时圆的半径
                 * endRadius 动画结束时圆的半径
                 */
                final Animator animatorHide = ViewAnimationUtils.createCircularReveal(animateView, centerX, centerY,
                        (float) Math.hypot(animateView.getWidth(), animateView.getHeight()), //确定圆的半径（算长宽的斜边长，这样半径不会太短也不会很长效果比较舒服）
                        0);
                animatorHide.addListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationStart() {
                    }

                    @Override
                    public void onAnimationEnd() {
                        animateView.setVisibility(View.GONE);
                    }
                });
                animatorHide.setDuration(450);
                animatorHide.start();
            } else
                animateView.setVisibility(View.GONE);
            animateView.setEnabled(false);
        }
    }
}

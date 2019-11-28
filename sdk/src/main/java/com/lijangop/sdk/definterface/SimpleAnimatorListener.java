package com.lijangop.sdk.definterface;

import android.animation.Animator;

/**
 * @Author lijiangop
 * @CreateTime 2019/11/20 17:15
 */
public abstract class SimpleAnimatorListener implements Animator.AnimatorListener {
    @Override
    public void onAnimationStart(Animator animator) {
        onAnimationStart();
    }

    public abstract void onAnimationStart();

    @Override
    public void onAnimationEnd(Animator animator) {
        onAnimationEnd();
    }
    
    public abstract void onAnimationEnd();

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}

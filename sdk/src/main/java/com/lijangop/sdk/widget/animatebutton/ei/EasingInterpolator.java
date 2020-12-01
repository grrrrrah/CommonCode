package com.lijangop.sdk.widget.animatebutton.ei;

import android.animation.TimeInterpolator;
import android.support.annotation.NonNull;

/**
 * @Author lijiangop
 * @CreateTime 2020/8/20 10:44
 */
public class EasingInterpolator implements TimeInterpolator {

    private final Ease ease;

    public EasingInterpolator(@NonNull Ease ease) {
        this.ease = ease;
    }

    @Override
    public float getInterpolation(float input) {
        return EasingProvider.get(this.ease, input);
    }

    public Ease getEase() {
        return ease;
    }
}

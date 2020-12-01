package com.lijangop.sdk.widget.bottomnavigationview;

import android.view.View;

/**
 * @Author lijiangop
 * @CreateTime 2020/8/24 18:06
 */
public interface OnTabClickListener {
    boolean interceptFromSelectEvent(View view, int position);
}

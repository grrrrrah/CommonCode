package com.lijangop.commoncode.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

/**
 * @Author lijiangop
 * @CreateTime 2020/8/21 14:47
 */
public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        setTheme(R.style.fullscreen);
        //        setContentView(R.layout.activity_splash);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startLoginWithAnimate();
        return super.onTouchEvent(event);
    }

    //取消startActivity的过度动画
    private void startLoginWithAnimate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                SplashActivity.this.finish();
                //取消界面跳转时的动画，使启动页的logo图片与注册、登录主页的logo图片完美衔接
                overridePendingTransition(0, 0);
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        if (handler != null) {
            //If token is null, all callbacks and messages will be removed.
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}

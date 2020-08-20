package com.lijangop.commoncode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lijangop.sdk.utils.MarketUtils;

/**
 * 测试Project
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void test(View view) {
        MarketUtils.goToMarket(this,getPackageName());
    }
}

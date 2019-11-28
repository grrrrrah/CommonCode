package com.lijangop.commoncode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.lijangop.sdk.FileUtils;

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
        Toast.makeText(this, FileUtils.getName(), Toast.LENGTH_SHORT).show();
    }
}

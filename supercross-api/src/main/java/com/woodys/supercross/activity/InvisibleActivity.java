package com.woodys.supercross.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * 当跨app调用时，为了绕过AIDL通讯引发手机的反链式启动程序的限制
 * 解决问题：android 高版本限制了当应用没有开启时，不能通过service和ContentProvider进行数据传递
 */
public final class InvisibleActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.START | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }
}

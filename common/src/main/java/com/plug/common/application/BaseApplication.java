package com.plug.common.application;

import android.app.Application;

import com.okay.supercross.BuildConfig;
import com.okay.supercross.SuperCross;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //线上环境，记得要关闭
        SuperCross.setEnableLog(BuildConfig.DEBUG);
        /*
        //假如需要定制log输出，可用使用如下配置
        SuperCross.setLogDelegate(new LogDelegate() {
            @Override
            public void v(String tag, String msg) {
                Log.v(tag, msg);
            }

            @Override
            public void d(String tag, String msg) {
                Log.d(tag, msg);
            }

            @Override
            public void i(String tag, String msg) {
                Log.i(tag, msg);
            }

            @Override
            public void w(String tag, String msg) {
                Log.w(tag, msg);
            }

            @Override
            public void e(String tag, String msg, Throwable tr) {
                Log.e(tag, msg, tr);
            }

        });
        */
        SuperCross.init(this);
    }
}

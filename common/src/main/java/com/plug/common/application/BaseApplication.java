package com.plug.common.application;

import android.app.Application;

import com.okay.supercross.BuildConfig;
import com.okay.supercross.SuperCross;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SuperCross.setEnableLog(BuildConfig.DEBUG);
        SuperCross.init(this);
    }
}

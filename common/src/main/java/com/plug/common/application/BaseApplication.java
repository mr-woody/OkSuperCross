package com.plug.common.application;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.okay.supercross.BuildConfig;
import com.okay.supercross.SuperCross;
import com.okay.supercross.application.ApplicationDelegate;

public class BaseApplication extends Application {
    public static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        ApplicationDelegate.onApplicationCreate(this);

        SuperCross.setEnableLog(BuildConfig.DEBUG);
        SuperCross.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ApplicationDelegate.onApplicationTerminate(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ApplicationDelegate.onApplicationConfigurationChanged(this,newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ApplicationDelegate.onApplicationLowMemory(this);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        ApplicationDelegate.onApplicationTrimMemory(this,level);
    }
}

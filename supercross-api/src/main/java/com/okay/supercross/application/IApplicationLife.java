package com.okay.supercross.application;

import android.app.Application;
import android.content.res.Configuration;

/**
 * 模块化，module生命周期管理类
 */
public interface IApplicationLife {
    void onCreate(Application application);

    void onTerminate(Application application);

    void onConfigurationChanged(Application application, Configuration newConfig);

    void onLowMemory(Application application);

    void onTrimMemory(Application application, int level);
}

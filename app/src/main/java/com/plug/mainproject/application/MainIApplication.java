package com.plug.mainproject.application;

import android.app.Application;
import android.content.res.Configuration;

import com.okay.supercross.annotation.RegistApplication;
import com.okay.supercross.application.IApplicationLife;

@RegistApplication
public class MainIApplication implements IApplicationLife {
    @Override
    public void onCreate(Application application) {
    }

    @Override
    public void onTerminate(Application application) {

    }

    @Override
    public void onConfigurationChanged(Application application, Configuration newConfig) {

    }

    @Override
    public void onLowMemory(Application application) {

    }

    @Override
    public void onTrimMemory(Application application, int level) {

    }
}
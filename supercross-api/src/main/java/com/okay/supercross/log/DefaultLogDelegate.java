package com.okay.supercross.log;

import android.util.Log;

public class DefaultLogDelegate implements LogDelegate {

    private static final DefaultLogDelegate sInstance = new DefaultLogDelegate();

    public static DefaultLogDelegate getInstance() {
        return sInstance;
    }

    private DefaultLogDelegate() { }


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

    @Override
    public void e(String tag, String msg) {
        Log.e(tag, msg);
    }
}

package com.woodys.supercross.utils;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import com.woodys.supercross.log.Debugger;

public class ServiceUtils {
    private static final String TAG = ServiceUtils.class.getSimpleName();

    /**
     * 考虑到Android 8.0在后台调用startService时会抛出IllegalStateException
     *
     * @param context
     * @param intent
     */
    public static void startServiceSafely(Context context, Intent intent) {
        if (null == context) {
            return;
        }
        try {
            context.startService(intent);
        } catch (IllegalStateException ex) {
           Debugger.e(TAG,ex);
        }
    }

    public static void unbindSafely(Context context, ServiceConnection connection) {
        if (context == null || connection == null) {
            return;
        }
        try {
            context.unbindService(connection);
        } catch (Exception ex) {
            Debugger.e(TAG,"unbind service exception:" + ex.getMessage());
        }
    }

}

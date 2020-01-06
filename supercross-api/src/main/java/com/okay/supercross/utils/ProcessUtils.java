package com.okay.supercross.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.okay.supercross.log.Debugger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


/**
 * 进程辅助工具类
 */
public class ProcessUtils {

    /**
     * 判断当前进程是否是主进程
     * @param context
     * @return
     */
    public static boolean isMainProcess(Context context) {
        String processName = getProcessName(context);
        if (null!=processName && processName.equals(context.getPackageName())) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前context进程名称
     *
     * @param context
     * @return
     */
    @Nullable
    public static String getProcessName(Context context) {
        return getProcessName(context,android.os.Process.myPid());
    }


    /**
     * 获取pid对应的进程名称
     * @param pid
     * @return
     */
    @Nullable
    public static String getProcessName(Context context,int pid) {
        // get by ams
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return null;
        }
        List<ActivityManager.RunningAppProcessInfo> processes = manager.getRunningAppProcesses();
        if (processes != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
                if (processInfo.pid == pid) {
                    return processInfo.processName;
                }
            }
        }
        return null;
    }
}

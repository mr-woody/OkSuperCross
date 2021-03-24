package com.woodys.supercross.log;

import android.text.TextUtils;
import android.util.Log;

/**
 * debug 日志打印，方便开发时检查问题
 */
public class Debugger {
    private static final String LOG_TAG = "OkSuperCross";
    private static int levelValue = Log.DEBUG;

    private static LogDelegate logDelegate = DefaultLogDelegate.getInstance();

    public static void setLogDelegate(LogDelegate delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException();
        }
        logDelegate = delegate;
    }
    
    /**
     * 设置日志等级
     */
    public static void setLogLevel(int level) {
        levelValue = level;
    }

    private static boolean isLoggable(int level) {
        return levelValue <= level;
    }

    public static void d(String tag, String msg, Object... args) {
        if (TextUtils.isEmpty(msg)) return;
        if(isLoggable(Log.DEBUG)){
            logDelegate.d(Debugger.LOG_TAG, format(tag,msg, args));
        }
    }

    public static void i(String tag, String msg, Object... args) {
        if (TextUtils.isEmpty(msg)) return;
        if(isLoggable(Log.INFO)){
            logDelegate.i(Debugger.LOG_TAG, format(tag,msg, args));
        }
    }

    public static void w(String tag, String msg, Object... args) {
        if (TextUtils.isEmpty(msg)) return;
        if(isLoggable(Log.WARN)){
            logDelegate.w(Debugger.LOG_TAG, format(tag,msg, args));
        }
    }


    public static void e(String tag, String msg, Object... args) {
        if (TextUtils.isEmpty(msg)) return;
        if(isLoggable(Log.ERROR)){
            logDelegate.e(Debugger.LOG_TAG, format(tag,msg, args),null);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (tr == null) return;
        if(isLoggable(Log.ERROR)){
            logDelegate.e(Debugger.LOG_TAG, format(tag,msg),tr);
        }
    }

    public static void e(String tag, Throwable tr) {
        if (tr == null) return;
        if(isLoggable(Log.ERROR)){
            logDelegate.e(Debugger.LOG_TAG, format(tag,null),tr);
        }
    }

    private static String format(String tag, String msg, Object... args) {
        if (args != null && args.length > 0) {
            try {
                return String.format(getComposeInfo(tag,msg), args);
            } catch (Throwable tr) {
                tr.printStackTrace();
            }
        }
        return getComposeInfo(tag,msg);
    }

    private static String getComposeInfo(String tag, String info) {
        String composeInfo = null;
        if(!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(info)){
            composeInfo = tag + "-->" + info;
        }else {
            composeInfo = !TextUtils.isEmpty(info)?info:tag;
        }
        return composeInfo;
    }
}

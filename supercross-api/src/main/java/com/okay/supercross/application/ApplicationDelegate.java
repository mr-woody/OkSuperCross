package com.okay.supercross.application;

import android.app.Application;
import android.content.res.Configuration;

import com.okay.supercross.utils.ClazzUtils;

import java.util.List;

public class ApplicationDelegate {

    private static List<String> applicationDelegates;

    private static List<String> getApplicationDelegates(Application baseContext){
        if(applicationDelegates == null){
            applicationDelegates = ClazzUtils.getClassNames(baseContext, "com.okay.supercross.apt");
        }
        return applicationDelegates;
    }

    public static void onApplicationCreate(Application baseContext) {
        List<String> baseContextDelegates = getApplicationDelegates(baseContext);
        for (String appDelegate : baseContextDelegates) {
            String className = appDelegate.substring(appDelegate.lastIndexOf(".") + 1, appDelegate.length());
            className = className.replace("_", ".");
            try {
                Class<?> clazz = Class.forName(className);
                Object obj = clazz.newInstance();
                if (obj instanceof IApplicationLife) {
                    ((IApplicationLife) obj).onCreate(baseContext);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public static void onApplicationTerminate(Application baseContext) {
        List<String> baseContextDelegates = getApplicationDelegates(baseContext);
        for (String appDelegate : baseContextDelegates) {
            String className = appDelegate.substring(appDelegate.lastIndexOf(".") + 1, appDelegate.length());
            className = className.replace("_", ".");
            try {
                Class<?> clazz = Class.forName(className);
                Object obj = clazz.newInstance();
                if (obj instanceof IApplicationLife) {
                    ((IApplicationLife) obj).onTerminate(baseContext);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public static void onApplicationConfigurationChanged(Application baseContext,Configuration newConfig) {
        List<String> baseContextDelegates = getApplicationDelegates(baseContext);
        for (String appDelegate : baseContextDelegates) {
            String className = appDelegate.substring(appDelegate.lastIndexOf(".") + 1, appDelegate.length());
            className = className.replace("_", ".");
            try {
                Class<?> clazz = Class.forName(className);
                Object obj = clazz.newInstance();
                if (obj instanceof IApplicationLife) {
                    ((IApplicationLife) obj).onConfigurationChanged(baseContext, newConfig);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public static void onApplicationLowMemory(Application baseContext) {
        List<String> baseContextDelegates = getApplicationDelegates(baseContext);
        for (String appDelegate : baseContextDelegates) {
            String className = appDelegate.substring(appDelegate.lastIndexOf(".") + 1, appDelegate.length());
            className = className.replace("_", ".");
            try {
                Class<?> clazz = Class.forName(className);
                Object obj = clazz.newInstance();
                if (obj instanceof IApplicationLife) {
                    ((IApplicationLife) obj).onLowMemory(baseContext);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public static void onApplicationTrimMemory(Application baseContext,int level) {
        List<String> baseContextDelegates = getApplicationDelegates(baseContext);
        for (String appDelegate : baseContextDelegates) {
            String className = appDelegate.substring(appDelegate.lastIndexOf(".") + 1, appDelegate.length());
            className = className.replace("_", ".");
            try {
                Class<?> clazz = Class.forName(className);
                Object obj = clazz.newInstance();
                if (obj instanceof IApplicationLife) {
                    ((IApplicationLife) obj).onTrimMemory(baseContext, level);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }
}

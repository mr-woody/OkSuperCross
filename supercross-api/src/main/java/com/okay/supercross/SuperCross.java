package com.okay.supercross;

import android.content.Context;
import android.os.IBinder;
import android.text.TextUtils;

import com.okay.supercross.bean.BinderBean;
import com.okay.supercross.event.Event;
import com.okay.supercross.event.EventCallback;
import com.okay.supercross.local.LocalServiceManager;
import com.okay.supercross.log.Debugger;
import com.okay.supercross.transfer.RemoteTransfer;
import com.okay.supercross.transfer.method.InvocationBridge;
import com.okay.supercross.transfer.method.ServerInterface;
import com.okay.supercross.transfer.method.TransformBinder;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 跨进程通信主入口类
 */
public class SuperCross {

    private static final String TAG = "SuperCross";

    private static Context appContext;

    private static AtomicBoolean initFlag = new AtomicBoolean(false);

    /**
     * 在Application的attachBaseContext方法中初始化，初始化，完成初始化后才能使用框架
     * @param context
     */
    public static void init(Context context) {
        if (initFlag.get() || context == null) {
            return;
        }
        appContext = context;
        RemoteTransfer.getInstance().init(context);
        initFlag.set(true);
    }

    public static Context getAppContext() {
        return appContext;
    }


    /**
     * Log开关。建议测试环境开启，线上环境应该关闭。
     * @param isEnable
     */
    public static void setEnableLog(boolean isEnable) {
        Debugger.setEnableLog(isEnable);
    }


    /**
     * 注册本地服务(注册的进程本地服务需要在本进程取消注册)
     * @param interfaceClass
     * @param serviceImpl
     */
    public static void registerLocalService(Class<?> interfaceClass, Object serviceImpl) {
        if (null == interfaceClass || null == serviceImpl) {
            return;
        }
        LocalServiceManager.getInstance().registerService(interfaceClass.getCanonicalName(), serviceImpl);
    }


    /**
     * 获取本地服务
     * @param interfaceClass
     * @param <T>
     * @return
     */
    public static <T> T getLocalService(Class<?> interfaceClass) {
        if (null == interfaceClass) {
            return null;
        }
        return (T) LocalServiceManager.getInstance().getLocalService(interfaceClass.getCanonicalName());
    }


    /**
     * 取消注册本地服务
     * @param interfaceClass
     */
    public static void unregisterLocalService(Class<?> interfaceClass) {
        if (null == interfaceClass) {
            return;
        }
        LocalServiceManager.getInstance().unregisterService(interfaceClass.getCanonicalName());
    }


    /**
     * 注册远程服务
     * @param interfaceClass  业务服务接口类class
     * @param serviceImpl     业务接口实现类(参数只支持基础类型和序列号对象)
     */
    public static  void registerRemoteService(Class<?> interfaceClass, Object serviceImpl) {
        if (null == interfaceClass || null == serviceImpl) {
            return;
        }
        ServerInterface serverInterface = new ServerInterface(interfaceClass);
        TransformBinder stubBinder = new TransformBinder(serverInterface, serviceImpl);
        RemoteTransfer.getInstance().registerStubService(interfaceClass.getCanonicalName(), stubBinder);
    }


    /**
     * 获取远程服务
     * @param interfaceClass
     * @param <T>
     * @return
     * TODO: 2019/6/30 后续需要考虑提升服务进程的优先级，可以使用 ConnectionManager.getInstance().bindAction 进行提升
     */
    public static synchronized <T> T getRemoteService(Class<?> interfaceClass) {
        if (null == interfaceClass) {
            return null;
        }
        String serviceCanonicalName = interfaceClass.getCanonicalName();
        Debugger.d("-->getRemoteService,serviceName:" + serviceCanonicalName);
        if (TextUtils.isEmpty(serviceCanonicalName)) {
            return null;
        }
        BinderBean binderBean = RemoteTransfer.getInstance().getRemoteServiceBean(serviceCanonicalName);
        if (binderBean == null) {
            Debugger.e("Found no binder for "+serviceCanonicalName+"! Please check you have register implementation for it or proguard reasons!");
            return null;
        }

        ServerInterface serverInterface = new ServerInterface(interfaceClass);
        IBinder binder = binderBean.getBinder();
        if (binder == null) {
            return null;
        }
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationBridge(serverInterface, binder));
    }

    /**
     * 取消注册的远程服务
     * @param interfaceClass
     */
    public static void unregisterRemoteService(Class<?> interfaceClass) {
        if (null == interfaceClass) {
            return;
        }
        RemoteTransfer.getInstance().unregisterStubService(interfaceClass.getCanonicalName());
    }


    /**
     * 订阅事件(可在任意进程的多个位置订阅)
     * @param name
     * @param listener
     */
    public static void subscribe(String name, EventCallback listener) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        RemoteTransfer.getInstance().subscribeEvent(name, listener);
    }

    /**
     * 取消订阅事件，按照EventCallback进行取消
     * @param listener
     */
    public static void unsubscribe(EventCallback listener) {
        if (null == listener) {
            return;
        }
        RemoteTransfer.getInstance().unsubscribeEvent(listener);
    }

    /**
     * 取消订阅事件，按照key进行取消,取消所有订阅这个key的EventCallback回调
     * @param key
     */
    public static void unsubscribe(String key) {
        if (null == key) {
            return;
        }
        RemoteTransfer.getInstance().unsubscribeEvent(key);
    }

    /**
     * 发布事件(发布事件后,多个进程注册的EventCallback会同时回调)
     * @param event
     */
    public static void publish(Event event) {
        if (null == event) {
            return;
        }
        RemoteTransfer.getInstance().publish(event);
    }
}

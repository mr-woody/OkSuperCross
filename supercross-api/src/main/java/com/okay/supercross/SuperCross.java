package com.okay.supercross;

import android.content.Context;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.okay.supercross.bean.BinderBean;
import com.okay.supercross.event.Event;
import com.okay.supercross.event.EventCallback;
import com.okay.supercross.local.LocalServiceManager;
import com.okay.supercross.log.Debugger;
import com.okay.supercross.log.LogDelegate;
import com.okay.supercross.transfer.RemoteTransfer;
import com.okay.supercross.transfer.method.InvocationBridge;
import com.okay.supercross.transfer.method.ServerInterface;
import com.okay.supercross.transfer.method.TransformBinder;
import com.okay.supercross.utils.ProcessUtils;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 跨进程通信主入口类
 */
public class SuperCross {
    private static final String TAG = SuperCross.class.getSimpleName();

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
        Debugger.d(TAG,"init(),currentPid:" + android.os.Process.myPid() + ",processName:" + ProcessUtils.getProcessName(context) + ",thread:" + Thread.currentThread().getName());
    }

    public static Context getAppContext() {
        return appContext;
    }


    /**
     * Log开关。建议测试环境开启，线上环境应该关闭。
     * @param isEnable
     */
    public static void setEnableLog(boolean isEnable) {
        Debugger.setLogLevel(isEnable? Log.DEBUG:Log.ASSERT);
    }

    /**
     * Log扩展接口，方便做日志输出定制（不设置，默认使用DefaultLogDelegate）
     * @param delegate
     */
    public static void setLogDelegate(@NonNull LogDelegate delegate) {
        Debugger.setLogDelegate(delegate);
    }


    /**
     * 注册本地服务(注册的进程本地服务需要在本进程取消注册)
     * @param interfaceClass
     * @param serviceImpl
     */
    public static void registerLocalService(@Nullable Class<?> interfaceClass,@Nullable Object serviceImpl) {
        registerLocalService(interfaceClass,serviceImpl,null);
    }

    /**
     * 注册本地服务(注册的进程本地服务需要在本进程取消注册)
     * @param interfaceClass
     * @param serviceImpl
     * @param tag             附加标示（为支持多实现）
     */
    public static void registerLocalService(@Nullable Class<?> interfaceClass,@Nullable Object serviceImpl,@Nullable String tag) {
        if (null == interfaceClass || null == serviceImpl) {
            return;
        }
        String key = interfaceClass.getCanonicalName();
        if(!TextUtils.isEmpty(tag)){
            key += "##" + tag;
        }
        Debugger.d(TAG,"registerLocalService:key = "+ key +",serviceImpl="+ (serviceImpl!=null?serviceImpl.toString():null) +", currentPid:" + android.os.Process.myPid() + ",processName:" + ProcessUtils.getProcessName(getAppContext()) + ",thread:" + Thread.currentThread().getName());
        LocalServiceManager.getInstance().registerService(key, serviceImpl);
    }


    /**
     * 获取本地服务
     * @param interfaceClass
     * @param <T>
     * @return
     */
    public static @Nullable <T> T getLocalService(@Nullable Class<?> interfaceClass) {
        return getLocalService(interfaceClass,null);
    }

    /**
     * 获取本地服务
     * @param interfaceClass
     * @param tag             附加标示（为支持多实现）
     * @return
     */
    public static @Nullable <T> T getLocalService(@Nullable Class<?> interfaceClass,@Nullable String tag) {
        if (null == interfaceClass) {
            return null;
        }
        String key = interfaceClass.getCanonicalName();
        if(!TextUtils.isEmpty(tag)){
            key += "##" + tag;
        }

        T serviceImpl = (T) LocalServiceManager.getInstance().getLocalService(key);
        Debugger.i(TAG,"getLocalService:key = "+ key +",serviceImpl="+ (serviceImpl!=null?serviceImpl.toString():null) +", currentPid:" + android.os.Process.myPid() + ",processName:" + ProcessUtils.getProcessName(getAppContext()) + ",thread:" + Thread.currentThread().getName());
        return serviceImpl;
    }


    /**
     * 取消注册本地服务
     * @param interfaceClass
     */
    public static void unregisterLocalService(@Nullable Class<?> interfaceClass) {
        unregisterLocalService(interfaceClass,null);
    }

    /**
     * 取消注册本地服务
     * @param interfaceClass
     * @param tag             附加标示（为支持多实现）
     */
    public static void unregisterLocalService(@Nullable Class<?> interfaceClass,@Nullable String tag) {
        if (null == interfaceClass) {
            return;
        }
        String key = interfaceClass.getCanonicalName();
        if(!TextUtils.isEmpty(tag)){
            key += "##" + tag;
        }
        Debugger.i(TAG,"unregisterLocalService:key = "+ key +", currentPid:" + android.os.Process.myPid() + ",processName:" + ProcessUtils.getProcessName(getAppContext()) + ",thread:" + Thread.currentThread().getName());
        LocalServiceManager.getInstance().unregisterService(key);
    }


    /**
     * 注册远程服务
     * @param interfaceClass  业务服务接口类class
     * @param serviceImpl     业务接口实现类(参数只支持基础类型和序列号对象)
     */
    public static void registerRemoteService(@Nullable Class<?> interfaceClass,@Nullable Object serviceImpl) {
        registerRemoteService(interfaceClass,null,serviceImpl);
    }


    /**
     * 注册远程服务
     * @param interfaceClass  业务服务接口类class
     * @param tag             附加标示（为支持多实现）
     * @param serviceImpl     业务接口实现类(参数只支持基础类型和序列号对象)
     */
    public static void registerRemoteService(@Nullable Class<?> interfaceClass,@Nullable String tag,@Nullable Object serviceImpl) {
        if (null == interfaceClass || null == serviceImpl) {
            return;
        }
        ServerInterface serverInterface = new ServerInterface(interfaceClass);
        TransformBinder stubBinder = new TransformBinder(serverInterface, serviceImpl);

        String key = interfaceClass.getCanonicalName();
        if(!TextUtils.isEmpty(tag)){
            key += "##" + tag;
        }
        Debugger.i(TAG,"registerRemoteService:key = "+ key +",serviceImpl="+ (serviceImpl!=null?stubBinder.toString():null) +", currentPid:" + android.os.Process.myPid() + ",processName:" + ProcessUtils.getProcessName(getAppContext()) + ",thread:" + Thread.currentThread().getName());
        RemoteTransfer.getInstance().registerStubService(key, stubBinder);
    }


    /**
     * 获取远程服务
     * @param interfaceClass
     * @param <T>
     * @return
     */
    public static synchronized  @Nullable  <T> T getRemoteService(@Nullable Class<?> interfaceClass) {
        return getRemoteService(interfaceClass,null);
    }

    /**
     * 获取远程服务
     * @param interfaceClass
     * @param tag             附加标示（为支持多实现）
     * @param <T>
     * @return
     */
    public static synchronized  @Nullable <T> T getRemoteService(@Nullable Class<?> interfaceClass, @Nullable String tag) {
        if (null == interfaceClass) {
            return null;
        }
        String key = interfaceClass.getCanonicalName();
        if(!TextUtils.isEmpty(tag)){
            key += "##" + tag;
        }

        BinderBean binderBean = RemoteTransfer.getInstance().getRemoteServiceBean(key);
        if (binderBean == null) {
            Debugger.w(TAG,"Found no binder for "+key+"! Please check you have register implementation for it or proguard reasons!");
            return null;
        }

        ServerInterface serverInterface = new ServerInterface(interfaceClass);
        IBinder binder = binderBean.getBinder();
        if (binder == null) {
            Debugger.w(TAG,"Found no server IBinder,Please check you have register implementation (ServerInterface("+key+"))!");
            return null;
        }
        T serviceImpl = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationBridge(serverInterface, binder));
        Debugger.i(TAG,"getRemoteService:key = "+ key +",serviceImpl="+ (serviceImpl!=null?binder.toString():null) +", currentPid:" + android.os.Process.myPid() + ",processName:" + ProcessUtils.getProcessName(getAppContext()) + ",thread:" + Thread.currentThread().getName());
        return serviceImpl;
    }



    /**
     * 取消注册的远程服务
     * @param interfaceClass
     */
    public static void unregisterRemoteService(@Nullable Class<?> interfaceClass) {
        unregisterRemoteService(interfaceClass,null);
    }


    /**
     * 取消注册的远程服务
     * @param interfaceClass  业务服务接口类class
     * @param tag             附加标示（为支持多实现）
     */
    public static void unregisterRemoteService(@Nullable Class<?> interfaceClass,@Nullable String tag) {
        if (null == interfaceClass) {
            return;
        }
        String key = interfaceClass.getCanonicalName();
        if(!TextUtils.isEmpty(tag)){
            key += "##" + tag;
        }
        Debugger.i(TAG,"unregisterRemoteService:key = "+ key +", currentPid:" + android.os.Process.myPid() + ",processName:" + ProcessUtils.getProcessName(getAppContext()) + ",thread:" + Thread.currentThread().getName());
        RemoteTransfer.getInstance().unregisterStubService(key);
    }


    /**
     * 订阅事件(可在任意进程的多个位置订阅)
     * @param name
     * @param listener
     */
    public static void subscribe(@Nullable String name,@Nullable EventCallback listener) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        if (null == listener) {
            return;
        }
        Debugger.i(TAG,"subscribe:key = "+ name +", currentPid:" + android.os.Process.myPid() + ",processName:" + ProcessUtils.getProcessName(getAppContext()) + ",thread:" + Thread.currentThread().getName());
        RemoteTransfer.getInstance().subscribeEvent(name, listener);
    }

    /**
     * 取消订阅事件，按照EventCallback进行取消
     * @param listener
     */
    public static void unsubscribe(@Nullable EventCallback listener) {
        if (null == listener) {
            return;
        }
        Debugger.i(TAG,"unsubscribe:listener = "+ listener.toString() +", currentPid:" + android.os.Process.myPid() + ",processName:" + ProcessUtils.getProcessName(getAppContext()) + ",thread:" + Thread.currentThread().getName());
        RemoteTransfer.getInstance().unsubscribeEvent(listener);
    }

    /**
     * 取消订阅事件，按照key进行取消,取消所有订阅这个key的EventCallback回调
     * @param key
     */
    public static void unsubscribe(@Nullable String key) {
        if (null == key) {
            return;
        }
        Debugger.i(TAG,"unsubscribe:key = "+ key +", currentPid:" + android.os.Process.myPid() + ",processName:" + ProcessUtils.getProcessName(getAppContext()) + ",thread:" + Thread.currentThread().getName());
        RemoteTransfer.getInstance().unsubscribeEvent(key);
    }

    /**
     * 发布事件(发布事件后,多个进程注册的EventCallback会同时回调)
     * @param event
     */
    public static void publish(@Nullable Event event) {
        if (null == event) {
            return;
        }
        Debugger.i(TAG,"publish:event = "+ event.toString() +", currentPid:" + android.os.Process.myPid() + ",processName:" + ProcessUtils.getProcessName(getAppContext()) + ",thread:" + Thread.currentThread().getName());
        RemoteTransfer.getInstance().publish(event);
    }
}

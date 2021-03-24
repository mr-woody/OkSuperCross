package com.woodys.supercross.dispatcher;

import android.os.IBinder;
import android.os.RemoteException;

import com.woodys.supercross.IDispatcher;
import com.woodys.supercross.bean.BinderBean;
import com.woodys.supercross.dispatcher.event.EventDispatcher;
import com.woodys.supercross.dispatcher.event.IEventDispatcher;
import com.woodys.supercross.dispatcher.service.IServiceDispatcher;
import com.woodys.supercross.dispatcher.service.ServiceDispatcher;
import com.woodys.supercross.event.Event;

public class Dispatcher extends IDispatcher.Stub {

    private IServiceDispatcher serviceDispatcher;
    private IEventDispatcher eventDispatcher;

    public static Dispatcher sInstance;

    private Dispatcher() {
        serviceDispatcher = new ServiceDispatcher();
        eventDispatcher = new EventDispatcher();
    }

    public static Dispatcher getInstance() {
        if (null == sInstance) {
            synchronized (Dispatcher.class) {
                if (null == sInstance) {
                    sInstance = new Dispatcher();
                }
            }
        }
        return sInstance;
    }

    //给同进程的DispatcherService调用的和远程调用
    @Override
    public synchronized void registerRemoteTransfer(int pid, IBinder transferBinder) {
        if (pid < 0) {
            return;
        }
        eventDispatcher.registerRemoteTransferLocked(pid, transferBinder);
    }


    @Override
    public synchronized BinderBean getTargetBinder(String serviceCanonicalName) throws RemoteException {
        return serviceDispatcher.getTargetBinderLocked(serviceCanonicalName);
    }

    @Override
    public synchronized IBinder fetchTargetBinder(String uri) throws RemoteException {
        //作为保留接口，后面可能会用到
        return null;
    }

    @Override
    public synchronized void registerRemoteService(String serviceCanonicalName, String processName, IBinder binder) throws RemoteException {
        serviceDispatcher.registerRemoteServiceLocked(serviceCanonicalName, processName, binder);
    }

    @Override
    public synchronized void unregisterRemoteService(String serviceCanonicalName) throws RemoteException {
        serviceDispatcher.unregisterRemoteServiceLocked(serviceCanonicalName);
        //然后让EventDispatcher通知各个进程清除缓存
        eventDispatcher.unregisterRemoteServiceLocked(serviceCanonicalName);
    }

    @Override
    public synchronized void publish(Event event) throws RemoteException {
        eventDispatcher.publishLocked(event);
    }

}

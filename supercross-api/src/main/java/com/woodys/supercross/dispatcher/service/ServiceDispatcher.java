package com.woodys.supercross.dispatcher.service;

import android.os.IBinder;
import android.os.RemoteException;

import com.woodys.supercross.SuperCross;
import com.woodys.supercross.bean.BinderBean;
import com.woodys.supercross.log.Debugger;
import com.woodys.supercross.dispatcher.listener.EmergencyHandler;
import com.woodys.supercross.dispatcher.listener.IEmergencyHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ServiceDispatcher implements IServiceDispatcher {
    private static final String TAG = ServiceDispatcher.class.getSimpleName();

    private IEmergencyHandler emergencyHandler;

    public ServiceDispatcher() {
        emergencyHandler = new EmergencyHandler();
    }

    private Map<String, BinderBean> remoteBinderCache = new ConcurrentHashMap<>();

    @Override
    public BinderBean getTargetBinderLocked(String serviceCanonicalName) throws RemoteException {
        Debugger.d(TAG,"getTargetBinderLocked,serivceName:" + serviceCanonicalName + ",currentPid:" + android.os.Process.myPid() + ",thread:" + Thread.currentThread().getName());
        BinderBean bean = remoteBinderCache.get(serviceCanonicalName);
        if (null == bean) {
            return null;
        } else {
            return bean;
        }
    }

    @Override
    public void registerRemoteServiceLocked(final String serviceCanonicalName, String processName,
                                            IBinder binder) throws RemoteException {
        Debugger.d( TAG,"registerRemoteServiceLocked,serviceCanonicalName:" + serviceCanonicalName + ",currentPid:" + android.os.Process.myPid() + ",thread:" + Thread.currentThread().getName());
        if (binder != null) {
            binder.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    Debugger.d(TAG,"binderDied,serviceCanonicalName:" + serviceCanonicalName);
                    BinderBean bean = remoteBinderCache.remove(serviceCanonicalName);
                    if (bean != null) {
                        emergencyHandler.handleBinderDied(SuperCross.getAppContext(), bean.getProcessName());
                    }
                }
            }, 0);
            remoteBinderCache.put(serviceCanonicalName, new BinderBean(binder, processName));
            Debugger.d(TAG,"registerRemoteServiceLocked(),binder is not null");
        } else {
            Debugger.d( TAG,"registerRemoteServiceLocked(),binder is null");
        }
    }

    @Override
    public void unregisterRemoteServiceLocked(String serviceCanonicalName) {
        Debugger.d(TAG, "unregisterRemoteServiceLocked,serviceCanonicalName:" + serviceCanonicalName + ",currentPid:" + android.os.Process.myPid() + ",thread:" + Thread.currentThread().getName());
        remoteBinderCache.remove(serviceCanonicalName);
    }
}

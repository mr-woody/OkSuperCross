package com.okay.supercross.dispatcher.service;

import android.os.IBinder;
import android.os.RemoteException;

import com.okay.supercross.SuperCross;
import com.okay.supercross.bean.BinderBean;
import com.okay.supercross.log.Debugger;
import com.okay.supercross.service.emergency.EmergencyHandler;
import com.okay.supercross.service.emergency.IEmergencyHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ServiceDispatcher implements IServiceDispatcher {

    private static final String TAG = "SuperCross";

    private IEmergencyHandler emergencyHandler;

    public ServiceDispatcher() {
        emergencyHandler = new EmergencyHandler();
    }

    private Map<String, BinderBean> remoteBinderCache = new ConcurrentHashMap<>();

    @Override
    public BinderBean getTargetBinderLocked(String serviceCanonicalName) throws RemoteException {
        Debugger.d(TAG, "ServiceDispatcher-->getTargetBinderLocked,serivceName:" + serviceCanonicalName + ",pid:" + android.os.Process.myPid() + ",thread:" + Thread.currentThread().getName());
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
        Debugger.d(TAG, "ServiceDispatcher-->registerRemoteServiceLocked,serviceCanonicalName:" + serviceCanonicalName + ",pid:" + android.os.Process.myPid() + ",thread:" + Thread.currentThread().getName());
        if (binder != null) {
            binder.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    Debugger.d("ServiceDispatcher-->binderDied,serviceCanonicalName:" + serviceCanonicalName);
                    BinderBean bean = remoteBinderCache.remove(serviceCanonicalName);
                    if (bean != null) {
                        emergencyHandler.handleBinderDied(SuperCross.getAppContext(), bean.getProcessName());
                    }
                }
            }, 0);
            remoteBinderCache.put(serviceCanonicalName, new BinderBean(binder, processName));
            Debugger.d("ServiceDispatcher-->registerRemoteServiceLocked(),binder is not null");
        } else {
            Debugger.d(TAG, "ServiceDispatcher-->registerRemoteServiceLocked(),binder is null");
        }
    }

    @Override
    public void unregisterRemoteServiceLocked(String serviceCanonicalName) {
        remoteBinderCache.remove(serviceCanonicalName);
    }
}

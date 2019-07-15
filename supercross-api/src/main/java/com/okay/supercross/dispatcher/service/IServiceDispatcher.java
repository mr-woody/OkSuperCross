package com.okay.supercross.dispatcher.service;

import android.os.IBinder;
import android.os.RemoteException;

import com.okay.supercross.bean.BinderBean;


public interface IServiceDispatcher {

    BinderBean getTargetBinderLocked(String serviceCanonicalName) throws RemoteException;

    void registerRemoteServiceLocked(String serviceCanonicalName, String processName, IBinder binder) throws RemoteException;

    void unregisterRemoteServiceLocked(String serviceCanonicalName) throws RemoteException;

}

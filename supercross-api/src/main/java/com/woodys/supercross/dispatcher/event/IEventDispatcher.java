package com.woodys.supercross.dispatcher.event;

import android.os.IBinder;
import android.os.RemoteException;

import com.woodys.supercross.event.Event;


public interface IEventDispatcher {

    void registerRemoteTransferLocked(int pid, IBinder transferBinder);

    void publishLocked(Event event) throws RemoteException;

    void unregisterRemoteServiceLocked(String serviceCanonicalName) throws RemoteException;
}

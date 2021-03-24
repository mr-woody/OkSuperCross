package com.woodys.supercross.dispatcher.event;

import android.os.IBinder;
import android.os.RemoteException;

import com.woodys.supercross.IRemoteTransfer;
import com.woodys.supercross.event.Event;
import com.woodys.supercross.log.Debugger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class EventDispatcher implements IEventDispatcher {
    private static final String TAG = EventDispatcher.class.getSimpleName();
    private Map<Integer, IBinder> transferBinders = new ConcurrentHashMap<>();

    @Override
    public void registerRemoteTransferLocked(final int pid, IBinder transferBinder) {
        Debugger.d(TAG,"registerRemoteTransferLocked,pid:" + pid+ ",currentPid:" + android.os.Process.myPid() + ",thread:" + Thread.currentThread().getName());
        if (transferBinder == null) {
            return;
        }
        try {
            transferBinder.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    transferBinders.remove(pid);
                }
            }, 0);
        } catch (RemoteException ex) {
            Debugger.e(TAG,ex);
        } finally {
            transferBinders.put(pid, transferBinder);
        }

    }

    @Override
    public void publishLocked(Event event) throws RemoteException {
        Debugger.d(TAG,"publishLocked,event.name:" + event.getName()+ ",currentPid:" + android.os.Process.myPid() + ",thread:" + Thread.currentThread().getName());
        for (Map.Entry<Integer, IBinder> entry : transferBinders.entrySet()) {
            IRemoteTransfer transfer = IRemoteTransfer.Stub.asInterface(entry.getValue());
            if (null != transfer) {
                try {
                    transfer.notify(event);
                } catch (RemoteException e) {
                    Debugger.e(TAG,e);
                }
            }
        }
    }

    @Override
    public void unregisterRemoteServiceLocked(String serviceCanonicalName) throws RemoteException {
        Debugger.d(TAG,"unregisterRemoteServiceLocked,serviceCanonicalName:" + serviceCanonicalName+ ",currentPid:" + android.os.Process.myPid() + ",thread:" + Thread.currentThread().getName());
        for (Map.Entry<Integer, IBinder> entry : transferBinders.entrySet()) {
            IRemoteTransfer transfer = IRemoteTransfer.Stub.asInterface(entry.getValue());
            if (null != transfer) {
                try {
                    transfer.unregisterRemoteService(serviceCanonicalName);
                } catch (RemoteException ex) {
                    Debugger.e(TAG,ex);
                }
            }
        }
    }
}

package com.okay.supercross.transfer.event;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.text.TextUtils;

import com.okay.supercross.bean.BinderWrapper;
import com.okay.supercross.IDispatcher;
import com.okay.supercross.IRemoteTransfer;
import com.okay.supercross.config.Constants;
import com.okay.supercross.dispatcher.DispatcherService;
import com.okay.supercross.event.Event;
import com.okay.supercross.event.EventCallback;
import com.okay.supercross.log.Debugger;
import com.okay.supercross.utils.ServiceUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EventTransfer {

    private Map<String, List<WeakReference<EventCallback>>> eventListeners = new HashMap<>();

    public void subscribeEventLocked(String name, EventCallback listener) {
        Debugger.d("RemoteTransfer-->subscribe,name:" + name);
        if (TextUtils.isEmpty(name) || listener == null) {
            return;
        }
        if (null == eventListeners.get(name)) {
            List<WeakReference<EventCallback>> list = new ArrayList<>();
            eventListeners.put(name, list);
        }
        eventListeners.get(name).add(new WeakReference<>(listener));
    }

    public void unsubscribeEventLocked(EventCallback listener) {
        for (Map.Entry<String, List<WeakReference<EventCallback>>> entry : eventListeners.entrySet()) {
            List<WeakReference<EventCallback>> listeners = entry.getValue();
            for (WeakReference<EventCallback> weakRef : listeners) {
                if (listener == weakRef.get()) {
                    listeners.remove(weakRef);
                    break;
                }
            }
        }
    }

    public void unsubscribeEventLocked(String key) {
        List<WeakReference<EventCallback>> listeners = eventListeners.get(key);
        for (WeakReference<EventCallback> weakRef : listeners) {
            listeners.remove(weakRef);
        }
    }


    public void publishLocked(Event event, IDispatcher dispatcherProxy, IRemoteTransfer.Stub stub, Context context) {
        Debugger.d("EventTransfer-->publishLocked,event.name:" + event.getName());
        if (null == dispatcherProxy) {
            BinderWrapper wrapper = new BinderWrapper(stub.asBinder());
            Intent intent = new Intent(context, DispatcherService.class);
            intent.setAction(Constants.DISPATCH_EVENT_ACTION);
            intent.putExtra(Constants.KEY_REMOTE_TRANSFER_WRAPPER, wrapper);
            intent.putExtra(Constants.KEY_EVENT, event);
            intent.putExtra(Constants.KEY_PID, android.os.Process.myPid());
            ServiceUtils.startServiceSafely(context, intent);
        } else {
            try {
                dispatcherProxy.publish(event);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void notifyLocked(Event event) {
        Debugger.d("EventTransfer-->notifyLocked,pid:" + android.os.Process.myPid() + ",event.name:" + event.getName());
        List<WeakReference<EventCallback>> listeners = eventListeners.get(event.getName());
        if (listeners == null) {
            Debugger.d("There is no listeners for " + event.getName() + " in pid " + android.os.Process.myPid());
            return;
        }
        for (int i = listeners.size() - 1; i >= 0; --i) {
            WeakReference<EventCallback> listenerRef = listeners.get(i);
            if (listenerRef.get() == null) {
                listeners.remove(i);
            } else {
                listenerRef.get().onNotify(event.getData());
            }
        }
    }
}

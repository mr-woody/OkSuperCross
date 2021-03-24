package com.woodys.supercross.transfer.event;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.text.TextUtils;

import com.woodys.supercross.IDispatcher;
import com.woodys.supercross.IRemoteTransfer;
import com.woodys.supercross.bean.BinderWrapper;
import com.woodys.supercross.config.Constants;
import com.woodys.supercross.dispatcher.DispatcherService;
import com.woodys.supercross.event.Event;
import com.woodys.supercross.event.EventCallback;
import com.woodys.supercross.log.Debugger;
import com.woodys.supercross.utils.ServiceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EventTransfer {
    private static final String TAG = EventTransfer.class.getSimpleName();

    private Map<String, List<EventCallback>> eventListeners = new HashMap<>();

    public void subscribeEventLocked(String name, EventCallback listener) {
        Debugger.d(TAG,"subscribeEventLocked,name:" + name);
        if (TextUtils.isEmpty(name) || listener == null) {
            return;
        }
        boolean isNullValue = (null == eventListeners.get(name));
        Debugger.d(TAG,"subscribeEventLocked->1,null == eventListeners.get(name):" + isNullValue);
        if (isNullValue) {
            List<EventCallback> list = new ArrayList<>();
            eventListeners.put(name, list);
        }
        eventListeners.get(name).add(listener);
    }

    public void unsubscribeEventLocked(EventCallback listener) {
        for (Map.Entry<String, List<EventCallback>> entry : eventListeners.entrySet()) {
            List<EventCallback> listeners = entry.getValue();
            for (EventCallback eventCallback : listeners) {
                if (listener == eventCallback) {
                    listeners.remove(eventCallback);
                    break;
                }
            }
        }
    }

    public void unsubscribeEventLocked(String key) {
        List<EventCallback> listeners = eventListeners.get(key);
        for (EventCallback eventCallback : listeners) {
            listeners.remove(eventCallback);
        }
    }


    public void publishLocked(Context context,Event event, IDispatcher dispatcherProxy, IRemoteTransfer.Stub stub) {
        Debugger.d(TAG,"publishLocked,event.name:" + event.getName());
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
               Debugger.e(TAG,ex);
            }
        }
    }

    public void notifyLocked(Event event) {
        Debugger.d(TAG,"notifyLocked,currentPid:" + android.os.Process.myPid() + ",event.name:" + event.getName());
        List<EventCallback> listeners = eventListeners.get(event.getName());
        if (listeners == null) {
            Debugger.d(TAG,"There is no listeners for " + event.getName() + " in currentPid " + android.os.Process.myPid());
            return;
        }
        for (int i = listeners.size() - 1; i >= 0; --i) {
            EventCallback eventCallback = listeners.get(i);
            if (eventCallback == null) {
                listeners.remove(i);
            } else {
                eventCallback.onNotify(event.getData());
            }
        }
    }
}

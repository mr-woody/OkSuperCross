package com.okay.supercross.service.remote;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.okay.supercross.bean.ConnectionBean;
import com.okay.supercross.log.Debugger;
import com.okay.supercross.match.StubServiceMatcher;
import com.okay.supercross.utils.ServiceUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionManager {

    private static ConnectionManager instance;

    //key是对方进程的占坑Service名称
    private Map<String, ConnectionBean> connectionCache = new HashMap<>();

    //已经发出bind请求但是尚未收到回调的connection就先放在这里
    private Map<String, ConnectionBean> waitingFlightConnCache = new HashMap<>();

    private ConnectionManager() {
    }

    public static ConnectionManager getInstance() {
        if (null == instance) {
            synchronized (ConnectionManager.class) {
                if (null == instance) {
                    instance = new ConnectionManager();
                }
            }
        }
        return instance;
    }

    private String getCommuStubServiceName(Intent intent) {
        if (intent.getComponent() == null) {
            return null;
        }
        return intent.getComponent().getClassName();
    }

    //这里不能按照serviceCanonicalName来区分，而是要按照target service来划分，如果targetService一样，那就没必要再绑定
    public synchronized String bindAction(Context context, String serverProcessName) {
        Debugger.d("ConnectionManager-->bindAction,serverProcessName:" + serverProcessName);
        Intent intent = StubServiceMatcher.matchIntent(context, serverProcessName);
        if (null == intent) {
            Debugger.d("match intent is null");
            return null;
        }

        final String commuStubServiceName = getCommuStubServiceName(intent);
        ConnectionBean bean = connectionCache.get(commuStubServiceName);
        ConnectionBean waitingBean = waitingFlightConnCache.get(commuStubServiceName);
        if (null == bean && waitingBean == null) {
            Debugger.d("first create ServiceConnectioin for " + commuStubServiceName);
            final ServiceConnection connection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    Debugger.d("onServiceConnected,name:" + commuStubServiceName);
                    ConnectionBean connectionBean = waitingFlightConnCache.remove(commuStubServiceName);
                    if (connectionBean == null) {
                        Debugger.e("No ConnectionBean in waitingFlightCache!");
                    } else {
                        connectionCache.put(commuStubServiceName, connectionBean);
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Debugger.d("onServiceDisconnected,name:" + commuStubServiceName);
                    //如果发生了这种情况，就要从缓存中移除掉!
                    connectionCache.remove(commuStubServiceName);
                    waitingFlightConnCache.remove(commuStubServiceName);
                }
            };
            bean = new ConnectionBean(connection);
            waitingFlightConnCache.put(commuStubServiceName, bean);
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
        } else if (waitingBean != null) {
            waitingBean.increaseRef();
        } else {
            bean.increaseRef();
        }
        return commuStubServiceName;
    }

    public synchronized void unbindAction(Context context, List<String> commuStubServiceNames) {
        Debugger.d("ConnectionManager-->unbindAction");
        boolean waitFlag;
        for (String stubServiceName : commuStubServiceNames) {
            waitFlag = false;
            ConnectionBean bean = connectionCache.get(stubServiceName);
            if (bean == null) {
                bean = waitingFlightConnCache.get(stubServiceName);
                waitFlag = true;
            }
            if (bean == null) {
                return;
            }
            bean.decreaseRef();
            if (bean.getRefCount() < 1) {
                Debugger.d("really unbind " + stubServiceName);
                //此时要将连接从缓存移除!
                if (waitFlag) {
                    //此时仅需要将ServiceConnection从缓存中移除即可，不需要进行unbind操作，因为此时实际上还没bind成功呢
                    waitingFlightConnCache.remove(stubServiceName);
                } else {
                    ServiceUtils.unbindSafely(context, bean.getServiceConnection());
                    connectionCache.remove(stubServiceName);
                }
            }
        }
    }


}

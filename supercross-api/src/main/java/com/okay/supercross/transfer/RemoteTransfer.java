package com.okay.supercross.transfer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;

import com.okay.supercross.bean.BinderWrapper;
import com.okay.supercross.IDispatcher;
import com.okay.supercross.IRemoteTransfer;
import com.okay.supercross.bean.BinderBean;
import com.okay.supercross.config.Constants;
import com.okay.supercross.dispatcher.cursor.DispatcherCursor;
import com.okay.supercross.dispatcher.DispatcherProvider;
import com.okay.supercross.dispatcher.DispatcherService;
import com.okay.supercross.event.Event;
import com.okay.supercross.event.EventCallback;
import com.okay.supercross.log.Debugger;
import com.okay.supercross.transfer.event.EventTransfer;
import com.okay.supercross.transfer.service.RemoteServiceTransfer;
import com.okay.supercross.utils.ServiceUtils;


public class RemoteTransfer extends IRemoteTransfer.Stub {

    public static final int MAX_WAIT_TIME = 600;

    private static RemoteTransfer sInstance;

    private Context context;

    private IDispatcher dispatcherProxy;

    private RemoteServiceTransfer serviceTransfer;
    private EventTransfer eventTransfer;

    private RemoteTransfer() {
        serviceTransfer = new RemoteServiceTransfer();
        eventTransfer = new EventTransfer();
    }

    public static RemoteTransfer getInstance() {
        if (null == sInstance) {
            synchronized (RemoteTransfer.class) {
                if (null == sInstance) {
                    sInstance = new RemoteTransfer();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化
     * @param context
     */
    public void init(Context context) {
        this.context = context;
        sendRegisterInfo();
    }


    /**
     * 让ServiceDispatcher反向注册到当前进程
     */
    private synchronized void sendRegisterInfo() {
        if (dispatcherProxy == null) {
            //后面考虑还是采用"has-a"的方式会更好
            BinderWrapper wrapper = new BinderWrapper(this.asBinder());
            Intent intent = new Intent(context, DispatcherService.class);
            intent.setAction(Constants.DISPATCH_REGISTER_SERVICE_ACTION);
            intent.putExtra(Constants.KEY_REMOTE_TRANSFER_WRAPPER, wrapper);
            intent.putExtra(Constants.KEY_PID, android.os.Process.myPid());
            ServiceUtils.startServiceSafely(context, intent);
        }
    }

    private void initDispatchProxyLocked() {
        if (null == dispatcherProxy) {
            IBinder dispatcherBinder = getIBinderFromProvider();
            if (null != dispatcherBinder) {
                Debugger.d("the binder from provider is not null");
                dispatcherProxy = IDispatcher.Stub.asInterface(dispatcherBinder);
                registerCurrentTransfer();
            }
        }
        if (null == dispatcherProxy) {
            sendRegisterInfo();
            try {
                wait(MAX_WAIT_TIME);
            } catch (InterruptedException ex) {
                Debugger.e("Attention! Wait out of time!");
                ex.printStackTrace();
            }
        }
    }

    public synchronized BinderBean getRemoteServiceBean(String serviceCanonicalName) {
        Debugger.d("RemoteTransfer-->getRemoteServiceBean,pid=" + android.os.Process.myPid() + ",thread:" + Thread.currentThread().getName());
        BinderBean cacheBinderBean = serviceTransfer.getIBinderFromCache(context, serviceCanonicalName);
        if (cacheBinderBean != null) {
            return cacheBinderBean;
        }
        initDispatchProxyLocked();
        if (serviceTransfer == null || dispatcherProxy == null) {
            return null;
        }
        return serviceTransfer.getAndSaveIBinder(serviceCanonicalName, dispatcherProxy);
    }

    private void registerCurrentTransfer() {
        try {
            dispatcherProxy.registerRemoteTransfer(android.os.Process.myPid(), this.asBinder());
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    private Uri getDispatcherProviderUri() {
        return Uri.parse("content://" + context.getPackageName() + "." + DispatcherProvider.URI_SUFFIX + "/main");
    }

    private IBinder getIBinderFromProvider() {
        Debugger.d("RemoteTransfer-->getIBinderFromProvider()");
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(getDispatcherProviderUri(), DispatcherProvider.PROJECTION_MAIN,
                    null, null, null);
            if (cursor == null) {
                return null;
            }
            return DispatcherCursor.stripBinder(cursor);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public synchronized void registerStubService(String serviceCanonicalName, IBinder stubBinder) {
        initDispatchProxyLocked();
        serviceTransfer.registerStubServiceLocked(serviceCanonicalName, stubBinder, context, dispatcherProxy, this);
    }

    /**
     * 要注销本进程的某个服务,注意它与unregisterRemoteService()的区别!
     * 这个方法表示要注销本进程的某个服务
     *
     * @param serviceCanonicalName
     */
    public synchronized void unregisterStubService(String serviceCanonicalName) {
        initDispatchProxyLocked();
        serviceTransfer.unregisterStubServiceLocked(serviceCanonicalName, context, dispatcherProxy);
    }


    public synchronized void subscribeEvent(String name, EventCallback listener) {
        eventTransfer.subscribeEventLocked(name, listener);
    }


    public synchronized void unsubscribeEvent(EventCallback listener) {
        eventTransfer.unsubscribeEventLocked(listener);
    }


    public void unsubscribeEvent(String key) {
        eventTransfer.unsubscribeEventLocked(key);
    }


    public synchronized void publish(Event event) {
        initDispatchProxyLocked();
        eventTransfer.publishLocked(event, dispatcherProxy, this, context);
    }


    public synchronized void registerDispatcher(IBinder dispatcherBinder) throws RemoteException {
        Debugger.d("RemoteTransfer-->registerDispatcher");
        //一般从发出注册信息到这里回调就6ms左右，所以绝大部分时候走的都是这个逻辑。
        dispatcherBinder.linkToDeath(new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                Debugger.d("RemoteTransfer-->dispatcherBinder binderDied");
                resetDispatcherProxy();
            }
        }, 0);
        dispatcherProxy = IDispatcher.Stub.asInterface(dispatcherBinder);
        notifyAll();
    }

    private synchronized void resetDispatcherProxy() {
        dispatcherProxy = null;
    }

    /**
     * 接收到来自Dispatcher的通知，如果本地有相应的IBinder,就要清除
     *
     * @param serviceCanonicalName
     * @throws RemoteException
     */
    public synchronized void unregisterRemoteService(String serviceCanonicalName) throws RemoteException {
        Debugger.d("RemoteTransfer-->unregisterRemoteServiceLocked,pid:" + android.os.Process.myPid() + ",serviceName:" + serviceCanonicalName);
        serviceTransfer.clearRemoteBinderCacheLocked(serviceCanonicalName);
    }

    public synchronized void notify(Event event) throws RemoteException {
        eventTransfer.notifyLocked(event);
    }

}

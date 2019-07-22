package com.okay.supercross.transfer.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.okay.supercross.IDispatcher;
import com.okay.supercross.IRemoteTransfer;
import com.okay.supercross.bean.BinderBean;
import com.okay.supercross.bean.BinderWrapper;
import com.okay.supercross.config.Constants;
import com.okay.supercross.dispatcher.DispatcherService;
import com.okay.supercross.log.Debugger;
import com.okay.supercross.utils.ProcessUtils;
import com.okay.supercross.utils.ServiceUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class RemoteServiceTransfer {

    /**
     * 本地的Binder,需要给其他进程使用的,key为inteface的完整名称
     */
    private Map<String, IBinder> stubBinderCache = new ConcurrentHashMap<>();

    private Map<String, BinderBean> remoteBinderCache = new ConcurrentHashMap<>();

    public void registerStubServiceLocked(String serviceCanonicalName, IBinder stubBinder,
                                          Context context, IDispatcher dispatcherProxy, IRemoteTransfer.Stub stub) {
        stubBinderCache.put(serviceCanonicalName, stubBinder);
        if (dispatcherProxy == null) {
            BinderWrapper wrapper = new BinderWrapper(stub.asBinder());
            Intent intent = new Intent(context, DispatcherService.class);
            intent.setAction(Constants.DISPATCH_REGISTER_SERVICE_ACTION);
            intent.putExtra(Constants.KEY_REMOTE_TRANSFER_WRAPPER, wrapper);
            intent.putExtra(Constants.KEY_BUSINESS_BINDER_WRAPPER, new BinderWrapper(stubBinder));
            intent.putExtra(Constants.KEY_SERVICE_NAME, serviceCanonicalName);
            setProcessInfo(intent, context);
            ServiceUtils.startServiceSafely(context, intent);
        } else {
            try {
                dispatcherProxy.registerRemoteService(serviceCanonicalName,
                        ProcessUtils.getProcessName(context), stubBinder);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void setProcessInfo(Intent intent, Context context) {
        intent.putExtra(Constants.KEY_PID, android.os.Process.myPid());
        intent.putExtra(Constants.KEY_PROCESS_NAME, ProcessUtils.getProcessName(context));
    }

    /**
     * 思考:其实是不是不用这么麻烦，直接利用事件通知机制进行通知就可以了吧？
     * 可以是可以，但是逻辑上就不那么清晰了，而且要写很多的if语句，可读性和可维护性也差了。
     *
     * @param serviceCanonicalName
     * @param context
     * @param dispatcherProxy
     */
    public void unregisterStubServiceLocked(String serviceCanonicalName, Context context, IDispatcher dispatcherProxy) {
        //第一步，清除本地的缓存
        clearStubBinderCache(serviceCanonicalName);
        //第二步，通知Dispatcher,然后让Dispatcher通知各进程
        if (null == dispatcherProxy) {
            Intent intent = new Intent(context, DispatcherService.class);
            intent.setAction(Constants.DISPATCH_UNREGISTER_SERVICE_ACTION);
            intent.putExtra(Constants.KEY_SERVICE_NAME, serviceCanonicalName);
            ServiceUtils.startServiceSafely(context, intent);
        } else {
            try {
                dispatcherProxy.unregisterRemoteService(serviceCanonicalName);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

    public BinderBean getIBinderFromCache(Context context, String serviceCanonicalName) {
        //如果是自己进程或者主进程，就不要进行bind操作了
        if (stubBinderCache.get(serviceCanonicalName) != null) {
            return new BinderBean(stubBinderCache.get(serviceCanonicalName),
                    ProcessUtils.getProcessName(context));
        }

        if (remoteBinderCache.get(serviceCanonicalName) != null) {
            return remoteBinderCache.get(serviceCanonicalName);
        }
        return null;
    }

    public BinderBean getAndSaveIBinder(final String serviceName, IDispatcher dispatcherProxy) {
        try {
            BinderBean binderBean = dispatcherProxy.getTargetBinder(serviceName);
            if (null == binderBean) {
                return null;
            }
            try {
                binderBean.getBinder().linkToDeath(new IBinder.DeathRecipient() {
                    @Override
                    public void binderDied() {
                        remoteBinderCache.remove(serviceName);
                    }
                }, 0);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
            Debugger.d("get IBinder from ServiceDispatcher");
            remoteBinderCache.put(serviceName, binderBean);
            return binderBean;
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 清除本地的IBinder缓存
     *
     * @param serviceName
     */
    public void clearStubBinderCache(String serviceName) {
        stubBinderCache.remove(serviceName);
    }

    public void clearRemoteBinderCacheLocked(String serviceName) {
        remoteBinderCache.remove(serviceName);
    }

}

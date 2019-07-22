package com.okay.supercross.dispatcher;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.okay.supercross.IRemoteTransfer;
import com.okay.supercross.bean.BinderWrapper;
import com.okay.supercross.config.Constants;
import com.okay.supercross.event.Event;
import com.okay.supercross.log.Debugger;
import com.okay.supercross.utils.ProcessUtils;

public class DispatcherService extends Service {
    public DispatcherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not supported implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Debugger.d("DispatcherService-->onCreate(),currentProcess:" + ProcessUtils.getProcessName(android.os.Process.myPid()));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        Debugger.d("DispatcherService-->onStartCommand,action:" + intent.getAction());
        if (Constants.DISPATCH_REGISTER_SERVICE_ACTION.equals(intent.getAction())) {
            registerRemoteService(intent);
        } else if (Constants.DISPATCH_UNREGISTER_SERVICE_ACTION.equals(intent.getAction())) {
            unregisterRemoteService(intent);
        } else if (Constants.DISPATCH_EVENT_ACTION.equals(intent.getAction())) {
            publishEvent(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void publishEvent(Intent intent) {
        BinderWrapper remoteTransferWrapper = intent.getParcelableExtra(Constants.KEY_REMOTE_TRANSFER_WRAPPER);
        int pid = intent.getIntExtra(Constants.KEY_PID, -1);
        IBinder remoteTransferBinder = remoteTransferWrapper.getBinder();
        registerAndReverseRegister(pid, remoteTransferBinder);
        Event event = intent.getParcelableExtra(Constants.KEY_EVENT);
        try {
            Dispatcher.getInstance().publish(event);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 注册和反向注册
     *
     * @param pid
     * @param transterBinder
     */
    private void registerAndReverseRegister(int pid, IBinder transterBinder) {
        Debugger.d("DispatcherService-->registerAndReverseRegister,pid=" + pid + ",processName:" + ProcessUtils.getProcessName(pid));
        IRemoteTransfer remoteTransfer = IRemoteTransfer.Stub.asInterface(transterBinder);

        Dispatcher.getInstance().registerRemoteTransfer(pid, transterBinder);

        if (remoteTransfer != null) {
            Debugger.d("now register to RemoteTransfer");
            try {
                remoteTransfer.registerDispatcher(Dispatcher.getInstance().asBinder());
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        } else {
            Debugger.d("IdspatcherRegister IBinder is null");
        }
    }

    private void registerRemoteService(Intent intent) {
        String serviceCanonicalName = intent.getStringExtra(Constants.KEY_SERVICE_NAME);
        int pid = intent.getIntExtra(Constants.KEY_PID, -1);
        //注册registerRemoteService时，也就是RemoteServiceTransfer的registerStubServiceLocked方法，才会传递这个key
        BinderWrapper wrapper = intent.getParcelableExtra(Constants.KEY_REMOTE_TRANSFER_WRAPPER);
        try {
            //说明是RemoteTransfer.sendRegisterInfo()，传递过来的数据
            if (TextUtils.isEmpty(serviceCanonicalName)) {
                Debugger.e("service canonical name is null");
            } else {
                BinderWrapper businessWrapper = intent.getParcelableExtra(Constants.KEY_BUSINESS_BINDER_WRAPPER);
                String processName = intent.getStringExtra(Constants.KEY_PROCESS_NAME);
                Dispatcher.getInstance().registerRemoteService(serviceCanonicalName,
                        processName, businessWrapper.getBinder());
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } finally {
            if (wrapper != null) {
                registerAndReverseRegister(pid, wrapper.getBinder());
            }
        }

    }

    private void unregisterRemoteService(Intent intent) {
        String serviceCanonicalName = intent.getStringExtra(Constants.KEY_SERVICE_NAME);
        try {
            Dispatcher.getInstance().unregisterRemoteService(serviceCanonicalName);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
}

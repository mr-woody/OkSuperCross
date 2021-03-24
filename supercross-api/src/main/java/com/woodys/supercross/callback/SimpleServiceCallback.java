package com.woodys.supercross.callback;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;

import com.woodys.supercross.ServiceCallback;

/**
 * 考虑到大部分情况下回调在主线程，作用做了处理，调用方不用切换线程，该回调返回在主线程中
 */
public abstract class SimpleServiceCallback extends ServiceCallback.Stub {

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public final void onSuccess(final Bundle result) throws RemoteException {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onSucceed(result);
            }
        });
    }

    @Override
    public final void onFail(final String reason) throws RemoteException {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onFailed(reason);
            }
        });
    }

    public abstract void onSucceed(Bundle result);

    public abstract void onFailed(String reason);
}

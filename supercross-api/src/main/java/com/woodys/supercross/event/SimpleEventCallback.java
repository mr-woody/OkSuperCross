package com.woodys.supercross.event;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

/**
 * 考虑到大部分情况下回调在主线程，作用做了处理，调用方不用切换线程，该回调返回在主线程中
 */
public abstract class SimpleEventCallback implements EventCallback {

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public final void onNotify(final Bundle result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onEvent(result);
            }
        });
    }

    public abstract void onEvent(Bundle result);
}

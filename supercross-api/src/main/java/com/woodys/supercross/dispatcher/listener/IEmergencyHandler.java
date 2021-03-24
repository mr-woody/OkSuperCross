package com.woodys.supercross.dispatcher.listener;

import android.content.Context;

/**
 * 当IBinder.DeathRecipient调用binderDied方法时，触发的接口
 */
public interface IEmergencyHandler {
    void handleBinderDied(Context context, String serverProcessName);
}

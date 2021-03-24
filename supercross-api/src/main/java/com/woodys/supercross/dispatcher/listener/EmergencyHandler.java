package com.woodys.supercross.dispatcher.listener;

import android.content.Context;

/**
 * binderDied方法调用后，执行的实现
 */
public class EmergencyHandler implements IEmergencyHandler {

    @Override
    public void handleBinderDied(Context context, String serverProcessName) {
        // TODO: 2019/6/30 暂不处理，后续考虑重启进程等操作或者统计使用
    }
}

package com.okay.supercross.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.okay.supercross.ICommuStub;
import com.okay.supercross.config.Constants;

/**
 * 占位服务，目的只是为了提升进程的优先级
 */
public class ConnectMultiCrossService extends Service {
    private static final String TAG = "SuperCross";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final ICommuStub.Stub mBinder = new ICommuStub.Stub() {
        @Override
        public void commu(Bundle args) throws RemoteException {
            //do nothing now
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand,pid:" + android.os.Process.myPid() + ",action:" + intent.getAction() + ",serviceName:" + intent.getStringExtra(Constants.KEY_SERVICE_NAME));
        //这样可以使Service所在进程的保活效果好一点
        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    //默认最多匹配21个进程，如果不够请自行添加
    public static class ConnectMultiCrossService0 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService1 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService2 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService3 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService4 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService5 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService6 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService7 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService8 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService9 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService10 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService11 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService12 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService13 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService14 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService15 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService16 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService17 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService18 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService19 extends ConnectMultiCrossService {

    }

    public static class ConnectMultiCrossService20 extends ConnectMultiCrossService {

    }
}

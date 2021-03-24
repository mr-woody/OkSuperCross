package com.plug.plug1.application;


import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;

import com.woodys.supercross.ServiceCallback;
import com.plug.common.plugservice.RemoteService;

public class PlugRemoteService implements RemoteService {

    @Override
    public String login(String username, String password) {
        System.out.print("username=>" + username);
        System.out.print("password=>" + password);
        return "token:" + "username=>" + username +"password=>" + password;

    }

    @Override
    public void login2(final String username, final String password, final ServiceCallback callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("password", password);
                try {
                    callBack.onSuccess(bundle);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

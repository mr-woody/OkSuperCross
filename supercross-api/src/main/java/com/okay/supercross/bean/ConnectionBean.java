package com.okay.supercross.bean;

import android.content.ServiceConnection;


public class ConnectionBean {

    private ServiceConnection serviceConnection;

    private int refCount;

    public ConnectionBean(ServiceConnection connection) {
        this.serviceConnection = connection;
        this.refCount = 1;
    }

    public void increaseRef() {
        ++refCount;
    }

    public void decreaseRef() {
        --refCount;
    }

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public int getRefCount() {
        return refCount;
    }
}

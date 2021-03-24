package com.woodys.supercross.bean;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * IBinder 和 processName 的载体本地序列号（Parcelable）类
 */
public class BinderBean implements Parcelable {

    private IBinder binder;
    private String processName;

    public BinderBean() {

    }

    public BinderBean(IBinder binder, String processName) {
        this.binder = binder;
        this.processName = processName;
    }

    public BinderBean(Parcel in) {
        this.binder = in.readStrongBinder();
        this.processName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(binder);
        dest.writeString(processName);
    }

    public static final Parcelable.Creator<BinderBean> CREATOR = new Creator<BinderBean>() {
        @Override
        public BinderBean createFromParcel(Parcel source) {
            return new BinderBean(source);
        }

        @Override
        public BinderBean[] newArray(int size) {
            return new BinderBean[size];
        }
    };

    public IBinder getBinder() {
        return binder;
    }

    public String getProcessName() {
        return processName;
    }
}

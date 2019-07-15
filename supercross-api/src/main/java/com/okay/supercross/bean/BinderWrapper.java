package com.okay.supercross.bean;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 跨进程IBinder传输封装本地序列化实体类
 */
public class BinderWrapper implements Parcelable {

    private final IBinder binder;

    public BinderWrapper(IBinder binder) {
        this.binder = binder;
    }

    public BinderWrapper(Parcel in) {
        this.binder = in.readStrongBinder();
    }

    public IBinder getBinder() {
        return binder;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(binder);
    }

    public static final Creator<BinderWrapper> CREATOR = new Creator<BinderWrapper>() {
        @Override
        public BinderWrapper createFromParcel(Parcel source) {
            return new BinderWrapper(source);
        }

        @Override
        public BinderWrapper[] newArray(int size) {
            return new BinderWrapper[size];
        }
    };
}

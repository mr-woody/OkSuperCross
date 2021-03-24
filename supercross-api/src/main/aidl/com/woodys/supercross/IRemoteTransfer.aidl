// IServiceRegister.aidl
package com.woodys.supercross;
import com.woodys.supercross.event.Event;

//由于主要是起一个转接的作用，所以取名Transfer
interface IRemoteTransfer {
    //这个要加上oneway关键字比较好,反正我们又不需要返回值
    oneway void registerDispatcher(IBinder dispatcherBinder);
    //从Dispatcher发往各个进程，如果进程中有相应IBinder的缓存，则要马上清除
    oneway void unregisterRemoteService(String serviceCanonicalName);

    oneway void notify(in Event event);
}

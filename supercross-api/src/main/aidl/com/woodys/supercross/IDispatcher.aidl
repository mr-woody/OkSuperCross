// IRemoteService.aidl
package com.woodys.supercross;
import com.woodys.supercross.event.Event;
import com.woodys.supercross.bean.BinderBean;

interface IDispatcher {

   BinderBean getTargetBinder(String serviceCanonicalName);
   //IBinder getTargetBinderLocked(String serviceCanonicalName);
   //这个uri其实就是Target Service的action
   IBinder fetchTargetBinder(String uri);

   void registerRemoteTransfer(int pid,IBinder remoteTransferBinder);

   void registerRemoteService(String serviceCanonicalName,String processName,IBinder binder);

   void unregisterRemoteService(String serviceCanonicalName);

   void publish(in Event event);

}

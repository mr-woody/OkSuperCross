// IRemoteService.aidl
package com.okay.supercross;
import com.okay.supercross.event.Event;
import com.okay.supercross.bean.BinderBean;

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

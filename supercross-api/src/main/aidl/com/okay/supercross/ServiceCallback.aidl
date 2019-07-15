// Callback.aidl
package com.okay.supercross;
import android.os.Bundle;

/**
* 对于耗时操作，就需要一个 ServiceCallback, 在server端处理耗时操作之后再回调。
* 回调是在Binder线程中
*/
interface ServiceCallback {
   void onSuccess(in Bundle result);
   void onFail(String reason);
}

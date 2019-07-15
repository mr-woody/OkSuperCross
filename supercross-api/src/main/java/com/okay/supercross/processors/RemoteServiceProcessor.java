package com.okay.supercross.processors;

import com.okay.supercross.ServiceCallback;

import java.lang.reflect.Method;

/**
 * RemoteService的远程方法AOP处理器
 */
public abstract class RemoteServiceProcessor {
    /**
     * AOP处理器
     * @param method 对应RemoteService的远程方法名
     * @param callbackProcessor 对应RemoteService的callbackProcessor回调处理器
     * @param params 对应RemoteService的远程方法参数
     * @return 是否执行RemoteService的远程方法
     */
    public abstract boolean process(Method method, ServiceCallback callbackProcessor, Object... params);
}

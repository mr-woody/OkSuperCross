package com.okay.supercross.utils;


import com.okay.supercross.annotation.Processor;
import com.okay.supercross.processors.RemoteServiceProcessor;
import com.okay.supercross.ServiceCallback;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class InvokeMethodUtils {
    private static InvokeMethodUtils invokeMethodUtils;
    private Map<String, Object> objectMap;

    private InvokeMethodUtils() {
        objectMap = new HashMap<>();
    }

    public static InvokeMethodUtils getInstance() {
        if (invokeMethodUtils == null) {
            invokeMethodUtils = new InvokeMethodUtils();
        }
        return invokeMethodUtils;
    }

    public void invokeMethod(Class cls, String method, Object[] params, ServiceCallback callbackProcessor) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        int paramsLength = params.length;
        switch (paramsLength) {
            case 0:
                invokeMethodParams0(cls, method, params, callbackProcessor);
                break;
            case 1:
                invokeMethodParams1(cls, method, params, callbackProcessor);
                break;
            case 2:
                invokeMethodParams2(cls, method, params, callbackProcessor);
                break;
            case 3:
                invokeMethodParams3(cls, method, params, callbackProcessor);
                break;
            case 4:
                invokeMethodParams4(cls, method, params, callbackProcessor);
                break;
            case 5:
                invokeMethodParams5(cls, method, params, callbackProcessor);
                break;
            case 6:
                invokeMethodParams6(cls, method, params, callbackProcessor);
                break;
            case 7:
                invokeMethodParams7(cls, method, params, callbackProcessor);
                break;
            case 8:
                invokeMethodParams8(cls, method, params, callbackProcessor);
                break;
            case 9:
                invokeMethodParams9(cls, method, params, callbackProcessor);
                break;
            case 10:
                invokeMethodParams10(cls, method, params, callbackProcessor);
                break;
        }
    }

    private Object getObject(Class cls) throws IllegalAccessException, InstantiationException {
        Object object = objectMap.get(cls.getName());
        if (object == null) {
            object = cls.newInstance();
            objectMap.put(cls.getName(), object);
        }
        return object;
    }

    private boolean injectRemoteServiceProcessors(Method remoteMethod, ServiceCallback callbackProcessor, Object[] params) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Processor processor = remoteMethod.getAnnotation(Processor.class);
        if (processor != null) {
            Class<? extends RemoteServiceProcessor>[] classes = processor.value();
            for (Class<? extends RemoteServiceProcessor> cls : classes) {
                Method method = cls.getMethod("process", Method.class, ServiceCallback.class, Object[].class);
                Boolean continued = (Boolean) method.invoke(getObject(cls), remoteMethod, callbackProcessor, params);
                if (!continued) {
                    return false;
                }
            }
        }
        return true;
    }

    private void invokeMethodParams0(Class cls, String method, Object[] params, ServiceCallback callbackProcessor) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Method remoteMethod = cls.getMethod(method, callbackProcessor.getClass());
        if (injectRemoteServiceProcessors(remoteMethod, callbackProcessor, params)) {
            remoteMethod.invoke(getObject(cls), callbackProcessor);
        }
    }

    private void invokeMethodParams1(Class cls, String method, Object[] params, ServiceCallback callbackProcessor) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Method remoteMethod = cls.getMethod(method, callbackProcessor.getClass(), params[0].getClass());
        if (injectRemoteServiceProcessors(remoteMethod, callbackProcessor, params)) {
            remoteMethod.invoke(getObject(cls), callbackProcessor, params[0]);
        }
    }

    private void invokeMethodParams2(Class cls, String method, Object[] params, ServiceCallback callbackProcessor) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Method remoteMethod = cls.getMethod(method, callbackProcessor.getClass(), params[0].getClass(), params[1].getClass());
        if (injectRemoteServiceProcessors(remoteMethod, callbackProcessor, params)) {
            remoteMethod.invoke(getObject(cls), callbackProcessor, params[0], params[1]);
        }
    }

    private void invokeMethodParams3(Class cls, String method, Object[] params, ServiceCallback callbackProcessor) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Method remoteMethod = cls.getMethod(method, params[0].getClass(), params[1].getClass(), params[2].getClass(), callbackProcessor.getClass());
        if (injectRemoteServiceProcessors(remoteMethod, callbackProcessor, params)) {
            remoteMethod.invoke(getObject(cls), callbackProcessor, params[0], params[1], params[2]);
        }
    }

    private void invokeMethodParams4(Class cls, String method, Object[] params, ServiceCallback callbackProcessor) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Method remoteMethod = cls.getMethod(method, callbackProcessor.getClass(), params[0].getClass(), params[1].getClass(), params[2].getClass(), params[3].getClass());
        if (injectRemoteServiceProcessors(remoteMethod, callbackProcessor, params)) {
            remoteMethod.invoke(getObject(cls), callbackProcessor, params[0], params[1], params[2], params[3]);
        }
    }

    private void invokeMethodParams5(Class cls, String method, Object[] params, ServiceCallback callbackProcessor) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Method remoteMethod = cls.getMethod(method, callbackProcessor.getClass(), params[0].getClass(), params[1].getClass(), params[2].getClass(), params[3].getClass(), params[4].getClass());
        if (injectRemoteServiceProcessors(remoteMethod, callbackProcessor, params)) {
            remoteMethod.invoke(getObject(cls), callbackProcessor, params[0], params[1], params[2], params[3], params[4]);
        }
    }

    private void invokeMethodParams6(Class cls, String method, Object[] params, ServiceCallback callbackProcessor) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Method remoteMethod = cls.getMethod(method, callbackProcessor.getClass(), params[0].getClass(), params[1].getClass(), params[2].getClass(), params[3].getClass(), params[4].getClass(), params[5].getClass());
        if (injectRemoteServiceProcessors(remoteMethod, callbackProcessor, params)) {
            remoteMethod.invoke(getObject(cls), callbackProcessor, params[0], params[1], params[2], params[3], params[4], params[5]);
        }
    }

    private void invokeMethodParams7(Class cls, String method, Object[] params, ServiceCallback callbackProcessor) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Method remoteMethod = cls.getMethod(method, callbackProcessor.getClass(), params[0].getClass(), params[1].getClass(), params[2].getClass(), params[3].getClass(), params[4].getClass(), params[5].getClass(), params[6].getClass());
        if (injectRemoteServiceProcessors(remoteMethod, callbackProcessor, params)) {
            remoteMethod.invoke(getObject(cls), callbackProcessor, params[0], params[1], params[2], params[3], params[4], params[5], params[6]);
        }
    }

    private void invokeMethodParams8(Class cls, String method, Object[] params, ServiceCallback callbackProcessor) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Method remoteMethod = cls.getMethod(method, callbackProcessor.getClass(), params[0].getClass(), params[1].getClass(), params[2].getClass(), params[3].getClass(), params[4].getClass(), params[5].getClass(), params[6].getClass(), params[7].getClass());
        if (injectRemoteServiceProcessors(remoteMethod, callbackProcessor, params)) {
            remoteMethod.invoke(getObject(cls), callbackProcessor, params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7]);
        }
    }

    private void invokeMethodParams9(Class cls, String method, Object[] params, ServiceCallback callbackProcessor) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Method remoteMethod = cls.getMethod(method, callbackProcessor.getClass(), params[0].getClass(), params[1].getClass(), params[2].getClass(), params[3].getClass(), params[4].getClass(), params[5].getClass(), params[6].getClass(), params[7].getClass(), params[8].getClass());
        if (injectRemoteServiceProcessors(remoteMethod, callbackProcessor, params)) {
            remoteMethod.invoke(getObject(cls), callbackProcessor, params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8]);
        }
    }

    private void invokeMethodParams10(Class cls, String method, Object[] params, ServiceCallback callbackProcessor) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Method remoteMethod = cls.getMethod(method, callbackProcessor.getClass(), params[0].getClass(), params[1].getClass(), params[2].getClass(), params[3].getClass(), params[4].getClass(), params[5].getClass(), params[6].getClass(), params[7].getClass(), params[8].getClass(), params[9].getClass());
        if (injectRemoteServiceProcessors(remoteMethod, callbackProcessor, params)) {
            remoteMethod.invoke(getObject(cls), callbackProcessor, params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9]);
        }
    }
}

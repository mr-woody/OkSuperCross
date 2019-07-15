package com.plug.plug1.processors;

import com.okay.supercross.ServiceCallback;
import com.okay.supercross.processors.RemoteServiceProcessor;

import java.lang.reflect.Method;

public class LoginProcessor extends RemoteServiceProcessor {
    @Override
    public boolean process(Method method, ServiceCallback callbackProcessor, Object... params) {
        return true;
    }
}

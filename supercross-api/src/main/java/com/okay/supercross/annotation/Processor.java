package com.okay.supercross.annotation;

import com.okay.supercross.processors.RemoteServiceProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于面向RemoteService的远程方法的AOP编程
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Processor {
    Class<? extends RemoteServiceProcessor>[] value();
}

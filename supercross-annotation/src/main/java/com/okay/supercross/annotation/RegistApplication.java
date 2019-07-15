package com.okay.supercross.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标注Application共享
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface RegistApplication {
}

package com.altas.core.annotation.restful;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface SessionAttr {
    String value() default "";
    boolean required() default false;
}

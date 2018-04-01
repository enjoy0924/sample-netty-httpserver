package com.altas.core.annotation.restful;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Ref {
    String value() default "";
    boolean required() default false;
}

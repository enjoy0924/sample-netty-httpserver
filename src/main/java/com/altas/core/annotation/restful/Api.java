package com.altas.core.annotation.restful;

import java.lang.annotation.*;


@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Api {
    String tag() default "";
    String description() default "";
}

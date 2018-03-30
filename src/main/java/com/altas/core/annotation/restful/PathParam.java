package com.altas.core.annotation.restful;

import java.lang.annotation.*;

/**
 * Created by G_dragon on 2017/7/4.
 */
@Target(ElementType.PARAMETER)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface PathParam {
    String value() default "";
}

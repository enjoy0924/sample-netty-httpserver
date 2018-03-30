package com.altas.core.annotation.restful;

import java.lang.annotation.*;

/**
 * Created by G_dragon on 2017/7/10.
 */
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
    String value() default "alr:um:authz";
}

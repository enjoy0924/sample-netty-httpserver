package com.alr.core.annotation.restful;

import java.lang.annotation.*;
import java.util.List;

/**
 * Created by G_dragon on 2017/7/10.
 */
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
    String value() default "alr:um:authz";
}

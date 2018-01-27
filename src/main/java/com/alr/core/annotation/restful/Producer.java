package com.alr.core.annotation.restful;

import com.alr.core.annotation.restful.enumeration.MimeType;

import java.lang.annotation.*;

/**
 * Created by G_dragon on 2017/7/4.
 *
 */

@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Producer {
    MimeType type() default MimeType.JSON;
}

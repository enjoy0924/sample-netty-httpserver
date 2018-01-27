package com.alr.core.annotation.restful;

import com.alr.core.annotation.restful.enumeration.HttpMethod;
import com.alr.core.annotation.restful.enumeration.MimeType;

import java.lang.annotation.*;

/**
 * Created by G_dragon on 2017/7/4.
 *
 */

@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Consumer {
    HttpMethod method() default HttpMethod.GET;
    MimeType type() default MimeType.ANY;
}

package com.altas.core.annotation.restful;

import com.altas.core.annotation.restful.enumeration.HttpMethod;
import com.altas.core.annotation.restful.enumeration.MimeType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Consumer {
    HttpMethod method() default HttpMethod.GET;
    MimeType type() default MimeType.ANY;
}

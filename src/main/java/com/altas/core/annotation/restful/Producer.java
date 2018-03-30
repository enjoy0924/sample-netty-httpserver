package com.altas.core.annotation.restful;

import com.altas.core.annotation.restful.enumeration.MimeType;

import java.lang.annotation.*;


@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Producer {
    MimeType type() default MimeType.JSON;
}

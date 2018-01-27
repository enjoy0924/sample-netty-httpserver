package com.alr.gateway.exception;

/**
 * Created by zhangy on 2017/7/12.
 */
public class UrlRepeatException extends Exception{
    public UrlRepeatException(String message) {
        super(message);
    }
}
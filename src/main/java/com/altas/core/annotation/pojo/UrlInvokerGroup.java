package com.altas.core.annotation.pojo;

import com.altas.gateway.exception.UnsupportedMethodException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UrlInvokerGroup {

    private Map<String, UrlInvoker> method2UrlInvokerMap = new ConcurrentHashMap<>();

    public boolean addAnnotationInvoker(String httpMethod, UrlInvoker urlInvoker) {
        if (method2UrlInvokerMap.containsKey(httpMethod)) {
            return false;
        } else {
            method2UrlInvokerMap.put(httpMethod, urlInvoker);
            return true;
        }
    }

    public UrlInvoker getAnnotationInvokerByHttpMethod(String httpMethod) throws UnsupportedMethodException {
        if (!method2UrlInvokerMap.containsKey(httpMethod)) {
            throw new UnsupportedMethodException("httpMethod unsupported!");
        }
        return method2UrlInvokerMap.get(httpMethod);
    }
}

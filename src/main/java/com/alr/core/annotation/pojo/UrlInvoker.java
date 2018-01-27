package com.alr.core.annotation.pojo;


import java.util.Map;

/**
 * Created by zhangy on 2017/7/6.
 */
public class UrlInvoker extends AnnotationInvoker {

    private String baseUrl;
    private String url;
    private String regexUrl;
    private Map<String, Integer> pathParams;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getRegexUrl() {
        return regexUrl;
    }

    public void setRegexUrl(String regexUrl) {
        this.regexUrl = regexUrl;
    }

    public Map<String, Integer> getPathParams() {
        return pathParams;
    }

    public void setPathParams(Map<String, Integer> pathParams) {
        this.pathParams = pathParams;
    }

}

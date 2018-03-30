package com.altas.core.annotation.restful.enumeration;

/**
 * Created by G_dragon on 2017/7/4.
 */
public enum MimeType {

    ANY("*/*"),
    TEXT("text/plain"),
    JSON("application/json"),
    URLENC("application/x-www-form-urlencoded"),
    IMGJPEG("image/jpeg");
    private String type;
    private MimeType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

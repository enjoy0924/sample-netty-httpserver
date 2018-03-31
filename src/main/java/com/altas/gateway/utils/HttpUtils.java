package com.altas.gateway.utils;

import com.altas.core.annotation.UrlHelper;
import com.altas.core.annotation.restful.enumeration.MimeType;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {

    public static final String HTTP_SESSION_KEY="altas-sessionId";

    public static Map<String, String> getQueryParamDictFromRequestUri(String uri) {

        Map<String, String> kv = new HashMap<>();
        int pos = uri.indexOf("?");
        if(-1 != pos){
//            try {
//                kv.putAll(getKvMap(uri.substring(pos + 1)));
//            } catch (KeyValueUnpairException | UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
        }


        return kv;
    }


    public static Map<String, String> getQueryParamDictFromString(String str){
        if(null == str || str.trim().isEmpty())
            return new HashMap<>();
        else {
//            try {
//                return getKvMap(str);
//            } catch (KeyValueUnpairException | UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
        }

        return new HashMap<>();
    }

    public static Map<String, String> getPathParamDictFromRequestUriAndPattern(String uri, String regexUrl) {
        Map<String,String> result = new HashMap<>();
//        result.put(CONST.KEY_PATH_URI,uri);
        return result;
    }

    public  static  String getBodyString(HttpRequest httpRequest, ByteBuf buf ) throws
            UnsupportedEncodingException {
        String encoding = httpRequest.headers().get(HttpHeaderNames.CONTENT_ENCODING);
        if (null == encoding) {
            encoding = "UTF-8";
        }
        byte[] bodyBytes = new byte[buf.readableBytes()];
        buf.readBytes(bodyBytes);
        String body = new String(bodyBytes, encoding);
        return body;
    }

//    public  static  String getBodyString(HttpRequest httpRequest, HttpContent httpContent) throws
//            UnsupportedEncodingException {
//        ByteBuf buf = httpContent.content();
//        String encoding = httpRequest.headers().get(HttpHeaderName.CONTENT_ENCODING.getName());
//        if (null == encoding) {
//            encoding = "UTF-8";
//        }
//        byte[] bodyBytes = new byte[buf.readableBytes()];
//        buf.readBytes(bodyBytes);
//        String body = new String(bodyBytes, encoding);
//        return body;
//    }

    private static boolean isKvBody(HttpRequest httpRequest,HttpContent httpContent) {
        ByteBuf buf = httpContent.content();
        if(0==buf.readableBytes()) {
            return  false;
        }else {
            String contentType = httpRequest.headers().get("Content-Type");
            if (contentType == null) {
                return true;
            } else {
                return contentType.equals(MimeType.URLENC);
            }
        }
    }

    public  static  String getSessionID(HttpRequest httpRequest) {
        String cookie = httpRequest.headers().get(HttpHeaderNames.COOKIE);
        if (cookie != null) {
            String[] kvs = cookie.split(";");

            for (String kv : kvs) {
                String[] strings = kv.split("=");
                if (strings[0].trim().equalsIgnoreCase(HTTP_SESSION_KEY)) {
                    return strings[1];
                }
            }
        }
        return null;
    }

    public static String getURI(FullHttpRequest fullHttpRequest) {
        String requestUrl = fullHttpRequest.uri();
        requestUrl= UrlHelper.getRequestUrl(requestUrl).trim();
        if (requestUrl.contains("?")) {
            requestUrl = requestUrl.split("\\?")[0];
        }

        return requestUrl;
    }
}

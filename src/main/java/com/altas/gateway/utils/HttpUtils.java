package com.altas.gateway.utils;

import com.altas.core.annotation.UrlHelper;
import com.altas.core.annotation.restful.enumeration.MimeType;
import com.altas.gateway.constant.CONST;
import com.altas.gateway.exception.KeyValueUnpairException;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangy on 2017/7/9.
 */
public class HttpUtils {

    public static final String HTTP_SESSION_KEY="alr-session-id";

    public static Map<String, String> getQueryParamDictFromRequestUri(String uri) {

        Map<String, String> kv = new HashMap<>();
        kv.putAll(_legacyGameQueryParam(uri));
        int pos = uri.indexOf("?");
        if(-1 != pos){
            try {
                kv.putAll(getKvMap(uri.substring(pos + 1)));
            } catch (KeyValueUnpairException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        return kv;
    }

    /**游戏框架修改的问题，这里已这种方式传输是没有办法*/
    private static Map<String, String> _legacyGameQueryParam(String uri) {

        Pattern pattern = Pattern.compile(";jsessionid=[0-9A-Za-z\\-]+__[0-9]+");
        Matcher matcher = pattern.matcher(uri);
        if(matcher.find()){
            String gameUri = matcher.group();

            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("shardingId", gameUri.split("__")[1]);
            queryParams.put("businessType","game");

            return queryParams;
        }
        return Collections.emptyMap();
    }


    public static Map<String, String> getQueryParamDictFromString(String str){
        if(null == str || str.trim().isEmpty())
            return new HashMap<>();
        else {
            try {
                return getKvMap(str);
            } catch (KeyValueUnpairException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return new HashMap<>();
    }

    public static Map<String, String> getPathParamDictFromRequestUriAndPattern(String uri, String regexUrl) {
        Map<String,String> result = new HashMap<>();
        result.put(CONST.KEY_PATH_URI,uri);
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

    private  static  Map<String,String> getKvMap(String kvString) throws KeyValueUnpairException, UnsupportedEncodingException {
        Map<String, String> result = new HashMap<String, String>();
        String[] kvs = kvString.split("&");
        for (int i = 0; i < kvs.length; i++) {

            String[] kv = kvs[i].split("=");
            if (kv.length < 2) {
                continue;
            }
            result.put(kv[0], filterValue(kv[1]));
        }
        return result;
    }

    private static String filterValue(String value) {
        if(null == value)
            return "";

        try {
            value=(null==value||value.trim().isEmpty())?"":URLDecoder.decode(value,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return value.replaceAll(";jsessionid", "");
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


    //    public static  Map<String,String> getKvMap(HttpRequest httpRequest,HttpContent httpContent) throws
//            UrlErrorException,KeyValueUnpairException,UnsupportedEncodingException{
//        String requestUrl = httpRequest.uri();
//        if (requestUrl.contains("?") || isKvBody(httpRequest, httpContent)) {
//            Map<String, String> kvMap = new HashMap<String, String>();
//            if (requestUrl.contains("?")) {
//                String[] strings = requestUrl.split("\\?");
//                if (2 != strings.length) {
//                    throw new UrlErrorException(requestUrl);
//                }
//                String kvString = strings[1];
//                kvMap.putAll(getKvMap(kvString));
//            }
//            if (isKvBody(httpRequest, httpContent)) {
//                kvMap.putAll(getBodyKvParam(httpRequest, httpContent));
//            }
//            return kvMap;
//        } else {
//            return null;
//        }
//    }


//    private  static Map<String,String> getBodyKvParam(HttpRequest httpRequest, HttpContent httpContent) throws
//            UnsupportedEncodingException,KeyValueUnpairException {
//        String body = getBodyString(httpRequest, httpContent);
//        return getKvMap(body);
//    }
}

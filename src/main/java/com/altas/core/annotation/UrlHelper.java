package com.altas.core.annotation;

import com.altas.core.annotation.pojo.PathParamUrl;
import com.altas.gateway.constant.CONST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UrlHelper {

    /**
     * 处理为规范统一的URL
     */
    public static String regularUrl(String url) {

        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }


    /**
     * 正则匹配url其中{}里面的内容，如/door/{db|dqb|rqb} 找出db|dqb|rqb后，
     * 再根据|分割；返回["door/qb","door/dqb","door/rqb"]
     * 如果不匹配，就返回原来的字符串列表。返回["/door/xx"]
     *
     * @param targetUrl
     * @return
     */
    public static List<String> matcherUrlToStringList(String targetUrl) {
        List<String> retList = new ArrayList<>();
        if (null == targetUrl || targetUrl.trim().isEmpty()) {
            return retList;
        }
        //正则匹配url其中{}里面的内容，如/door/{db|dqb|rqb}
        Pattern pattern = Pattern.compile(CONST.PATTERN_BRACES_INNER_WITHOUT_BRACES);
        Matcher matcher = pattern.matcher(targetUrl);
        if(matcher.find()){
            String[] urls= matcher.group().split("\\|");
            for(String url : urls){
                retList.add(targetUrl.replaceAll(CONST.PATTERN_BRACES_INNER_WITH_BRACES,url));
            }
            return retList;
        }
        retList.add(targetUrl);
        return retList;
    }

    /**
     * 将请求的url处理为标准的url(之前session等信息可能会放在url中去)
     */
    public static String getRequestUrl(String url) {
        if (url.contains(";")) {
            return url.split(";")[0];
        } else {
            return url;
        }
    }

    public static String complexUrlKey(String method, String url) {
        if (null == method)
            method = "";

        return method + ":" + url;
    }

    public static boolean urlContainsParam(String url) {
        String pattern = "\\{[\\w]+}";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(url);
        return m.find();
    }

    public static PathParamUrl getPathParamUrlInfo(String url) {

        PathParamUrl pathParamUrl = new PathParamUrl();
        Map<String, Integer> map = new HashMap<>();

        String pattern = "\\{[\\w]+}";
        String patternText = "[\\w]+";
        String findPattern = "{[w]+}";
        StringBuffer sb = new StringBuffer();
        String[] segments = url.split("/");
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(url);
        while (m.find()) {
            m.appendReplacement(sb, pattern);
            String paramName = m.group();
            for (int i = 0; i < segments.length; i++) {
                if (segments[i].equals(paramName))
                    map.put(paramName.substring(1, paramName.length() - 1), i);
            }

        }
        m.appendTail(sb);
        String urlPattern = sb.toString();
        urlPattern = urlPattern.replace(findPattern, patternText);

        pathParamUrl.setLength(url.split("/").length);
        pathParamUrl.setPatternString(urlPattern);
        pathParamUrl.setParamLocationMap(map);
        return pathParamUrl;
    }

}

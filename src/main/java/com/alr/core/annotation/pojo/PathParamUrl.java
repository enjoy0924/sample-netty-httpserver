package com.alr.core.annotation.pojo;

import java.util.Map;

/**
 * Created by zhangy on 2017/7/9.
 */
public class PathParamUrl {
    private int length;
    private Map<String,Integer> paramLocationMap;
    private String patternString;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Map<String, Integer> getParamLocationMap() {
        return paramLocationMap;
    }

    public void setParamLocationMap(Map<String, Integer> paramLocationMap) {
        this.paramLocationMap = paramLocationMap;
    }

    public String getPatternString() {
        return patternString;
    }

    public void setPatternString(String patternString) {
        this.patternString = patternString;
    }


}

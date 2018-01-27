package com.alr.core.annotation.pojo;

/**
 * Created by zhangy on 2017/7/6.
 */
public class AnnotationParam {

    public static final int PARAM_TYPE_HEADER = 1;
    public static final int PARAM_TYPE_PATH   = 2;
    public static final int PARAM_TYPE_BODY   = 3;
    public static final int PARAM_TYPE_QUERY  = 4;

    private int index;
    private String paramName;
    private Class<?> type;
    private int paramType;
    private ParamConstraint constraint;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public int getParamType() {
        return paramType;
    }

    public void setParamType(int paramType) {
        this.paramType = paramType;
    }

    public ParamConstraint getConstraint() {
        if(null == constraint)
            constraint = new ParamConstraint();
        return constraint;
    }

    public void setConstraint(ParamConstraint constraint) {
        this.constraint = constraint;
    }
}

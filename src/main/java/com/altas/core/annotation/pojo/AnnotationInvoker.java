package com.altas.core.annotation.pojo;

import com.altas.gateway.exception.ParamLackException;
import com.altas.gateway.session.Session;
import com.altas.gateway.utils.ConvertUtils;
import io.netty.handler.codec.http.HttpHeaders;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class AnnotationInvoker {

    private Method method;
    private Object object;
    private PermissionConstraint permissionConstraint;
    private ConsumeConstraint consumeConstraint;
    private ProduceConstraint produceConstraint;
    private List<AnnotationParam> queryParams;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public PermissionConstraint getPermissionConstraint() {
        return permissionConstraint;
    }

    public void setPermissionConstraint(PermissionConstraint permissionConstraint) {
        this.permissionConstraint = permissionConstraint;
    }

    public ConsumeConstraint getConsumeConstraint() {
        return consumeConstraint;
    }

    public void setConsumeConstraint(ConsumeConstraint consumeConstraint) {
        this.consumeConstraint = consumeConstraint;
    }

    public ProduceConstraint getProduceConstraint() {
        return produceConstraint;
    }

    public void setProduceConstraint(ProduceConstraint produceConstraint) {
        this.produceConstraint = produceConstraint;
    }

    public List<AnnotationParam> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(List<AnnotationParam> queryParams) {
        this.queryParams = queryParams;
    }

    public Object[] reformParamsFromInvokeParamsAndHeadersAndBody(Map<String, String> queryParamDict, Map<String, String> formParamDict, Map<String, String> pathParamDict,
                                                                  HttpHeaders headers, String body, Map<String, String> sessionAttr) throws ParamLackException {

        Object[] params = new Object[queryParams.size()];
        for (AnnotationParam param : queryParams) {
            Class<?> type = param.getType();
            String value = "";
            String paramName = param.getParamName();
            if (param.getParamType() == AnnotationParam.PARAM_TYPE_HEADER) {
                //头里面的参数
                value = headers.get(paramName);
                if (null == value && param.getConstraint().required()) {
                    throw new ParamLackException("lack of required parameter : " + paramName);
                }
            } else if(param.getParamType() == AnnotationParam.PARAM_TYPE_FORM){

                value = formParamDict.get(paramName);
                if (null == value && param.getConstraint().required()) {
                    throw new ParamLackException("lack of required parameter : " + paramName);
                }

            } else if (param.getParamType() == AnnotationParam.PARAM_TYPE_QUERY) {
                //URL或者body里面的查询参数
                value = queryParamDict.get(paramName);
                if (null == value && param.getConstraint().required()) {
                    throw new ParamLackException("lack of required parameter : " + paramName);
                }

            } else if (param.getParamType() == AnnotationParam.PARAM_TYPE_PATH) {
                //路径参数
                value = pathParamDict.get(paramName);
            } else if (param.getParamType() == AnnotationParam.PARAM_TYPE_BODY) {
                //Body参数
                value = body;
            } else if(param.getParamType() == AnnotationParam.PARAM_TYPE_SESSION_ATTR){

                value = sessionAttr.get(paramName);
                if (null == value && param.getConstraint().required()) {
                    throw new ParamLackException("lack of required parameter : " + paramName);
                }
            }

            params[param.getIndex()] = ConvertUtils.forceTypeConvert(type, value);
        }

        return params;
    }


}

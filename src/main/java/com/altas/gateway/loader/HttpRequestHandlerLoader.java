package com.altas.gateway.loader;

import com.altas.core.annotation.ReflectHelper;
import com.altas.core.annotation.UrlHelper;
import com.altas.core.annotation.pojo.*;
import com.altas.core.annotation.restful.*;
import com.altas.exception.UnknownUrlException;
import com.altas.exception.UrlInvokerNotFoundException;
import com.altas.gateway.exception.UnsupportedMethodException;
import com.altas.gateway.exception.UrlRepeatException;
import com.altas.gateway.utils.LoggerHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class HttpRequestHandlerLoader {

    private static HttpRequestHandlerLoader instance = new HttpRequestHandlerLoader();

    private HttpRequestHandlerLoader() {
    }

    public static HttpRequestHandlerLoader getInstance() {
        return instance;
    }

    private Map<String, UrlInvokerGroup> url2UrlInvokerGroupDict = new ConcurrentHashMap<>();

    public boolean loadUrlDictFromAnnotation(List<String> packages) {
        try {
            if (scanAnnotationFromPackages(packages))
                return true;
        } catch (Exception e) {
            LoggerHelper.error(e.getMessage(), e);
            return false;
        }

        return false;
    }

    private boolean scanAnnotationFromPackages(List<String> packages) throws IllegalAccessException, InstantiationException, UrlRepeatException {

        List<Class<?>> classesInPackages = new ArrayList<>();

        //获取设置的包里面的所有的类类型
        for (String packageName : packages) {
            classesInPackages.addAll(ReflectHelper.getClassesWithPackageName(packageName));

        }
        for (Class<?> classItem : classesInPackages) {
            Api api = classItem.getAnnotation(Api.class);
            if (api != null) {
                Url url = classItem.getAnnotation(Url.class);
                String baseUrl = "";
                if (null != url) {
                    baseUrl = UrlHelper.regularUrl(url.value());
                }
                scanClassMethodAnnotation(classItem, baseUrl, classItem.newInstance());
            }
        }

        return true;
    }

    public UrlInvoker getInvokerByMethodAndUrl(String method, String url) throws UnsupportedMethodException, UnknownUrlException, UrlInvokerNotFoundException {

        UrlInvokerGroup urlInvokerGroup = url2UrlInvokerGroupDict.get(url);
        if(null == urlInvokerGroup)
            throw new UnknownUrlException("url " +url+ " mapping failed");

        UrlInvoker urlInvoker = urlInvokerGroup.getAnnotationInvokerByHttpMethod(method);
        if (null == urlInvoker) {
            throw new UrlInvokerNotFoundException("url "+url+ "invoker not found");
        }

        return urlInvoker;
    }

    private boolean scanClassMethodAnnotation(Class<?> classType, String baseUrl, Object instance) throws UrlRepeatException {

        //获取类的所有方法
        Method[] methods = classType.getMethods();
        List<String> classUrls;
        List<String> methodUrls;
        for (Method method : methods) {
            Url methodUrl = method.getAnnotation(Url.class);
            if (methodUrl != null) {
                classUrls = UrlHelper.matcherUrlToStringList(baseUrl);
                for (String classUrl : classUrls) {
                    methodUrls = UrlHelper.matcherUrlToStringList(UrlHelper.regularUrl(methodUrl.value()));
                    for (String mUrl : methodUrls) {
                        //使用类的url组合方法的url构成完整的url
                        String fullUrl = classUrl + mUrl;

                        //Url映射方法的信息
                        UrlInvoker invoker = new UrlInvoker();
                        invoker.setMethod(method);
                        invoker.setObject(instance);
                        invoker.setPermissionConstraint(scanPermissionWithMethod(method));
                        invoker.setConsumeConstraint(scanConsumerWithMethod(method));
                        invoker.setProduceConstraint(scanProducerWithMethod(method));
                        invoker.setQueryParams(scanQueryParamsWithMethod(method));
                        invoker.setBaseUrl(baseUrl);
                        invoker.setUrl(fullUrl);

                        //将方法加入字典
                        addInvoker2Dict(fullUrl, invoker);
                    }
                }

            }
        }

        return true;
    }


    private void addInvoker2Dict(String fullUrl, UrlInvoker invoker) throws UrlRepeatException {
        //将方法加入字典
        if (!url2UrlInvokerGroupDict.containsKey(fullUrl)) {
            url2UrlInvokerGroupDict.put(fullUrl, new UrlInvokerGroup());
        }
        boolean success = url2UrlInvokerGroupDict.get(fullUrl).addAnnotationInvoker(invoker.getConsumeConstraint().getMethod().name(), invoker);
        if (!success) {
            throw new UrlRepeatException("duplicated Url" + fullUrl + "" + invoker.getConsumeConstraint().getMethod().name());
        }
    }

    private PathParamUrl scanPathParamsWithUrl(String fullUrl) {
        return UrlHelper.getPathParamUrlInfo(fullUrl);
    }

    private ProduceConstraint scanProducerWithMethod(Method method) {

        ProduceConstraint produceConstraint = new ProduceConstraint();

        Producer producer = method.getAnnotation(Producer.class);
        if (null != producer) {
            produceConstraint.setMimeType(producer.type());
        }

        return produceConstraint;
    }

    private ConsumeConstraint scanConsumerWithMethod(Method method) {

        ConsumeConstraint consumeConstraint = new ConsumeConstraint();

        Consumer consumer = method.getAnnotation(Consumer.class);
        if (null != consumer) {
            consumeConstraint.setMethod(consumer.method());
            consumeConstraint.setMimeType(consumer.type());
        }

        return consumeConstraint;
    }

    private PermissionConstraint scanPermissionWithMethod(Method method) {

        PermissionConstraint permissionConstraint = new PermissionConstraint();

        Permission permission = method.getAnnotation(Permission.class);
        if (null != permission) {
            String permissionValue = permission.value();
            if (null != permissionValue) {
                permissionConstraint.addPermissions(permissionValue,";");
            }
        }
        return permissionConstraint;
    }

    /**
     * 扫描一个方法里面所有的参数集合
     *
     * @param method
     * @return
     */
    private List<AnnotationParam> scanQueryParamsWithMethod(Method method) {
        List<AnnotationParam> paramList = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            AnnotationParam param = new AnnotationParam();
            param.setIndex(i);

            do {
                QueryParam queryParam = parameters[i].getAnnotation(QueryParam.class);
                if (null != queryParam) {
                    param.setParamName(queryParam.value());
                    param.setParamType(AnnotationParam.PARAM_TYPE_QUERY);
                    param.getConstraint().setRequired(queryParam.required());
                    break;
                }

                FormParam formParam = parameters[i].getAnnotation(FormParam.class);
                if (null != formParam) {
                    param.setParamName(formParam.value());
                    param.setParamType(AnnotationParam.PARAM_TYPE_FORM);
                    param.getConstraint().setRequired(formParam.required());
                    break;
                }

                HeaderParam headerParam = parameters[i].getAnnotation(HeaderParam.class);
                if (null != headerParam) {
                    param.setParamName(headerParam.value());
                    param.setParamType(AnnotationParam.PARAM_TYPE_HEADER);
                    param.getConstraint().setRequired(headerParam.required());
                    break;
                }

                PathParam pathParam = parameters[i].getAnnotation(PathParam.class);
                if (null != pathParam) {
                    param.setParamName(pathParam.value());
                    param.setParamType(AnnotationParam.PARAM_TYPE_PATH);
                    break;
                }

                SessionAttr sessionAttr = parameters[i].getAnnotation(SessionAttr.class);
                if (null != sessionAttr) {
                    param.setParamName(sessionAttr.value());
                    param.setParamType(AnnotationParam.PARAM_TYPE_SESSION_ATTR);
                    break;
                }

                BodyParam bodyParam = parameters[i].getAnnotation(BodyParam.class);
                if (null != bodyParam) {
                    param.setParamType(AnnotationParam.PARAM_TYPE_BODY);
                    break;
                }
            } while (false);

            param.setType(parameters[i].getType());
            paramList.add(param);
        }
        return paramList;
    }
}


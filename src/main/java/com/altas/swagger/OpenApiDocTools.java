package com.altas.swagger;

import com.altas.core.annotation.ReflectHelper;
import com.altas.core.annotation.UrlHelper;
import com.altas.core.annotation.pojo.AnnotationParam;
import com.altas.core.annotation.pojo.ConsumeConstraint;
import com.altas.core.annotation.pojo.PermissionConstraint;
import com.altas.core.annotation.pojo.ProduceConstraint;
import com.altas.core.annotation.restful.*;
import com.altas.core.annotation.restful.enumeration.HttpMethod;
import com.altas.gateway.exception.UrlRepeatException;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class OpenApiDocTools {

    public static void main(String[] argv){

        List<String> apiBasePackages = new ArrayList<>();
        apiBasePackages.add("com.altas.api");

        try {

            OpenApiDocTools.loadServer();

            OpenApiDocTools.loadInfo();

            OpenApiDocTools.loadAnnotation(apiBasePackages);

            OpenApiDocTools.jsonDocument();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (UrlRepeatException e) {
            e.printStackTrace();
        }

    }

    private static OpenAPI openAPI = new OpenAPI();

    /**
     servers:
     - url: 'https://virtserver.swaggerhub.com/Altas/ERP/1.0.0'
     - url: 'http://virtserver.swaggerhub.com/Altas/ERP/1.0.0'
     info:
     description: |
     This is a sample Petstore server.  You can find
     out more about Swagger at
     [http://swagger.io](http://swagger.io) or on
     [irc.freenode.net, #swagger](http://swagger.io/irc/).
     version: "1.0.0-oas3"
     title: Swagger Petstore
     termsOfService: 'http://swagger.io/terms/'
     contact:
     email: apiteam@swagger.io
     license:
     name: Apache 2.0
     url: 'http://www.apache.org/licenses/LICENSE-2.0.html'
     * */

    public static void loadInfo(){
        Info info = new Info();
        info.setDescription("后端接口的HTTP API文档");
        info.setVersion("1.1.0");
        info.setTitle("Swagger auto display");
        info.setTermsOfService("这是一份服务声明");

        Contact contact = new Contact();
        contact.setName("Andy");
        contact.setEmail("enjoy0924@gamil.com");

        openAPI.setInfo(info);
    }

    public static void loadServer(){

        Server server = new Server();
        server.setUrl("www.xuexihappy.com");

        openAPI.addServersItem(server);
    }


    public static void loadAnnotation(List<String> apiBasePackages) throws IllegalAccessException, InstantiationException, UrlRepeatException {

        List<Class<?>> classesInPackages = new ArrayList<>();

        //获取设置的包里面的所有的类类型
        for (String packageName : apiBasePackages) {
            classesInPackages.addAll(ReflectHelper.getClassesWithPackageName(packageName));

        }
        for (Class<?> classItem : classesInPackages) {
            Api api = classItem.getAnnotation(Api.class);
            if(null == api)
                continue;

            String tag = api.tag();
            String description = api.description();
            addTag(tag, description);

            if (api != null) {
                Url url = classItem.getAnnotation(Url.class);
                String baseUrl = "";
                if (null != url) {
                    baseUrl = UrlHelper.regularUrl(url.value());
                }
                scanClassMethodAnnotation(tag, classItem, baseUrl);
            }
        }
    }

    private static void addTag(String tagDesc, String description) {

        boolean exist = false;
        List<Tag> tags = openAPI.getTags();
        if(null == tags){
            tags = new ArrayList<>();
            openAPI.setTags(tags);
        }
        for(Tag tag : tags){
            if(tag.getName().equalsIgnoreCase(tagDesc)){
                exist = true;
                break;
            }
        }

        if(!exist){
            Tag tag = new Tag();
            tag.setName(tagDesc);
            tag.setDescription(description);
            openAPI.addTagsItem(tag);
        }

    }

    private static boolean scanClassMethodAnnotation(String tag, Class<?> classType, String baseUrl) throws UrlRepeatException {

        //获取类的所有方法
        Method[] methods = classType.getMethods();
        List<String> classUrls;
        List<String> methodUrls;
        for (Method method : methods) {
            Url methodUrl = method.getAnnotation(Url.class);
            if(null == methodUrl)
                continue;

            Deprecated deprecated = method.getAnnotation(Deprecated.class);
            String methodName = method.getName();
            boolean isDeprecated = (null!=deprecated);
            if (methodUrl != null) {
                String description = methodUrl.description();
                classUrls = UrlHelper.matcherUrlToStringList(baseUrl);
                for (String classUrl : classUrls) {
                    methodUrls = UrlHelper.matcherUrlToStringList(UrlHelper.regularUrl(methodUrl.value()));
                    for (String mUrl : methodUrls) {
                        //使用类的url组合方法的url构成完整的url
                        String path = classUrl + mUrl;

                        PermissionConstraint permissionConstraint = scanPermissionWithMethod(method);
                        ConsumeConstraint consumeConstraint       = scanConsumerWithMethod(method);
                        ProduceConstraint produceConstraint       = scanProducerWithMethod(method);
                        List<AnnotationParam> annotationParams    = scanQueryParamsWithMethod(method);

                        addPath(tag, description, path, methodName, isDeprecated, permissionConstraint, consumeConstraint, produceConstraint, annotationParams);
                    }
                }

            }
        }

        return true;
    }

    private static void addPath(String tag, String summary, String path, String operationId, boolean isDeprecated, PermissionConstraint permissionConstraint, ConsumeConstraint consumeConstraint, ProduceConstraint produceConstraint, List<AnnotationParam> annotationParams) {

        PathItem pathItem = new PathItem();
        pathItem.setSummary(summary);
        pathItem.description(summary);

        PathItem.HttpMethod httpMethod = getHttpMethod(consumeConstraint);
        Operation operation = new Operation();
        operation.deprecated(isDeprecated);
        operation.addTagsItem(tag);
        operation.setSecurity(getSecurity(permissionConstraint));
        operation.setResponses(getResponse(produceConstraint));
        operation.setOperationId(operationId);
//        operation.setParameters(getParameters(annotationParams));

        operation.setRequestBody(getRequestBody(annotationParams, consumeConstraint));

        pathItem.operation(httpMethod, operation);

        Paths paths = openAPI.getPaths();
        if(null == paths){
            paths = new Paths();
            openAPI.setPaths(paths);
        }

        paths.addPathItem(path, pathItem);

    }

    private static ApiResponses getResponse(ProduceConstraint produceConstraint) {
        ApiResponses apiResponses = new ApiResponses();

        return apiResponses;
    }

    private static List<SecurityRequirement> getSecurity(PermissionConstraint permissionConstraint) {

        List<SecurityRequirement> securityRequirements = new ArrayList<>();

        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList("permission",permissionConstraint.getPermissions());

        return securityRequirements;
    }

    private static RequestBody getRequestBody(List<AnnotationParam> annotationParams, ConsumeConstraint consumeConstraint) {

        RequestBody requestBody = new RequestBody();

        Content content = new Content();
        MediaType mediaType = new MediaType();

        Schema schema = new Schema();
        schema.setType("object");
        List<String> requiredItem = new ArrayList<>();
        for(AnnotationParam annotationParam : annotationParams) {
            Schema property = new Schema();
            int paramIn = annotationParam.getParamType();
            if(paramIn == AnnotationParam.PARAM_TYPE_BODY){
                continue;
            }

            String type = getParamType(annotationParam.getType().getTypeName());
            if(null != type) {
                property.setType(type);
            }
            String format = getFormatByType(annotationParam.getType().getTypeName());
            if(null != format) {
                property.setFormat(format);
            }

            String paramName = annotationParam.getParamName();
            if(null != paramName) {
                property.setReadOnly(true);
                if(annotationParam.getConstraint().required()){
                    requiredItem.add(annotationParam.getParamName());
                }
                schema.addProperties(annotationParam.getParamName(), property);
            }
        }
        schema.required(requiredItem);
        mediaType.setSchema(schema);
        content.addMediaType(consumeConstraint.getMimeType().getType(), mediaType);

        //TODO 设置参数的例子
        mediaType.getExamples();

        requestBody.setContent(content);

        return requestBody;
    }

    private static String getFormatByType(String type) {

        if(type.contains("Integer")){
            return "int32";
        }else if(type.contains("Float")){
            return "float";
        }else if(type.contains("Long")){
            return "int64";
        }else if(type.contains("Double")){
            return "double";
        }else if(type.contains("Date")){
            return "date";
        }

        return null;
    }

    /**
     integer	integer	int32	signed 32 bits
     long	integer	int64	signed 64 bits
     float	number	float
     double	number	double
     string	string
     byte	string	byte	base64 encoded characters
     binary	string	binary	any sequence of octets
     boolean	boolean
     date	string	date	As defined by full-date - RFC3339
     dateTime	string	date-time	As defined by date-time - RFC3339
     password	string	password	A hint to UIs to obscure input.
     * */

    private static String getParamType(String typeName) {
        if(typeName.contains("String")){
            return "string";
        }else if(typeName.contains("Integer")){
            return "integer";
        }else if(typeName.contains("Float")){
            return "number";
        }else if(typeName.contains("Long")){
            return "integer";
        }else if(typeName.contains("Double")){
            return "number";
        }else if(typeName.contains("Boolean")){
            return "boolean";
        }else if(typeName.contains("Date")){
            return "string";
        }

        return null;
    }


    private static List<io.swagger.v3.oas.models.parameters.Parameter> getParameters(List<AnnotationParam> annotationParams) {

        List<io.swagger.v3.oas.models.parameters.Parameter> parameters = new ArrayList<>();
        for(AnnotationParam annotationParam : annotationParams){

            io.swagger.v3.oas.models.parameters.Parameter parameter = new io.swagger.v3.oas.models.parameters.Parameter();

            parameter.required(annotationParam.getConstraint().required());
            parameter.explode(true);
//            parameter.setContent(content);
//            parameter.set

            parameters.add(parameter);
        }

        return parameters;
    }

    private static PathItem.HttpMethod getHttpMethod(ConsumeConstraint consumeConstraint) {

        HttpMethod httpMethod = consumeConstraint.getMethod();
        if(httpMethod == HttpMethod.POST) {
            return PathItem.HttpMethod.POST;
        } else if(httpMethod == HttpMethod.GET){
            return PathItem.HttpMethod.GET;
        } else if(httpMethod == HttpMethod.PUT){
            return PathItem.HttpMethod.PUT;
        } else if(httpMethod == HttpMethod.DELETE){
            return PathItem.HttpMethod.DELETE;
        }else {
            return PathItem.HttpMethod.TRACE;
        }
    }

    private static ProduceConstraint scanProducerWithMethod(Method method) {

        ProduceConstraint produceConstraint = new ProduceConstraint();

        Producer producer = method.getAnnotation(Producer.class);
        if (null != producer) {
            produceConstraint.setMimeType(producer.type());
        }

        return produceConstraint;
    }

    private static ConsumeConstraint scanConsumerWithMethod(Method method) {

        ConsumeConstraint consumeConstraint = new ConsumeConstraint();

        Consumer consumer = method.getAnnotation(Consumer.class);
        if (null != consumer) {
            consumeConstraint.setMethod(consumer.method());
            consumeConstraint.setMimeType(consumer.type());
        }

        return consumeConstraint;
    }

    private static PermissionConstraint scanPermissionWithMethod(Method method) {

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

    private static List<AnnotationParam> scanQueryParamsWithMethod(Method method) {
        List<AnnotationParam> paramList = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            AnnotationParam param = new AnnotationParam();
            param.setIndex(i);

            do {
                FormParam formParam = parameters[i].getAnnotation(FormParam.class);
                if (null != formParam) {
                    param.setParamName(formParam.value());
                    param.setParamType(AnnotationParam.PARAM_TYPE_QUERY);
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

    public static String jsonDocument(){

        try {
            System.out.println(Json.pretty().writeValueAsString(openAPI));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "";
    }
}

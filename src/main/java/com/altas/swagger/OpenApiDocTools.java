package com.altas.swagger;

import com.altas.core.annotation.ReflectHelper;
import com.altas.core.annotation.UrlHelper;
import com.altas.core.annotation.pojo.AnnotationParam;
import com.altas.core.annotation.restful.*;
import com.altas.core.annotation.restful.enumeration.HttpMethod;
import com.altas.gateway.exception.UrlRepeatException;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.models.*;
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenApiDocTools {

    public static void main(String[] argv){

        List<String> apiBasePackages = new ArrayList<>();
        apiBasePackages.add("com.altas.api");

        List<String> extendPackages = new ArrayList<>();
        extendPackages.add("com.altas.gateway.schema");
        try {

            OpenApiDocTools.loadServer();

            OpenApiDocTools.loadInfo();

            OpenApiDocTools.loadAnnotation(apiBasePackages);

            OpenApiDocTools.loadExtendAnnotation(extendPackages);

            OpenApiDocTools.jsonDocument();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (UrlRepeatException e) {
            e.printStackTrace();
        }

    }

    private static void loadExtendAnnotation(List<String> extendPackages) {
        List<Class<?>> classesInPackages = new ArrayList<>();

        //获取设置的包里面的所有的类类型
        for (String packageName : extendPackages) {
            classesInPackages.addAll(ReflectHelper.getClassesWithPackageName(packageName));

        }

        Components components = new Components();
        for (Class<?> classItem : classesInPackages) {
            Annotation[] annotations = classItem.getAnnotations();
            if(null == annotations || annotations.length==0)
                continue;
            for(Annotation annotation : annotations){
                if(annotation instanceof io.swagger.v3.oas.annotations.media.Schema){

                    Schema schema = new Schema();
                    schema.setType("object");

                    Field[] fields = classItem.getDeclaredFields();
                    Map<String, Schema> properties = new HashMap<>();
                    for(Field field : fields){
                        String name = field.getName();
                        String type = field.getType().getTypeName();

                        Schema property = new Schema();
                        property.setType(getParamType(type));
                        property.setFormat(getParamType(type));

                        properties.put(name, property);

                    }
                    schema.setProperties(properties);
                    String typeName = classItem.getTypeName();
                    String[] type = typeName.split("\\.");
                    components.addSchemas(type[type.length-1] , schema);
                }
            }
        }
        openAPI.components(components);
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
        server.setUrl("www.altas.com");

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

            Url urlAnnotation = method.getAnnotation(Url.class);
            if(null == urlAnnotation)
                continue;

            Deprecated deprecated = method.getAnnotation(Deprecated.class);
            String methodName = method.getName();

            List<QueryParam> queryParamAnnotations = new ArrayList<>();
            List<PathParam> pathParamAnnotations = new ArrayList<>();
            List<HeaderParam> headerParamAnnotations = new ArrayList<>();
            List<FormParam> formParamAnnotations = new ArrayList<>();
            List<SessionAttr> sessionAttrAnnotations = new ArrayList<>();
            Annotation[][] annotationArray = method.getParameterAnnotations();
            for(Annotation[] annotations: annotationArray){
                for(Annotation annotation : annotations){
                    if(annotation instanceof  QueryParam){
                        queryParamAnnotations.add((QueryParam) annotation);
                    }else if(annotation instanceof PathParam){
                        pathParamAnnotations.add((PathParam) annotation);
                    }else if(annotation instanceof HeaderParam){
                        headerParamAnnotations.add((HeaderParam) annotation);
                    }else if(annotation instanceof FormParam){
                        formParamAnnotations.add((FormParam) annotation);
                    }else if(annotation instanceof SessionAttr){
                        sessionAttrAnnotations.add((SessionAttr)annotation);
                    }
                }
            }

            List<io.swagger.v3.oas.models.parameters.Parameter> parameters = getParametersByAnnotation(
                    queryParamAnnotations, pathParamAnnotations, headerParamAnnotations, sessionAttrAnnotations);

            RequestBody requestBody = getRequestBodyByAnnotation(method.getAnnotation(Consumer.class), formParamAnnotations);
            ApiResponses apiResponses = getResponseByAnnotation(method.getAnnotation(io.swagger.v3.oas.annotations.responses.ApiResponses.class), method.getAnnotation(Producer.class));
            List<SecurityRequirement> securityRequirements = getSecurityByAnnotation(method.getAnnotation(Permission.class));

            boolean isDeprecated = (null!=deprecated);
            if (urlAnnotation != null) {
                String description = urlAnnotation.description();
                classUrls = UrlHelper.matcherUrlToStringList(baseUrl);
                for (String classUrl : classUrls) {
                    methodUrls = UrlHelper.matcherUrlToStringList(UrlHelper.regularUrl(urlAnnotation.value()));
                    for (String mUrl : methodUrls) {
                        //使用类的url组合方法的url构成完整的url
                        String path = classUrl + mUrl;

                        PathItem pathItem = new PathItem();
                        pathItem.setSummary(urlAnnotation.summary());
                        pathItem.description(description);

                        PathItem.HttpMethod httpMethod = getHttpMethodByAnnotation(method.getAnnotation(Consumer.class));
                        Operation operation = new Operation();
                        operation.deprecated(isDeprecated);
                        operation.addTagsItem(tag);
                        operation.setSecurity(securityRequirements);
                        operation.setResponses(apiResponses);
                        operation.setOperationId(methodName);
                        if(null != parameters && !parameters.isEmpty())
                            operation.setParameters(parameters);
                        operation.setRequestBody(requestBody);

                        pathItem.operation(httpMethod, operation);

                        Paths paths = openAPI.getPaths();
                        if(null == paths){
                            paths = new Paths();
                            openAPI.setPaths(paths);
                        }

                        paths.addPathItem(path, pathItem);
                    }
                }

            }
        }

        return true;
    }

    private static List<io.swagger.v3.oas.models.parameters.Parameter> getParametersByAnnotation(
            List<QueryParam> queryParamAnnotations, List<PathParam> pathParamAnnotations, List<HeaderParam> headerParamAnnotations, List<SessionAttr> sessionAttrAnnotations) {

        List<io.swagger.v3.oas.models.parameters.Parameter> parameters = new ArrayList<>();
        for(PathParam pathParamAnnotation : pathParamAnnotations){
            io.swagger.v3.oas.models.parameters.Parameter parameter = new io.swagger.v3.oas.models.parameters.Parameter();
            parameter.setRequired(pathParamAnnotation.required());
            parameter.setName(pathParamAnnotation.value());
            parameter.setIn(ParameterIn.PATH.name().toLowerCase());

            Schema schema = new Schema();
            schema.setType(pathParamAnnotation.type());
            schema.setFormat(pathParamAnnotation.format());

            parameter.setSchema(schema);

            parameters.add(parameter);
        }

        for(HeaderParam headerParamAnnotation : headerParamAnnotations){
            io.swagger.v3.oas.models.parameters.Parameter parameter = new io.swagger.v3.oas.models.parameters.Parameter();
            parameter.setRequired(headerParamAnnotation.required());
            parameter.setName(headerParamAnnotation.value());
            parameter.setIn(ParameterIn.HEADER.name().toLowerCase());

            Schema schema = new Schema();
            schema.setType(headerParamAnnotation.type());
            schema.setFormat(headerParamAnnotation.format());

            parameter.setSchema(schema);

            parameters.add(parameter);
        }

        for(QueryParam queryParamAnnotation : queryParamAnnotations){
            io.swagger.v3.oas.models.parameters.Parameter parameter = new io.swagger.v3.oas.models.parameters.Parameter();
            parameter.setRequired(queryParamAnnotation.required());
            parameter.setName(queryParamAnnotation.value());
            parameter.setIn(ParameterIn.QUERY.name().toLowerCase());

            Schema schema = new Schema();
            schema.setType(queryParamAnnotation.type());
            schema.setFormat(queryParamAnnotation.format());

            parameter.setSchema(schema);

            parameters.add(parameter);
        }

        for(SessionAttr sessionAttrAnnotation : sessionAttrAnnotations){
            io.swagger.v3.oas.models.parameters.Parameter parameter = new io.swagger.v3.oas.models.parameters.Parameter();
            parameter.setRequired(sessionAttrAnnotation.required());
            parameter.setName(sessionAttrAnnotation.value());
            parameter.setIn(ParameterIn.COOKIE.name().toLowerCase());
            parameter.setDescription("system auto inject when session is available!");

            Schema schema = new Schema();
            schema.setType("string");
//            schema.setFormat(queryParamAnnotation.format());

            parameter.setSchema(schema);

            parameters.add(parameter);
        }

        return parameters;
    }

    private static ApiResponses getResponseByAnnotation(io.swagger.v3.oas.annotations.responses.ApiResponses apiResponsesAnnotation, Producer producer) {

        ApiResponses apiResponses = new ApiResponses();

        ApiResponse[] apiResponseAnnotations = apiResponsesAnnotation.value();

        for (ApiResponse apiResponseAnnotation : apiResponseAnnotations) {

            String responseCode = apiResponseAnnotation.responseCode();
            String description = apiResponseAnnotation.description();

            io.swagger.v3.oas.models.responses.ApiResponse apiResponse = new io.swagger.v3.oas.models.responses.ApiResponse();
            apiResponse.description(description);


            io.swagger.v3.oas.annotations.media.Content[] contentAnnotations = apiResponseAnnotation.content();
            if (contentAnnotations.length > 0) {
                for (io.swagger.v3.oas.annotations.media.Content contentAnnotation : contentAnnotations) {
                    Content content = new Content();

                    Schema schemaDict = getCommonSchemaDict();

                    Schema schema = new Schema();
                    io.swagger.v3.oas.annotations.media.Schema schemaAnnotation = contentAnnotation.schema();
                    schema.set$ref(schemaAnnotation.ref());
                    schema.setDescription(schemaAnnotation.description());
                    schemaDict.addProperties("payload", schema);

                    MediaType mediaType = new MediaType();
                    mediaType.setSchema(schemaDict);
                    content.addMediaType(producer.type().getType(), mediaType);

                    //设置@Examples
//                    mediaType.setExamples();

                    apiResponse.setContent(content);
                }
            } else {
                MediaType mediaType = new MediaType();
                mediaType.setSchema(getCommonSchemaDict());
                Content content = new Content();
                content.addMediaType(producer.type().getType(), mediaType);

                apiResponse.setContent(content);
            }

            apiResponses.addApiResponse(String.format("code=%s",responseCode), apiResponse);
        }

        return apiResponses;
    }

    private static List<SecurityRequirement> getSecurityByAnnotation(Permission permissionAnnotaion) {

        List<SecurityRequirement> securityRequirements = new ArrayList<>();

        SecurityRequirement securityRequirement = new SecurityRequirement();

        String permissionVal = permissionAnnotaion.value();
        String[] permissions = permissionVal.split(";");
        for(String permission : permissions) {
            securityRequirement.addList(permission);
        }

        return securityRequirements;
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

        return "object";
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

        return "object";
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

    private static PathItem.HttpMethod getHttpMethodByAnnotation(Consumer consumer) {

        HttpMethod httpMethod = consumer.method();
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

    private static RequestBody getRequestBodyByAnnotation(Consumer consumer, List<FormParam> formParamAnnotations) {

        RequestBody requestBody = new RequestBody();

        Content content = new Content();
        MediaType mediaType = new MediaType();

        Schema schema = new Schema();
        schema.setType("object");
        List<String> requiredItem = new ArrayList<>();
        for(FormParam formParam : formParamAnnotations) {
            Schema property = new Schema();
            property.setType(formParam.type());

            String format = formParam.format();
            if(!format.trim().isEmpty()) {
                property.setFormat(format);
            }

            String paramName = formParam.value();
            if(null != paramName) {
                property.setReadOnly(true);
                if(formParam.required()){
                    requiredItem.add(paramName);
                }
                schema.addProperties(paramName, property);
            }
        }
        schema.required(requiredItem);
        mediaType.setSchema(schema);
        content.addMediaType(consumer.type().getType(), mediaType);

        //TODO 设置参数的例子
        mediaType.getExamples();

        requestBody.setContent(content);

        return requestBody;
    }


    public static String jsonDocument(){

        try {
            System.out.println(Json.pretty().writeValueAsString(openAPI));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static Schema getCommonSchemaDict() {
        List<String> requiredProp = new ArrayList<>();
        requiredProp.add("code");
        requiredProp.add("message");

        Schema schemaDict = new Schema();
        schemaDict.required(requiredProp);

        Schema schemaCode = new Schema();
        schemaCode.setFormat("number");
        schemaCode.setType("integer");
        schemaDict.addProperties("code", schemaCode);

        Schema schemaMessage = new Schema();
        schemaMessage.setFormat("string");
        schemaMessage.setType("string");
        schemaDict.addProperties("message", schemaMessage);

        return schemaDict;
    }
}

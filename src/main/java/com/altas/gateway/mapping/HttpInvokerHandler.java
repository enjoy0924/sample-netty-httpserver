package com.altas.gateway.mapping;

import com.alr.api.ResultDictionary;
import com.altas.core.annotation.pojo.UrlInvoker;
import com.altas.core.annotation.restful.enumeration.MimeType;
import com.alr.core.utils.JsonHelper;
import com.alr.core.utils.LoggerHelper;
import com.altas.exception.UrlInvokerNotFoundException;
import com.altas.gateway.constant.CONST;
import com.altas.gateway.exception.ParamLackException;
import com.altas.gateway.loader.HttpRequestHandlerLoader;
import com.altas.gateway.permission.PermissionManage;
import com.altas.gateway.session.Session;
import com.altas.gateway.session.SessionManager;
import com.altas.gateway.session.SessionState;
import com.alr.gateway.tars.globalservants.GlobalServantsConst;
import com.altas.gateway.utils.HttpUtils;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by zhangy on 2017/7/6.
 *
 */
public class HttpInvokerHandler {

    private static final String cookieFormatString = (HttpUtils.HTTP_SESSION_KEY+"=%s;Path=/door/;HttpOnly");

    public static HttpResponse invoke(FullHttpRequest fullHttpRequest) throws UnsupportedEncodingException {

        HttpResponseStatus responseStatus = HttpResponseStatus.OK;
        AsciiString responseContentType = HttpHeaderValues.TEXT_PLAIN;
        byte[] bytesBody={};

        boolean replySession = false;
        Session session = null;
        try {
            UrlInvoker invoker = HttpRequestHandlerLoader.getInstance().getInvokerByMethodAndUrl(fullHttpRequest.method().name(), HttpUtils.getURI(fullHttpRequest));
            if (null == invoker) {
                throw new UrlInvokerNotFoundException(HttpUtils.getURI(fullHttpRequest));
            }

            LatestSession latestSession = refreshSession(HttpUtils.getSessionID(fullHttpRequest));
            replySession=latestSession.isReplySessionID();
            session=latestSession.getSession();

             Object result;
            int error = PermissionManage.instance().hasPermission(invoker.getPermissionConstraint(), session);
            if (error == GlobalServantsConst.ERROR_CODE_OK) {//有权限就执行方法
                result = executeByHttpRequest(invoker, fullHttpRequest, session);
            } else {
                result = new ResultDictionary(error);
                if(error == GlobalServantsConst.ERROR_CODE_USER_LOGIN_IN_OTHER_PLACE){
                    if(null != session) //清除当前的session
                        clearCurrSessionBySessionId(session.getSessionId());
                }
            }

            MimeType mimeType = invoker.getProduceConstraint().getMimeType();
            if (mimeType == MimeType.JSON) {
                bytesBody = JsonHelper.allToJson(result).getBytes("UTF-8");
                responseContentType = HttpHeaderValues.APPLICATION_JSON;
            } else if (mimeType == MimeType.TEXT) {
                bytesBody = result.toString().getBytes("UTF-8");
            }else if (mimeType == MimeType.IMGJPEG){
                ByteArrayOutputStream imgByteArray = (ByteArrayOutputStream)result;
                bytesBody = imgByteArray.toByteArray();
                imgByteArray.close();
                responseContentType = new AsciiString("image/jpeg; charset=UTF-8");
            }

        } catch (UrlInvokerNotFoundException e){
            responseStatus = HttpResponseStatus.NOT_FOUND;
            bytesBody = getMessageBodyFromException(e);
        } catch (Exception e) {
            responseStatus = HttpResponseStatus.INTERNAL_SERVER_ERROR;
            bytesBody = getMessageBodyFromException(e);
        }

        /**组装需要返回的数据*/
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseStatus, Unpooled.wrappedBuffer(bytesBody));

        if (replySession) {
            String cookie = fullHttpRequest.headers().get(HttpHeaderNames.COOKIE);
            if(null == cookie || cookie.trim().isEmpty() || !cookie.contains(HttpUtils.HTTP_SESSION_KEY))
                response.headers().add(HttpHeaderNames.SET_COOKIE, String.format(cookieFormatString,session.getSessionId()));
        }
        response.headers().set(HttpHeaderNames.CONTENT_ENCODING, "UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, responseContentType);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

        return response;
    }

    private static byte[] getMessageBodyFromException(Exception e) {
        String errorMessage = e.getMessage();
        if (null == errorMessage) {errorMessage = "message body is null";}
        try {
            return errorMessage.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        return null;
    }

    private static Object executeByHttpRequest(UrlInvoker invoker, FullHttpRequest fullHttpRequest, Session session) throws ParamLackException {

        String uri = fullHttpRequest.uri();
        try {
            Map<String, String> queryParamDict = HttpUtils.getQueryParamDictFromRequestUri(uri);
            String contentType = fullHttpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE);

            String body = HttpUtils.getBodyString(fullHttpRequest, fullHttpRequest.content());
            if (null != contentType && contentType.contains(MimeType.URLENC.getType())) {
                queryParamDict.putAll(HttpUtils.getQueryParamDictFromString(body));
            }

            /**从Session里面取出部分参数放入这里面*/
            if(null != session) {
                queryParamDict.put(CONST.KEY_SESSION, session.getSessionId());
                String userId = session.getUserId();
                if (null != userId && !userId.trim().isEmpty()) {
                    queryParamDict.put(CONST.KEY_USER_ID, userId);
                }
            }

            Map<String, String> pathParamDict = HttpUtils.getPathParamDictFromRequestUriAndPattern(uri, invoker.getRegexUrl());
            //获取方法里面对应的查询参数
            Object[] params = invoker.reformParamsFromInvokeParamsAndHeadersAndBody(queryParamDict, pathParamDict, fullHttpRequest.headers(), body);

            return invoker.getMethod().invoke(invoker.getObject(), params);
        }catch (UnsupportedEncodingException e){
            LoggerHelper.error(e.getMessage(), e);
            return new ResultDictionary(GlobalServantsConst.ERROR_CODE_UNKNOWN);
        } catch (InvocationTargetException | IllegalAccessException e) {
            LoggerHelper.error(e.getMessage(), e);
            return new ResultDictionary(GlobalServantsConst.ERROR_CODE_UNKNOWN);
        }
    }

    private static void clearCurrSessionBySessionId(String sessionId) {
        SessionManager.instance().clearBySessionId(sessionId);
    }


    private static LatestSession refreshSession(String sessionId) {
        //记录当前session并将当前session头放入headers
        LatestSession result = new LatestSession();
        Session session = null;
        boolean sendSession = false;

        if (null == sessionId) {
            sendSession = true;
            session = new Session(SessionManager.generateSessionId());
        } else {
            SessionState sessionState = SessionManager.instance().validateSessionState(sessionId);
            if (sessionState == SessionState.NOT_LOGIN) {
                session = SessionManager.instance().getSessionBySessionId(sessionId);
            } else if (sessionState == SessionState.TIME_OUT) {
                result.setTimeOut(true);
                sendSession = true;
                session = new Session(sessionId);
            } else if (sessionState == SessionState.LOGIN_AT_OTHER_PLACE) {
                session = SessionManager.instance().getSessionBySessionId(sessionId);
                session.setSessionState(SessionState.LOGIN_AT_OTHER_PLACE);
            } else if (sessionState == SessionState.LOGIN)
                session = SessionManager.instance().getSessionBySessionId(sessionId);
        }
        SessionManager.instance().refreshSession(session);
        result.setReplySessionID(sendSession);
        result.setSession(session);
        return result;
    }

    static class LatestSession {
        private Session session;
        private boolean replySessionID = false;
        private boolean timeOut = false;

        public Session getSession() {
            return session;
        }

        public void setSession(Session session) {
            this.session = session;
        }

        public boolean isReplySessionID() {
            return replySessionID;
        }

        public void setReplySessionID(boolean replySessionID) {
            this.replySessionID = replySessionID;
        }

        public boolean isTimeOut() {
            return timeOut;
        }

        public void setTimeOut(boolean timeOut) {
            this.timeOut = timeOut;
        }
    }
}

package com.altas.gateway.session;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session implements Serializable {
    private String sessionId;
    private String userName;

    private SessionState sessionState;
    private List<String> permissionList;
    private Map<String,Object> attributeMap =new ConcurrentHashMap<>();

    private String userId;
    private int gender;

    public Session(String sessionId) {
        this.sessionId = sessionId;
        sessionState = SessionState.NOT_LOGIN;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public SessionState getSessionState() {
        return sessionState;
    }

    public void setSessionState(SessionState sessionState) {
        this.sessionState = sessionState;
    }

    public List<String> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
    }

    public void addAttribute(String key,Object value){
        attributeMap.put(key,value);
    }

    public void removeAttribute(String key){
        attributeMap.remove(key);
    }

    public Object getAttribute(String key){
        return attributeMap.get(key);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}

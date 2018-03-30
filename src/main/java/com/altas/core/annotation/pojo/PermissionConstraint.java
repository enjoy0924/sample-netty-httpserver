package com.altas.core.annotation.pojo;

import java.util.Map;

public class PermissionConstraint {

    /**
     * 权限map,考虑到权限有多个共用作用的情况，使用map来装，key为权限注入的名称，value为权限对应的反射类
     */
    private Map<String,String> permissionMap;

    /**
     * 多个权限共同作用的分隔符（|或者&，其他暂不支持），比如alr:um:authz|alr:vip:game,表示这两个任一个权限满足即可
     * 而alr:um:authz&alr:vip:game，表示这两个共同作用才会有效果
     */
    private String multiPermissionSeparator;


    public Map<String, String> getPermissionMap() {
        return permissionMap;
    }

    public void setPermissionMap(Map<String, String> permissionMap) {
        this.permissionMap = permissionMap;
    }

    public String getMultiPermissionSeparator() {
        return multiPermissionSeparator;
    }

    public void setMultiPermissionSeparator(String multiPermissionSeparator) {
        this.multiPermissionSeparator = multiPermissionSeparator;
    }





}

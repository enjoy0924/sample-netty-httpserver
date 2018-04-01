package com.altas.gateway.permission;

import com.altas.core.annotation.pojo.PermissionConstraint;
import com.altas.gateway.loader.GlobalConfig;
import com.altas.gateway.session.Session;
import com.altas.gateway.tars.globalservants.GlobalServantsConst;

import java.util.List;


public class PermissionManage {

    private static final PermissionManage permissionManage = new PermissionManage();

    public static PermissionManage instance() {
        return permissionManage;
    }

    /**
     * 根据权限约束类判断是否有权限
     *
     * @param permissionConstraint
     * @param session
     * @return
     */
    public int hasPermission(PermissionConstraint permissionConstraint, Session session) {

        List<String> permissions = permissionConstraint.getPermissions();
        if (null == permissions) {
            return GlobalServantsConst.ERROR_CODE_OK;
        }

        int error = GlobalServantsConst.ERROR_CODE_OK;
        try {
            for (String permission : permissions) {
                Permission permissionInvoke = GlobalConfig.instance().getPermissionInvokerByPermission(permission);
                if(null == permissionInvoke) {
                    error = GlobalServantsConst.ERROR_CODE_OK;
                }else {
                    error = permissionInvoke.validatePermission(session);
                }
            }

        } catch (Exception e) {
            error = GlobalServantsConst.ERROR_CODE_UNKNOWN;
        }

        return error;
    }
}

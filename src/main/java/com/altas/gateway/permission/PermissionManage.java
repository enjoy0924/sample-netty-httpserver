package com.altas.gateway.permission;

import com.altas.core.annotation.pojo.PermissionConstraint;
import com.altas.gateway.constant.CONST;
import com.altas.gateway.session.Session;
import com.alr.gateway.tars.globalservants.GlobalServantsConst;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Dengxl on 2017/7/14.
 */
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

        Map<String, String> permissionMap = permissionConstraint.getPermissionMap();

        if (null == permissionMap) {
            return GlobalServantsConst.ERROR_CODE_OK;
        }

        int error = GlobalServantsConst.ERROR_CODE_OK;
        try {
            for (Map.Entry<String, String> map : permissionMap.entrySet()) {
                Class clazz = Class.forName(map.getValue());
                Object instance = clazz.newInstance();
                Method method = instance.getClass().getDeclaredMethod(CONST.PERMISSION_VALIDATE_METHOD_NAME, Session.class);
                error = (int) method.invoke(instance, session);
                if(null==permissionConstraint.getMultiPermissionSeparator()){
                    //如果不是复合型的权限设定，直接就可以退出了
                    break;
                }
                //如果权限是以“|”分割，只要当前遍历过程中有一个为true，就满足返回。
                if (CONST.MULTI_PERMISSION_PEPARATOR_OR.equals(permissionConstraint.getMultiPermissionSeparator()) ) {
                    if(error == GlobalServantsConst.ERROR_CODE_OK)
                        break;
                } else if (CONST.MULTI_PERMISSION_PEPARATOR_ADD.equals(permissionConstraint.getMultiPermissionSeparator())) {
                    if(error != GlobalServantsConst.ERROR_CODE_OK)
                        break;
                } else {//表示map只有一个或者上面两种情况的最终结果
                    break;
                }
            }

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |InvocationTargetException e) {
            error = GlobalServantsConst.ERROR_CODE_UNKNOWN;
        }

        return error;
    }
}

package com.alr.gateway.permission;

import com.alr.gateway.session.Session;
import com.alr.gateway.tars.globalservants.GlobalServantsConst;

/**
 * Created by zhangy on 2017/7/13.
 */
public class GamePermissionImpl implements Permission {
    @Override
    public int validatePermission(Session session) {
        //todo(dxl):校验游戏包权限
        return GlobalServantsConst.ERROR_CODE_OK;
    }
}

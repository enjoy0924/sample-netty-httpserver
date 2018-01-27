package com.alr.gateway.permission;

import com.alr.gateway.session.Session;
import com.alr.gateway.tars.globalservants.GlobalServantsConst;

/**
 * Created by Dengxl on 2017/7/14.
 */
public class OlympicMathPermissionImpl implements Permission {
    @Override
    public int validatePermission(Session session) {
        //todo(dxl):校验奥数权限
        return GlobalServantsConst.ERROR_CODE_OK;
    }
}

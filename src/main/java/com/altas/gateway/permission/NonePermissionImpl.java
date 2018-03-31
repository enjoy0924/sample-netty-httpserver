package com.altas.gateway.permission;

import com.altas.gateway.session.Session;
import com.altas.gateway.tars.globalservants.GlobalServantsConst;


public class NonePermissionImpl implements Permission {
    @Override
    public int validatePermission(Session session) {
        return GlobalServantsConst.ERROR_CODE_OK;
    }
}

package com.alr.gateway.permission;

import com.alr.gateway.session.Session;
import com.alr.gateway.tars.globalservants.GlobalServantsConst;

/**
 * Created by zhangy on 2017/7/13.
 */
public class NonePermissionImpl implements Permission {
    @Override
    public int validatePermission(Session session) {
        return GlobalServantsConst.ERROR_CODE_OK;
    }
}

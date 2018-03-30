package com.altas.gateway.permission;

import com.altas.gateway.session.Session;
import com.altas.gateway.session.SessionManager;
import com.alr.gateway.tars.globalservants.GlobalServantsConst;

/**
 * Created by G_dragon on 2017/9/14.
 */
public class LogOutPermissionImpl implements Permission {
    @Override
    public int validatePermission(Session session) {
        SessionManager.instance().clearBySessionId(session.getSessionId());
        return GlobalServantsConst.ERROR_CODE_OK;
    }
}

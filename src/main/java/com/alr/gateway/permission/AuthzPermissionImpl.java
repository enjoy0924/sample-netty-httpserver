package com.alr.gateway.permission;

import com.alr.gateway.session.Session;
import com.alr.gateway.session.SessionState;
import com.alr.gateway.tars.globalservants.GlobalServantsConst;

/**
 * Created by zhangy on 2017/7/13.
 */
public class AuthzPermissionImpl implements Permission {
    @Override
    public int validatePermission(Session session) {
        if(null == session || null == session.getUserId() || session.getUserId().isEmpty())
            return GlobalServantsConst.ERROR_CODE_USER_SESSION_HAS_LOST;

        SessionState sessionState = session.getSessionState();
        if(sessionState.equals(SessionState.LOGIN_AT_OTHER_PLACE))
            return GlobalServantsConst.ERROR_CODE_USER_LOGIN_IN_OTHER_PLACE;

        if(sessionState.equals(SessionState.LOGIN))
            return GlobalServantsConst.ERROR_CODE_OK;
        else
            return GlobalServantsConst.ERROR_CODE_UNKNOWN;
    }
}

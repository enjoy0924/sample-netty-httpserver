package com.alr.gateway.permission;

import com.alr.gateway.session.Session;

/**
 * Created by zhangy on 2017/7/13.
 */
public interface Permission {
    int validatePermission(Session session);
}

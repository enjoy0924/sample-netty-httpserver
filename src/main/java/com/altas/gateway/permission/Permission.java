package com.altas.gateway.permission;

import com.altas.gateway.session.Session;


public interface Permission {
    int validatePermission(Session session);
}

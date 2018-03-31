package com.altas.api;

import com.altas.core.annotation.restful.*;
import com.altas.core.annotation.restful.enumeration.HttpMethod;
import com.altas.core.annotation.restful.enumeration.MimeType;
import com.altas.gateway.constant.CONST;

import java.util.HashMap;
import java.util.Map;


@Api(tag = "entry")
@Url("/user")
public class LoginApi {

    @Permission(value = CONST.PERMISSION_NONE)
    @Url(value = "/login")
    @Consumer(method = HttpMethod.POST, type = MimeType.URLENC)
    @Producer(type = MimeType.JSON)
    public Map<String, Object> login(@FormParam(value = "loginName", required = true) String username,
                                     @FormParam(value = "password", required = false) String password,
                                     @HeaderParam(value = "session", required = true) String sessionId,
                                     @HeaderParam(value = "User-Agent", required = false) String userAgent) {

        /**这里获取登录的操作系统版本，供统计分析使用*/
        return new HashMap<>();
    }
}

package com.altas.api;

import com.altas.core.annotation.restful.*;
import com.altas.core.annotation.restful.enumeration.HttpMethod;
import com.altas.core.annotation.restful.enumeration.MimeType;
import com.altas.gateway.constant.CONST;
import com.altas.gateway.schema.Response;
import com.altas.gateway.session.Session;
import com.altas.gateway.session.SessionManager;
import com.altas.gateway.session.SessionState;
import com.altas.gateway.tars.globalservants.GlobalServantsConst;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Api(tag = "entry", description = "负责用户的注册登录等信息")
@Url("/user")
public class LoginApi {

    @Permission(value = CONST.PERMISSION_NONE)
    @Url(value = "/login", summary = "login", description = "用户登录")
    @Consumer(method = HttpMethod.POST, type = MimeType.URLENC)
    @Producer(type = MimeType.JSON)
    @ApiResponses({
            @ApiResponse(responseCode = "0x1000", description = "success full",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/Account"))
            }),
            @ApiResponse(responseCode = "0x1001", description = "session time out"),
            @ApiResponse(responseCode = "0x1002", description = "login at other place")
     })
    public Response login(@FormParam(value = "loginName", required = true) String username,
                                     @FormParam(value = "password", required = true) String password,
                                     @SessionAttr(value = CONST.SYS_AUTO_INJECT_PARAM_KEY_SESSION_ID, required = false) String sessionId,
                                     @HeaderParam(value = "User-Agent", required = false) String userAgent) {

        Session session = SessionManager.instance().getSessionBySessionId(sessionId);

        //成功之后刷新Session的值
        session.setSessionState(SessionState.LOGIN);
        SessionManager.instance().refreshSession(session);


        /**这里获取登录的操作系统版本，供统计分析使用*/
        return new Response(GlobalServantsConst.ERROR_CODE_OK);
    }
}

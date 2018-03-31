package com.altas.gateway.session;

import com.altas.cache.redis.JedisTemplate;
import com.altas.gateway.utils.SerializeHelper;

import java.util.UUID;

public class SessionManager {

    public static SessionManager instance() {
        return new SessionManager();
    }


    private final String sessionPrefix = "session";
    private final String loginPrefix = "loginState";

    /**
     * 随机生成一个新的SessionId
     *
     * @return
     */
    public static String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 根据sessionId获取session
     *
     * @param sessionId
     * @return
     */
    public Session getSessionBySessionId(String sessionId) {
        try {
            String subject = JedisTemplate.instance().get(sessionPrefix, sessionId);
            return (Session) SerializeHelper.deserialize(subject);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 刷新Session的生命周期
     *
     * @param session
     * @return
     */
    public boolean refreshSession(Session session) {
        JedisTemplate.instance().set(sessionPrefix, session.getSessionId(), SerializeHelper.serialize(session));

        if (session.getSessionState() == SessionState.LOGIN) {
            JedisTemplate.instance().set(loginPrefix, session.getUserName(), session.getSessionId());
        }
        return true;
    }

    /**
     * 校验Session状态
     *
     * @param sessionId
     * @return
     */
    public SessionState validateSessionState(String sessionId) {
        Session session = getSessionBySessionId(sessionId);
        if (null == session) {
            return SessionState.TIME_OUT;
        } else if (SessionState.LOGIN == session.getSessionState()) {
            return checkSessionState(session);
        } else {
            return session.getSessionState();
        }
    }

    private SessionState checkSessionState(Session session) {
        //登陆表里面会记录所有已登陆账户的session  如果sessionID不一样  被其他用户登陆  超级用户不受限制
        String loginSessionID = JedisTemplate.instance().get(loginPrefix, session.getUserName());
        if (null == loginSessionID) {
            JedisTemplate.instance().set(loginPrefix, session.getUserName(), session.getSessionId());
            return SessionState.LOGIN;
        } else {
            if (loginSessionID.equals(session.getSessionId())) {
                return SessionState.LOGIN;
            } else {
                return SessionState.LOGIN_AT_OTHER_PLACE;
            }
        }
    }

    public void clearBySessionId(String sessionId) {
        JedisTemplate.instance().deleteWithPrefix(sessionPrefix, sessionId);
    }
}

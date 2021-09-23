package com.example.demo.config.session;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.demo.config.realm.CustomPrincipal;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 自定义sessionDao，实现分布式session
 */
@Component
public class CustomSessionDao extends AbstractSessionDAO {
    static final String PRINCIPALS_SESSION_KEY = "org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY";

    static final String AUTHENTICATED_SESSION_KEY = "org.apache.shiro.subject.support.DefaultSubjectContext_AUTHENTICATED_SESSION_KEY";


    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        storeSession(sessionId, session);
        return sessionId;
    }

    private void storeSession(Serializable sessionId, Session session) {
        /**
         * 考虑可读性，所以使用json形式。
         * <br/>
         * json反序列化为session时，principal有问题，所以将其组织为realmMapPrincipals。
         */
        SimpleSession simpleSession = (SimpleSession) session;
        CustomSimpleSession customSimpleSession = new CustomSimpleSession();
        BeanUtils.copyProperties(simpleSession, customSimpleSession);

        Map<String, List<CustomPrincipal>> realmMapPrincipals = getRealmMapPrincipals(session);
        Boolean authenticatedSessionKeyVal = getAuthenticatedSessionKeyVal(session);

        if (realmMapPrincipals != null) {
            customSimpleSession.setRealmMapPrincipals(realmMapPrincipals);
        }

        if (authenticatedSessionKeyVal != null) {
            customSimpleSession.setAuthenticatedSessionKeyVal(authenticatedSessionKeyVal);
        }

        SessionUtils.save(sessionId.toString(), customSimpleSession);
    }

    /**
     * 获取session中的principals
     */
    static Map<String, List<CustomPrincipal>> getRealmMapPrincipals(Session session) {
        Map<String, List<CustomPrincipal>> reamlMapPricipals = null;

        SimpleSession storeSession = (SimpleSession)session ;
        Object attribute = storeSession.getAttribute(PRINCIPALS_SESSION_KEY);
        if (null != attribute) {
            SimplePrincipalCollection simplePrincipalCollection = (SimplePrincipalCollection)attribute;
            Set<String> realmNames = simplePrincipalCollection.getRealmNames();
            if (!CollectionUtils.isEmpty(realmNames)) {
                reamlMapPricipals = new HashMap<>();
                for (String realmName : realmNames) {
                    Collection collection = simplePrincipalCollection.fromRealm(realmName);
                    Object collect = collection.stream().collect(Collectors.toList());
                    reamlMapPricipals.put(realmName, (List<CustomPrincipal>) collect);
                }
            }
        }

        return  reamlMapPricipals;
    }

    /**
     * 更新权限信息
     * @param sessionId
     * @param authorizationInfo
     */
    public static void updateAuthorizationInfo(String sessionId, SimpleAuthorizationInfo authorizationInfo) {
        CustomSimpleSession customSimpleSession = SessionUtils.getCustomSession(sessionId.toString());
        customSimpleSession.getRealmMapPrincipals().values().stream().findFirst().get().get(0).setAuthorizationInfo(authorizationInfo);
        SessionUtils.save(sessionId, customSimpleSession);
    }

    static Boolean getAuthenticatedSessionKeyVal(Session session){
        Boolean attribute = (Boolean) session.getAttribute(AUTHENTICATED_SESSION_KEY);
        return attribute;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        CustomSimpleSession customSimpleSession = SessionUtils.getCustomSession(sessionId.toString());
        if (null == customSimpleSession) {
            return null;
        }

        SimpleSession simpleSession = new SimpleSession();
        BeanUtils.copyProperties(customSimpleSession, simpleSession);

        fillSession(simpleSession, customSimpleSession);

        return simpleSession;
    }

    /**
     * 填充 cache的attribute
     */
    static void fillSession(Session session, CustomSimpleSession customSimpleSession) {
        if (session != null) {
            SimplePrincipalCollection simplePrincipalCollection = (SimplePrincipalCollection) session.getAttribute(PRINCIPALS_SESSION_KEY);

            if (null == simplePrincipalCollection) {
                simplePrincipalCollection = new SimplePrincipalCollection();
            }
            simplePrincipalCollection.clear();

            Map<String, List<CustomPrincipal>> realmMapPrincipals = customSimpleSession.getRealmMapPrincipals();
            if (null != realmMapPrincipals) {
                for (String realm : realmMapPrincipals.keySet()) {
                    simplePrincipalCollection.addAll(realmMapPrincipals.get(realm), realm);
                }
            }

            session.setAttribute(PRINCIPALS_SESSION_KEY, simplePrincipalCollection);

            if (customSimpleSession.getAuthenticatedSessionKeyVal() != null) {
                session.setAttribute(AUTHENTICATED_SESSION_KEY, customSimpleSession.getAuthenticatedSessionKeyVal());
            }
        }
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        storeSession(session.getId(), session);
    }

    @Override
    public void delete(Session session) {
        SessionUtils.del(session.getId().toString());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return null;
    }
}

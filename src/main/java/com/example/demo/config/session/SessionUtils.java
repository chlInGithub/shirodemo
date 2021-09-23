package com.example.demo.config.session;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.demo.config.realm.CustomPrincipal;
import org.springframework.util.StringUtils;

/**
 * session 读取的封装类，在内部使用分布式解决方案。
 */
public class SessionUtils {

    // TODO 假设存储分布式session
    private static Map<String, String> distributedSession = new ConcurrentHashMap<>();

    private static final String KEY_PREFIX = "session:";

    static {
        ParserConfig.getGlobalInstance().addAccept("com.example.demo.config.realm.CustomPrincipal");

        // 用于测试分布式情况
        distributedSession.put("session:cdc182ef-ff82-4753-95b1-bf2a9729d147",
                "{\"authenticatedSessionKeyVal\":true,\"expired\":false,\"host\":\"0:0:0:0:0:0:0:1\",\"id\":\"cdc182ef-ff82-4753-95b1-bf2a9729d147\",\"lastAccessTime\":1632369785331,\"realmMapPrincipals\":{\"customAuthenticatingRealm\":[{\"authorizationInfo\":{\"stringPermissions\":[\"/demo/testPerm\",\"/demo/testRole\"]},\"username\":\"user\"}]},\"startTimestamp\":1632369746262,\"timeout\":1800000}");
    }

    public static void save(String sessionId, Map<String, List<CustomPrincipal>> object) {
        String key = getKey(sessionId);
        String jsonString = JSONObject.toJSONString(object, SerializerFeature.WriteClassName, SerializerFeature.WriteDateUseDateFormat);
        System.out.println(key + "  " + jsonString);
        distributedSession.put(key, jsonString);
    }

    public static Map<String, List<CustomPrincipal>> get(String sessionId) {
        String key = getKey(sessionId);
        String jsonString = distributedSession.get(key);
        if (!StringUtils.hasLength(jsonString)) {
            return null;
        }
        Map<String, List<CustomPrincipal>> cache = (Map<String, List<CustomPrincipal>>) JSONObject.parseObject(jsonString, Map.class);

        return cache;
    }

    public static void del(String sessionId) {
        String key = getKey(sessionId);
        distributedSession.remove(key);
    }

    static String getKey(String sessionId) {
        return KEY_PREFIX + sessionId;
    }
    public static void save(String sessionId, CustomSimpleSession customSimpleSession) {
        String key = getKey(sessionId);
        String jsonString = JSONObject.toJSONString(customSimpleSession);
        System.out.println(key + "  " + jsonString);
        distributedSession.put(key, jsonString);
    }

    public static CustomSimpleSession getCustomSession(String sessionId) {
        String json = distributedSession.get(getKey(sessionId));
        if (!StringUtils.hasLength(json)) {
            return null;
        }

        CustomSimpleSession customSimpleSession = JSONObject.parseObject(json, CustomSimpleSession.class);

        return customSimpleSession;
    }
}

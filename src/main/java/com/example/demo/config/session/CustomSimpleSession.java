package com.example.demo.config.session;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.demo.config.realm.CustomPrincipal;
import lombok.Data;

/**
 * 由于SimpleSession中field都是临时的，所以需要copy后持久化。
 */
@Data
public class CustomSimpleSession {
    Serializable id;
    Date startTimestamp;
    Date stopTimestamp;
    Date lastAccessTime;
    long timeout;
    boolean expired;
    String host;

    Map<String, List<CustomPrincipal>> realmMapPrincipals;
    Boolean authenticatedSessionKeyVal;

}

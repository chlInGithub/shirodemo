package com.example.demo.config.realm;

import lombok.Data;
import org.apache.shiro.authz.SimpleAuthorizationInfo;

/**
 * 自定义的当事人
 */
@Data
public class CustomPrincipal {
    String username;

    /**
     * 权限信息
     */
    SimpleAuthorizationInfo authorizationInfo;
}

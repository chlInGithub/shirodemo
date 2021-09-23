package com.example.demo.config.realm;

import java.util.HashSet;
import java.util.Set;

import com.example.demo.config.session.CustomSessionDao;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.stereotype.Component;

/**
 * 自定义realm，很诡异的一点，如果该类使用@Component，则出现异常，位置为 AbstractShiroConfiguration.java:70 securityManager.setAuthorizer(authorizer());
 * <br/>
 * 所以注释了@Component
 */
//@Component
public class CustomAuthorizingRealm extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        CustomPrincipal primaryPrincipal = (CustomPrincipal)principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = primaryPrincipal.getAuthorizationInfo();
        if (authorizationInfo == null) {

            // TODO 根据principal去查询角色权限

            authorizationInfo = new SimpleAuthorizationInfo();

            Set<String> perms = new HashSet<>();
            perms.add("/demo/testPerm");
            perms.add("/demo/testRole");
            authorizationInfo.setStringPermissions(perms);

            // 更新到分布式session中
            Session session = SecurityUtils.getSubject().getSession();
            CustomSessionDao.updateAuthorizationInfo(session.getId().toString(), authorizationInfo);
        }

        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String username = usernamePasswordToken.getUsername();
        // TODO 根据username去查询用户信息

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo();
        SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();
        CustomPrincipal principal = new CustomPrincipal();
        principal.setUsername("user");
        principalCollection.add(principal, "customAuthenticatingRealm");
        authenticationInfo.setPrincipals(principalCollection);
        authenticationInfo.setCredentials("password");
        return authenticationInfo;
    }
}

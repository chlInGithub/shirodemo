package com.example.demo.config;

import javax.servlet.Filter;

import com.example.demo.config.filter.UrlPermAccessControlFilter;
import com.example.demo.config.realm.CustomAuthorizingRealm;
import com.example.demo.config.session.CustomSessionDao;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomShiroConfig {

    /**
     * 使用filter，进行url授权验证
     */
    @Bean
    public Filter urlPermAccessControlFilter() {
        return new UrlPermAccessControlFilter();
    }

    @Bean
    SessionDAO sessionDAO() {
        return new CustomSessionDao();
    }

    @Bean
    protected SessionManager sessionManager(SessionDAO sessionDAO) {
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        defaultWebSessionManager.setSessionDAO(sessionDAO);
        return defaultWebSessionManager;
    }

    /**
     * 自定义realm
     */
    @Bean
    public Realm authorizingRealm() {
        CustomAuthorizingRealm customAuthorizingRealm = new CustomAuthorizingRealm();
        return customAuthorizingRealm;
    }

    /**
     * 定义 url-pattern 对应的 shiro filter 类型
     */
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        /*
         * 按照添加顺序进行匹配，建议之前的urlPattern不要包含之后的urlPattern
         */

        // 使用authc、roles filters， 即需要登录，且角色admin
        //chainDefinition.addPathDefinition("/demo/testRole", "authc, roles[admin]");
        //chainDefinition.addPathDefinition("/demo/testPerm", "authc, perms[testPerm]");

        // 使用authc、logout filters，需要登录
        chainDefinition.addPathDefinition("/logout", "authc, logout");

        // 使用authc filters，需要登录，不做授权验证
        chainDefinition.addPathDefinition("/**", "authc");

        return chainDefinition;
    }
}

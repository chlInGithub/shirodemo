package com.example.demo.config.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * url path 作为权限，根据url进行权限验证
 */
public class UrlPermAccessControlFilter extends OncePerRequestFilter implements Ordered {

    /**
     * 放在shiroFilter之后
     */
    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().endsWith(".html");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        Subject subject = SecurityUtils.getSubject();
        String requestURI = request.getRequestURI();
        boolean permitted = subject.isPermitted(requestURI);
        if (!permitted) {
            response.sendRedirect("/unauthorized.html");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

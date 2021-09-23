package com.example.demo.config;

import java.io.IOException;
import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 列举自定义filter的方式。
 * <br/>
 * 了解自动装配filter的过程，如下为debug地方。
 * <br/>
 * org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext#selfInitialize(javax.servlet.ServletContext)
 * <br/>
 * <ul>
 *     <li>从beanFactory获取bean集合，类型为org.springframework.boot.web.servlet.ServletContextInitializer</li>
 *     <li>org.springframework.boot.web.servlet.FilterRegistrationBean 正是 ServletContextInitializer 的子类</li>
 *     <li>调用ServletContextInitializer#onStartup</li>
 *     <li>注册到ServletContext中 org.springframework.boot.web.servlet.AbstractFilterRegistrationBean#addRegistration(java.lang.String, javax.servlet.ServletContext)</li>
 *     <li>org.apache.catalina.core.ApplicationContext#addFilter(java.lang.String, java.lang.String, javax.servlet.Filter)</li>
 * </ul>
 *
 * <br/>
 * 方式1已经返回FilterRegistrationBean实例；方式2返回filter实例，但会被包装为FilterRegistrationBean实例
 */
//@Configuration
public class CustomFilters {

    /**
     * 方式1
     * <br/>
     * FilterRegistrationBean方式，可以修改 自定义filter 的配置
     */
    @Bean
    protected FilterRegistrationBean<CustomFilter> customFilterFilterRegistrationBean() throws Exception {

        FilterRegistrationBean<CustomFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR);
        filterRegistrationBean.setFilter(new CustomFilter());
        filterRegistrationBean.setName("customFilter");
        filterRegistrationBean.setOrder(1);
        // filterRegistrationBean.setUrlPatterns();

        return filterRegistrationBean;
    }

    /**
     * 方式2
     * <br/>
     * 直接反回自定义filter实例
     */
    @Bean
    public CustomFilter customFilter1(){
        CustomFilter customFilter = new CustomFilter();
        return customFilter;
    }

    public static class CustomFilter extends OncePerRequestFilter implements Ordered {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                FilterChain filterChain) throws ServletException, IOException {
            System.out.println("=======CustomFilter======" + getOrder());

            filterChain.doFilter(request, response);

        }

        @Override
        public int getOrder() {
            return Integer.MIN_VALUE;
        }
    }
}

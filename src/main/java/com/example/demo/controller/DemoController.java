package com.example.demo.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource
    HttpServletRequest httpServletRequest;

    @GetMapping("/t1")
    public String t1(){
        System.out.println(httpServletRequest.getRequestURI());
        System.out.println(httpServletRequest.getServletPath());
        System.out.println(httpServletRequest.getPathInfo());
        return "t1";
    }
    @GetMapping("/testRole")
    public String testRole(){
        /*Subject subject = SecurityUtils.getSubject();
        boolean admin = subject.hasRole("admin");
        if (!admin) {
            return "no role";
        }*/
        return "t1";
    }

    @GetMapping("/testPerm")
    public String testPerm(){
        /*Subject subject = SecurityUtils.getSubject();
        boolean admin = subject.hasRole("admin");
        if (!admin) {
            return "no role";
        }*/
        return "t1";
    }
}

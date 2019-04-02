package com.ji.test.controller;

import com.ji.spring.springmvc.annotation.RequestParam;
import org.w3c.dom.UserDataHandler;

import com.ji.spring.core.annotation.AutoWired;
import com.ji.spring.springmvc.annotation.Controller;
import com.ji.spring.springmvc.annotation.RequestMapping;
import com.ji.test.pojo.User;
import com.ji.test.service.TestService;

/**
 * @BelongsProject: 手写springmvc框架练习
 * @BelongsPackage: com.ji.test.controller
 * @Author: JiLiugang
 * @CreateTime: 2019-03-23 10:55
 * @Description:
 */
@Controller
@RequestMapping("test")
public class TestController {
    @AutoWired
    private TestService testService;
    @RequestMapping("demo")
    public void success(){
        System.out.println("访问成功！111111111111111111111");
    }
    @RequestMapping("user")
    public void user(@RequestParam("age")Integer i,@RequestParam("name") String name ,User user) {
    	System.out.println(user);
        System.out.println(i);
        System.out.println();
        System.out.println(name);
    }
}

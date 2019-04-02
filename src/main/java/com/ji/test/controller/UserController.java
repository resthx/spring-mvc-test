package com.ji.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ji.spring.core.annotation.AutoWired;
import com.ji.spring.springmvc.annotation.Controller;
import com.ji.spring.springmvc.annotation.RequestMapping;
import com.ji.test.pojo.User;
import com.ji.test.service.UserService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("user")
public class UserController {
    @AutoWired
    private UserService userService;
    @RequestMapping("findUser")
    public List<User> findUser(HttpServletResponse response) throws IOException {
        List<User> list = userService.findUser();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter writer = objectMapper.writer();
        /*String s = writer.writeValueAsString(user);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(s);*/
        return list;
    }
}

package com.ji.test.service;

import com.ji.spring.core.annotation.Component;
import com.ji.spring.core.annotation.Service;
import com.ji.test.pojo.User;

import java.util.ArrayList;
import java.util.List;

@Component("userService")
public class UserService {
    public List<User> findUser(){
        User user1 = new User();
        user1.setAge(23);
        user1.setName("测试1");
        User user2 = new User();
        user2.setAge(23);
        user2.setName("测试2");
        User user3 = new User();
        user3.setAge(55);
        user3.setName("测试3");
        List<User> list = new ArrayList<>();
        list.add(user1);
        list.add(user2);
        list.add(user3);
        return list;
    }
}

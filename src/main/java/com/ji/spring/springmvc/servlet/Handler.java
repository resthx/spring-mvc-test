package com.ji.spring.springmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public interface Handler {
    void invoke(Object instance, Method method, Object[] args, HttpServletRequest req, HttpServletResponse resp) throws Exception;
}

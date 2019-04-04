package com.ji.spring.springmvc.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @BelongsProject: 手写springmvc框架练习
 * @BelongsPackage: com.ji.spring.springmvc.servlet
 * @Author: JiLiugang
 * @CreateTime: 2019-03-30 15:32
 * @Description:
 */
public class HttpRequestHandler implements Handler{

    public void invoke(Object instance, Method method, Object[] args, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Object invoke = null;
        Class<?> type = method.getReturnType();
        try {
                invoke = method.invoke(instance, args);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.out.println("方法未声明public");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        if (invoke!=null){
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(invoke);
            resp.setHeader("Content-Type", "application/json;charset=UTF-8");
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().write(s);
        }
    }

}

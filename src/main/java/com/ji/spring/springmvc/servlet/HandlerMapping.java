package com.ji.spring.springmvc.servlet;

import java.lang.reflect.Method;

/**
 * @BelongsProject: 手写springmvc框架练习
 * @BelongsPackage: com.ji.spring.springmvc.servlet
 * @Author: JiLiugang
 * @CreateTime: 2019-03-23 17:19
 * @Description:
 */
public class HandlerMapping {
    private Object controller;
    private Method method;

    public HandlerMapping() {
    }

    public HandlerMapping(Object controller,Method method) {
        this.controller = controller;
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

}

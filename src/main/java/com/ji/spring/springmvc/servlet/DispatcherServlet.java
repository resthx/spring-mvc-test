package com.ji.spring.springmvc.servlet;

import com.ji.spring.core.annotation.AutoWired;
import com.ji.spring.core.annotation.Component;
import com.ji.spring.core.annotation.Service;
import com.ji.spring.springmvc.annotation.Controller;
import com.ji.spring.core.bean.SpringMvcBeanFactory;
import com.ji.spring.core.bean.BeanFactory;
import com.ji.spring.springmvc.annotation.RequestMapping;
import com.ji.spring.springmvc.annotation.RequestParam;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.KeyStore;
import java.util.*;

/**
 * @BelongsProject: 手写springmvc框架练习
 * @BelongsPackage: com.ji.springmvc.servlet
 * @Author: JiLiugang
 * @CreateTime: 2019-03-23 10:03
 * @Description:
 */
public class DispatcherServlet extends HttpServlet {
    private List<String> classNameList = new ArrayList<String>();
    private Map<String,HandlerMapping> handlerMappings = new HashMap<>();
    @Override
    public void init() throws ServletException {
        //扫描包下的类
        try {
            initClassNameList();
        } catch (FileNotFoundException e) {
            System.out.println("没有找到配置文件");
        }
        //初始化bean容器
        initBeans();
        //初始化属性注入
        initWired();
        //初始化HandlerMapping
        initHandlerMapping();
    }

    private void initHandlerMapping() {
        //遍历bean容器
        SpringMvcBeanFactory beanFactory = (SpringMvcBeanFactory) SpringMvcBeanFactory.getBeanFactory();
        Set<String> set = beanFactory.keySet();
        for (String key:set){
            Object bean = beanFactory.getBean(key);
            Class beanClass = bean.getClass();
            if (beanClass.isAnnotationPresent(Controller.class)){
                //如果是controller

                String url = "/";
                if (beanClass.isAnnotationPresent(RequestMapping.class)){
                    //判断类上RequestMapping路径
                    RequestMapping requestMapping = (RequestMapping) beanClass.getAnnotation(RequestMapping.class);
                    String tem = requestMapping.value();
                    url = url+tem+"/";
                }
                //判断方法上RequestMapping路径
                Method[] methods = beanClass.getDeclaredMethods();
                for (Method method:methods){
                    if (method.isAnnotationPresent(RequestMapping.class)){
                        RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
                        String s = methodAnnotation.value();
                        String url_tem = url;
                        url_tem += s;
                        url_tem.replaceAll("//", "/");
                        HandlerMapping handlerMapping = new HandlerMapping(bean,method);
                        handlerMappings.put(url_tem,handlerMapping);
                    }
                }
            }
        }
    }

    private Object[] searchParam(Method method, HttpServletRequest req, HttpServletResponse resp) {
        //获取方法上的参数类型并生成参数
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int j=0;j<parameterTypes.length;j++){
            String simpleName = parameterTypes[j].getSimpleName();
            switch (simpleName){
                case "HttpServletRequest":
                    args[j] = req;
                    break;
                case "HttpServletResponse":
                    args[j]=resp;
                    break;
                case "String":
                    RequestParam requestParam = getRequestParamAnnotationByIndex(parameterAnnotations,j);
                    String param_Name = requestParam.value();
                    String str = req.getParameter(param_Name);
                    if (!isEmpty(str)){
                        args[j] = str;
                    }
                    break;
                case "Integer":
                    RequestParam requestParam1 = getRequestParamAnnotationByIndex(parameterAnnotations,j);
                    String param_Name1 = requestParam1.value();
                    String parameter = req.getParameter(param_Name1);
                    if (!isEmpty(parameter)){
                        args[j] = (Integer)Integer.parseInt(parameter);
                    }
                    break;
                default:
                    for (String s:classNameList){
                        if (s.endsWith(simpleName)){
                            simpleName = s;
                        }
                    }
                    Class t = null;
                    try {
                         t = Class.forName(simpleName);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        System.out.println("未找到参数实体类");
                    }
                    Object o = null;
                    try {
                        o = t.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        System.out.println("实体类实例化失败");
                    }
                    Field[] declaredFields = t.getDeclaredFields();
                    for (Field field : declaredFields){
                        String filed_name = field.getName();
                        if (req.getParameter(filed_name)!=null||"".equals(req.getParameter(filed_name))){
                            field.setAccessible(true);
                            try {
                                field.set(o,req.getParameter(filed_name));
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                                System.out.println("实体类参数注入失败");
                            }
                        }
                    }
                    args[j] = o;
            }
        }
        return args;
    }

    private RequestParam getRequestParamAnnotationByIndex(Annotation[][] parameterAnnotations,int j) {
        for (Annotation annotation :parameterAnnotations[j]){
            Class<? extends Annotation> type = annotation.annotationType();
            if ("RequestParam".equals(type.getSimpleName())){
                return (RequestParam) annotation;
            }
        }
        return null;
    }
    //获取参数注解上索引对应参数的RequestMapping注解


    private void initWired() {
        SpringMvcBeanFactory beanFactory = (SpringMvcBeanFactory) SpringMvcBeanFactory.getBeanFactory();
        Set<String> set = beanFactory.keySet();
        for (String key:set){
            Object bean = beanFactory.getBean(key);
            Field[] fields = bean.getClass().getDeclaredFields();
            if (fields.length>0){
                for (Field field:fields){
                    if (field.isAnnotationPresent(AutoWired.class)){
                        String value = field.getAnnotation(AutoWired.class).value();
                        field.setAccessible(true);
                        try {
                            if (!"".equals(value)) {
                                field.set(bean, beanFactory.getBean(value));
                            }

                            field.set(bean, beanFactory.getBean(field.getName()));
                        }
                        catch (IllegalAccessException e) {
                            e.printStackTrace();
                            System.out.println("bean注入失败");
                        }
                    }
                }
            }
        }
    }
    private void initBeans() {
        //获取bean工厂
        BeanFactory beanFactory = SpringMvcBeanFactory.getBeanFactory();
        for (String className:classNameList){
            Class bean = null;
            try {
                bean = Class.forName(className);
            } catch (ClassNotFoundException e) {
                System.out.println("没有找到类");
            }
            if (bean.isAnnotationPresent(Controller.class)||bean.isAnnotationPresent(Service.class)){
                Object o = null;
                try {
                    o = bean.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    System.out.println("生成实例化对象失败");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                beanFactory.setBean(o);
            }
            if (bean.isAnnotationPresent(Component.class)){
                Component annotation = (Component) bean.getAnnotation(Component.class);
                Object o = null;
                try {
                    o = bean.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if ("".equals(annotation.value())){
                    beanFactory.setBean(o);
                }else {
                    String name = annotation.value();
                    beanFactory.setBeanByName(name,o);
                }
            }
        }
    }

    private void initClassNameList() throws FileNotFoundException {
        //获取配置文件
        URL resource = DispatcherServlet.class.getClassLoader().getResource("application.yaml");
        Yaml yaml = new Yaml();
        Map load = (Map) yaml.load(new FileInputStream(resource.getFile()));
        Map application = (Map) load.get("springmvc");
        Map scanPackage_map = (Map) application.get("application");
        String scanPackage = (String) scanPackage_map.get("scanPackage");
        scanClassList(scanPackage);
    }

    private void scanClassList(String scanPackage) {
        URL url = DispatcherServlet.class.getClassLoader().getResource(scanPackage.replaceAll("\\.","/"));
        File file = new File(url.getFile());
        File[] files = file.listFiles();
        for (File file_path:files){
            if (file_path.isDirectory()){
                scanClassList(scanPackage+"."+file_path.getName());
            }else if (file_path.getName().endsWith(".class")){
                classNameList.add(scanPackage+"."+file_path.getName().replace(".class", ""));
            }
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String requestURI = req.getRequestURI();
        HandlerMapping handlerMapping = handlerMappings.get(requestURI);
        if (handlerMapping==null){
            resp.getWriter().write("404 NOT FOUND");
            return;
        }
        Object instance = handlerMapping.getController();
        Method method = handlerMapping.getMethod();
        Object[] args = searchParam(method, req, resp);
        Handler.invoke(instance,method,args,req,resp);
    }
    //判断字符串是否为空
    public static boolean isEmpty(String s){
        return (!"".equals(s)&&s!=null)?false:true;
    }
}

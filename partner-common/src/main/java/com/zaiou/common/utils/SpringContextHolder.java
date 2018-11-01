package com.zaiou.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zaiou.common.service.CoreService;
import com.zaiou.common.vo.CustomResponse;
import com.zaiou.common.vo.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Description: 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候中取出ApplicaitonContext.
 * @auther: LB 2018/9/19 15:12
 * @modify: LB 2018/9/19 15:12
 */
@Component
@Slf4j
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量.
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (SpringContextHolder.applicationContext == null) {
            SpringContextHolder.applicationContext = applicationContext; // NOSONAR
        }
    }

    /**
     * 取得存储在静态变量中的ApplicationContext.
     */
    public static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return applicationContext;
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        checkApplicationContext();
        return (T) applicationContext.getBean(name);
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> clazz) {
        checkApplicationContext();
        return applicationContext.getBean(clazz);
    }

    /**
     * 从静态变量ApplicationContext中取得Bean,
     * 自动转型为所赋值对象的类型.com.froad.ebank.filter.service.transform.impl.ConvertXML
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBeanByClassName(String className) {
        try {
            Class<?> classInstance = Class.forName(className);
            return (T) getBean(classInstance);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检查bean注入
     */
    private static void checkApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicaitonContext未注入,请在beans.xml中定义SpringContextHolder");
        }
    }

    /**
     * 清除applicationContext静态变量.
     */
    public static void cleanApplicationContext() {
        applicationContext = null;
    }

    /**
     * 数据模型 获取反射对象
     */
    public static Request getReflectionObject(String jym, String reqdata) {
        String instanceName = String.format("%s%sReq", Character.toLowerCase(jym.charAt(0)), jym.substring(1));
        log.debug("instanceName:" + instanceName);
        Request request = getBean(instanceName);
        Gson gson = getBean(Gson.class);
        Request fromJson = gson.fromJson(reqdata, request.getClass());
        String threadName = fromJson.getThreadName();
        if (StringUtils.isNotEmpty(threadName)) {
            log.info("switch thread name ---> [{}]", threadName);
            Thread.currentThread().setName(threadName);
        }
        fromJson.setJym(jym);

        log.info("应用层数据:{}", DesensitizedUtils.toJsonString(fromJson));
        return fromJson;
    }

    /**
     * 数据模型 执行流程对应方法对应方法
     */
    public static CustomResponse invokeMethod(String jym, Request request) throws NoSuchMethodException {
        String instanceName = String.format("%s%sProcess", Character.toLowerCase(jym.charAt(0)), jym.substring(1));
        log.debug("invokeMethod instanceName:" + instanceName);
        CoreService service = getBean(instanceName);
        return service.execute(request);
    }


    @Override
    public void destroy() throws Exception {
        SpringContextHolder.cleanApplicationContext();
    }
}

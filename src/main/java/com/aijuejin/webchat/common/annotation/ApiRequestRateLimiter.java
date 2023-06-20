package com.aijuejin.webchat.common.annotation;


import java.lang.annotation.*;


/**
 * @Description: 定义访问频率注解
 * @Title: AsyncTaskPoolConfig
 * @Package com.aijuejin.webchat.common.config
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/17 19:25
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiRequestRateLimiter {
}

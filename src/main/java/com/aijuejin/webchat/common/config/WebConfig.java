package com.aijuejin.webchat.common.config;

import com.aijuejin.webchat.common.interceptor.AuthInterceptor;
import com.aijuejin.webchat.common.properties.WebChatProperties;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.config.annotation.*;

/**
 * @Description: WebMVC配置
 * @Title: WebConfig
 * @Package com.aijuejin.webchat.common.config
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/17 14:46
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private WebChatProperties webChatProperties;
     public void addInterceptors(InterceptorRegistry registry) {
        // Client Authentication
        //registry.addInterceptor(new AuthInterceptor(webChatProperties))
          //   .addPathPatterns("/**")
            // .excludePathPatterns("/", "/api/verify", "/api/session"
           //,"/doc.html"
     //);

    }
}

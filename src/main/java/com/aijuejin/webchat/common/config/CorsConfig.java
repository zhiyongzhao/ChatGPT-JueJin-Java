package com.aijuejin.webchat.common.config;

import com.aijuejin.webchat.common.properties.WebChatProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @Description: 跨域配置
 * @Title: CorsConfig
 * @Package com.aijuejin.webchat.common.config
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/17 19:00
 */
@Configuration
public class CorsConfig {

    @Bean
    @ConditionalOnProperty(name = "app.cors-allowed-origin")
    public FilterRegistrationBean corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setMaxAge(3600L);
        config.addAllowedOriginPattern("*");

        // Apply all path
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);

        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(configSource));
        bean.setOrder(0);
        return bean;
    }
}

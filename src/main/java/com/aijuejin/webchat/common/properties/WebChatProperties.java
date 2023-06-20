package com.aijuejin.webchat.common.properties;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description: //openai属性配置
 * @Title: OpenAIProperties
 * @Package com.aijuejin.webchat.common.properties
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/17 15:41
 */
@Data
@Component
public class WebChatProperties {

    /**
     * OpenAI API KEY
     */
    @Value("${web-chat.openai.api-key}")
    private String apiKey;

    /**
     * OpenAI API Model
     */
    @Value("${web-chat.openai.api-model:gpt-3.5-turbo}")
    private String apiModel;

    /**
     * OpenAI API URL
     */
    @Value("${web-chat.openai.api-url:https://api.openai.com}")
    private String apiUrl="https://api.openai.com";

    /**
     * 可以访问 ChatGPT的代理服务
     */
    @Value("${web-chat.openai.api-proxy-url:https://openai.geekr.cool}")
    private String apiProxyUrl;

    @Value("${web-chat.openai.access-token}")
    private String accessToken;

    @Value("${web-chat.openai.sensitive-id}")
    private String sensitiveId;

    /**
     * API 请求超时, ms
     */
    @Value("${web-chat.api.timeout:2 * 60 * 1000L}")
    private Long timeout ;

    /**
     * Chat API 每小时的最大请求数, 0 - unlimited
     */
    @Value("${web-chat.api.max-request-per-hour:0}")
    private Integer maxRequestPerHour;

    /**
     * 打印请求日志
     */
    private Boolean apiDisableLog=false;

    /**
     * Socks 代理
     */
    private SocksProxy socksProxy;

    /**
     * HTTP 代理
     */
    private HttpProxy httpProxy;

    /**
     * 前端授权密钥
     */
    @Value("${web-chat.auth-secret-key}")
    private String authSecretKey;

    @Data
    public static class SocksProxy {
        @Value("${web-chat.socks-proxy.host}")
        private String host;
        @Value("${web-chat.socks-proxy.port}")
        private Integer port;
        @Value("${web-chat.socks-proxy.username}")
        private String username;
        @Value("${web-chat.socks-proxy.host}")
        private String password;
        public boolean isAvailable() {
            return ObjectUtil.isAllNotEmpty(host, port);
        }
    }

    @Data
    public static class HttpProxy {
        private String host;
        private Integer port;

        public boolean isAvailable() {
            return ObjectUtil.isAllNotEmpty(host, port);
        }
    }

}

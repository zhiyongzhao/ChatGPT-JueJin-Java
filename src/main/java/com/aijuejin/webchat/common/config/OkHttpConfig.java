package com.aijuejin.webchat.common.config;

import cn.hutool.core.util.StrUtil;
import com.aijuejin.webchat.common.properties.WebChatProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Authenticator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Description: OkHttp配置
 * @Title: WebConfig
 * @Package com.aijuejin.webchat.common.config
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/17 14:46
 */
@Slf4j
@Configuration
public class OkHttpConfig {

    @Bean
    public OkHttpClient okHttpClient(WebChatProperties webChatProperties) {
        ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .readTimeout(webChatProperties.getTimeout(), TimeUnit.MILLISECONDS);

        String apiKey = webChatProperties.getApiKey();
        if (StrUtil.isNotBlank(apiKey)) {
            clientBuilder.addInterceptor(new AuthenticationInterceptor(apiKey));
        }

        // 打印接口请求日志
        if (webChatProperties.getApiDisableLog()) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(logging).build();
        }

        // proxy
        this.setupProxy(clientBuilder, webChatProperties);

        return clientBuilder.build();
    }

    /**
     * Set okHttp 代理
     */
    private void setupProxy(OkHttpClient.Builder clientBuilder, WebChatProperties webChatProperties) {
        WebChatProperties.SocksProxy socksProxy = webChatProperties.getSocksProxy();
        WebChatProperties.HttpProxy httpProxy = webChatProperties.getHttpProxy();

        Proxy proxy = null;
        if (socksProxy != null && socksProxy.isAvailable()) {
            // Socks Proxy
            proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(socksProxy.getHost(), socksProxy.getPort()));
            // socks authentication
            if (StrUtil.isAllNotBlank(socksProxy.getUsername(), socksProxy.getPassword())) {
                java.net.Authenticator.setDefault(new ProxyAuthenticator(socksProxy.getUsername(), socksProxy.getPassword()));
            }
        } else if (httpProxy != null && httpProxy.isAvailable()) {
            // HTTP proxy
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpProxy.getHost(), httpProxy.getPort()));
            clientBuilder.proxy(proxy);
        }

        if (proxy != null) {
            clientBuilder.proxy(proxy);
            log.info("OkHttp Proxy configured: {}", proxy);
        }
    }

    public static class AuthenticationInterceptor implements Interceptor {
        private final String token;

        public AuthenticationInterceptor(String token) {
            Objects.requireNonNull(token, "OpenAI token required");
            this.token = token;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (chain.request().header("Authorization") == null) {
                request = chain.request()
                        .newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .build();
            }
            return chain.proceed(request);
        }
    }

    public class ProxyAuthenticator extends Authenticator {
        private PasswordAuthentication auth;
        public ProxyAuthenticator(String strUserName, String strPasswd) {
            auth = new PasswordAuthentication(strUserName, strPasswd.toCharArray());
        }
        protected PasswordAuthentication getPasswordAuthentication() {
            return auth;
        }
    }

}

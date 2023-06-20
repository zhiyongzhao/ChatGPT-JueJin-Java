package com.aijuejin.webchat.common.interceptor;

import cn.hutool.core.util.StrUtil;
import com.aijuejin.webchat.common.exception.UnauthorizedRRException;
import com.aijuejin.webchat.common.properties.WebChatProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Description: 前端授权
 * @Title: AuthInterceptor
 * @Package com.aijuejin.webchat.common.interceptor
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/17 22:28
 */
public class AuthInterceptor implements HandlerInterceptor {
    private final WebChatProperties webChatProperties;

    public AuthInterceptor(WebChatProperties webChatProperties) {
        this.webChatProperties = webChatProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (StrUtil.isNotBlank(webChatProperties.getAuthSecretKey())) {
            String token = getToken(request);
            if (!StrUtil.equals(webChatProperties.getAuthSecretKey(), token)) {
                throw new UnauthorizedRRException();
            }
        }
        return true;
    }

    public String getToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return StrUtil.startWith(bearer, "Bearer ") ? bearer.split(" ")[1] : null;
    }

}

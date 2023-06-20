package com.aijuejin.webchat.common.aspect;

import com.aijuejin.webchat.common.annotation.ApiRequestRateLimiter;
import com.aijuejin.webchat.common.exception.OpenAiApiRequestTooManyRRException;
import com.aijuejin.webchat.common.properties.WebChatProperties;
import com.aijuejin.webchat.common.utils.SpringUtil;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * @Description: 实现ApiRequestRateLimiter注解
 * @Title: ApiRequestRateLimiterAspect
 * @Package com.aijuejin.webchat.common.aspect
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/17 19:26
 */

@Aspect
@Component
public class ApiRequestRateLimiterAspect {
    private RateLimiterRegistry rateLimiterRegistry = null;

    public ApiRequestRateLimiterAspect(WebChatProperties webChatProperties) {
        Integer period = webChatProperties.getMaxRequestPerHour();
        if (period != null && period > 0) {
            this.rateLimiterRegistry = RateLimiterRegistry.of(
                    RateLimiterConfig.custom()
                            .limitForPeriod(period) // Maximum number of requests
                            .limitRefreshPeriod(Duration.ofHours(2)) // 2 hour
                            .timeoutDuration(Duration.ofMillis(1))
                            .build()
            );
        }
    }

    @Before("@annotation(apiRequestRateLimiter)")
    public void doBefore(JoinPoint point, ApiRequestRateLimiter apiRequestRateLimiter) {
        if (this.rateLimiterRegistry == null) return;
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(getCombineKey(point));
        if (!rateLimiter.acquirePermission()) {
            throw new OpenAiApiRequestTooManyRRException();
        }
    }

    public String getCombineKey(JoinPoint point) {
        StringBuilder sb = new StringBuilder(SpringUtil.getClientIp()).append("-");
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();
        sb.append(targetClass.getName()).append("-").append(method.getName());
        return sb.toString();
    }


}

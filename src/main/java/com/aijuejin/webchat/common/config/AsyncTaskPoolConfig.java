package com.aijuejin.webchat.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description: 异步线程池配置
 * @Title: AsyncTaskPoolConfig
 * @Package com.aijuejin.webchat.common.config
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/17 19:06
 */
@Configuration
public class AsyncTaskPoolConfig {

    @Bean
    public ThreadPoolTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2);
        executor.setQueueCapacity(executor.getCorePoolSize() * 10);
        executor.setMaxPoolSize(executor.getCorePoolSize() * 10);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("AsyncTask-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}

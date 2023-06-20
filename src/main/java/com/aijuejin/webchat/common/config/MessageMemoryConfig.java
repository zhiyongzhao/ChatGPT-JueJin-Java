package com.aijuejin.webchat.common.config;

import com.aijuejin.webchat.service.memory.MessageMemoryService;
import com.aijuejin.webchat.service.memory.MessageMemoryServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 数据存储配置
 * @Title: DataStoreConfig
 * @Package com.aijuejin.webchat.common.config
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/17 18:57
 */
@Configuration
public class MessageMemoryConfig {
    @Bean
    @ConditionalOnProperty(name = "app.chat-message-store", havingValue = "memory", matchIfMissing = true)
    public MessageMemoryService memoryMsgStoreService() {
        // memory-store
        return new MessageMemoryServiceImpl();
    }

    @Bean
    @ConditionalOnProperty(name = "app.chat-message-store", havingValue = "mysql")
    public MessageMemoryService mysqlMsgStoreService() {
        // TODO mysql-store
        return null;
    }


}

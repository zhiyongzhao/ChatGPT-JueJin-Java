package com.aijuejin.webchat.service.memory;

import com.aijuejin.webchat.entity.po.ChatMessageMemoryPO;

import java.util.List;

/**
 * @Description: 数据存储服务
 * @Title: MessageMemoryServiceImpl
 * @Package com.aijuejin.webchat.service.memory
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/19 16:44
 */
public interface MessageMemoryService {
    /**
     *
     *
     * @param messages
     */
    void save(ChatMessageMemoryPO...messages);

    /**
     *
     *
     * @param parentMessageId
     * @return
     */
    List<ChatMessageMemoryPO> getParentMessages(String parentMessageId);
}

package com.aijuejin.webchat.entity.po;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @Description: chat数据存储实体
 * @Title: ChatDataMemoryPO
 * @Package com.aijuejin.webchat.entity.po
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/18 16:52
 */

@Builder
@Getter
public class ChatMessageMemoryPO {

    /**
     * required
     */
    private String messageId;

    private String text;

    /**
     *   system,
     *   user",
     *   assistant
     */
    private String role;

    private String parentMessageId;

    /**
     * required
     */
    private LocalDateTime created;
}

package com.aijuejin.webchat.service.memory;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aijuejin.webchat.entity.po.ChatMessageMemoryPO;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Description: //
 * @Title: MessageMemoryServiceImpl
 * @Package com.aijuejin.webchat.service.memory
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/19 16:44
 */
@Slf4j
public class MessageMemoryServiceImpl implements MessageMemoryService{

    private Map<String, ChatMessageMemoryPO> MSG_MAP = new ConcurrentHashMap<>();

    @Override
    public void save(ChatMessageMemoryPO... messages) {
        List<ChatMessageMemoryPO> messageList = Arrays.stream(messages).filter(ObjectUtil::isNotNull).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(messageList)) return;

        for (ChatMessageMemoryPO chatMessageMemoryPO : messageList) {
            MSG_MAP.put(chatMessageMemoryPO.getMessageId(), chatMessageMemoryPO);
        }
    }

    @Override
    public List<ChatMessageMemoryPO> getParentMessages(String parentMessageId) {
        List<ChatMessageMemoryPO> list = new ArrayList<>();
        if (StrUtil.isNotBlank(parentMessageId)) {
            String nextParentMessageId = parentMessageId;
            do {
                ChatMessageMemoryPO msg = MSG_MAP.get(nextParentMessageId);
                if (msg == null) break;
                list.add(msg);
                nextParentMessageId = msg.getParentMessageId();
                if (StrUtil.isBlank(nextParentMessageId)) break;
            } while (true);
        }
        return list.stream()
                // Sort by created
                .sorted(Comparator.comparing(ChatMessageMemoryPO::getCreated))
                .collect(Collectors.toList());
    }
}

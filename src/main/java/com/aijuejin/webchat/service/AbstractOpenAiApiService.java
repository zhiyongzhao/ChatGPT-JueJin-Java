package com.aijuejin.webchat.service;


import com.aijuejin.webchat.entity.po.ChatMessageMemoryPO;
import com.aijuejin.webchat.common.properties.WebChatProperties;
import com.aijuejin.webchat.entity.po.SsePushEventBuilderPO;
import com.aijuejin.webchat.entity.request.ChatProcessRequest;
import com.aijuejin.webchat.entity.response.ChatProcessResponse;
import com.aijuejin.webchat.service.memory.MessageMemoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.service.OpenAiService;
import jakarta.annotation.Resource;
import okhttp3.OkHttpClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

/**
 * @Description: 定义一个openai抽象服务类
 * @Title: AbstractChatService
 * @Package com.aijuejin.webchat.service.impl
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/18 16:30
 */
public abstract class AbstractOpenAiApiService {

    protected ObjectMapper okHttpObjectMapper = OpenAiService.defaultObjectMapper();

    @Resource
    protected WebChatProperties webChatProperties;
    @Resource
    protected OkHttpClient okHttpClient;
    @Resource
    protected MessageMemoryService messageMemoryService;

    /**
     * 基于sse的流式会话
     *
     * @param sseEmitter
     * @param chatProcessRequest
     */
    public abstract void streamChat(SseEmitter sseEmitter, ChatProcessRequest chatProcessRequest);

    /**
     * 把chat结果以sse流的形式从服务端推送给web端
     *
     * @param sseEmitter
     * @param chatProcessResponse
     */
    protected void pushClient(SseEmitter sseEmitter, ChatProcessResponse chatProcessResponse) {
        try {
            sseEmitter.send(new SsePushEventBuilderPO().data(chatProcessResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save conversation message
     *
     * @param messages
     */
    protected void saveMessages(ChatMessageMemoryPO...messages) {
        messageMemoryService.save(messages);
    }

    /**
     * Save conversation message
     *
     * @param parentMessageId
     * @return
     */
    protected List<ChatMessageMemoryPO> getParentMessages(String parentMessageId) {
        return messageMemoryService.getParentMessages(parentMessageId);
    }

}

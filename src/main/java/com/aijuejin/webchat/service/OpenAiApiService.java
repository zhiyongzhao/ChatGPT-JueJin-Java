package com.aijuejin.webchat.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aijuejin.webchat.common.exception.OpenAIApiServiceNotInitializedRRException;
import com.aijuejin.webchat.common.utils.ObjUtil;
import com.aijuejin.webchat.entity.po.ChatMessageMemoryPO;
import com.aijuejin.webchat.entity.request.ChatProcessRequest;
import com.aijuejin.webchat.entity.response.ChatProcessResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.Single;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Header;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: openai接口服务
 * @Title: OpenAiApiService
 * @Package com.aijuejin.webchat.service.memory
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/18 23:27
 */
@Slf4j
@Service
public class OpenAiApiService extends AbstractOpenAiApiService implements CommandLineRunner {

    private OpenAiService openAiService;
    private OpenAiExtApi openAiExtApi;

    @Override
    public void run(String... args) throws Exception {
        if (StrUtil.isBlank(webChatProperties.getApiKey())) {
            log.warn("The [ chat.openai-api-key ] configuration option was not found, init of openAi api service has been skipped");
            return;
        }

        String baseUrl = ObjUtil.getNotBlankValSequential("https://api.openai.com", webChatProperties.getApiUrl());
        Retrofit retrofit = OpenAiService.defaultRetrofit(okHttpClient, okHttpObjectMapper).newBuilder().baseUrl(baseUrl).build();
        openAiService = new OpenAiService(retrofit.create(OpenAiApi.class), okHttpClient.dispatcher().executorService());
        openAiExtApi = retrofit.create(OpenAiExtApi.class);

        log.info("Successfully created the openAi api service instance");
    }

    public void checkService() {
        if (StrUtil.isBlank(webChatProperties.getApiKey())) {
            throw new OpenAIApiServiceNotInitializedRRException();
        }
    }

    /**
     * 查询账户余额
     *
     * @return
     */
    public Double queryBalance() {
        this.checkService();
        Double balance = null;
        try {
            if (StrUtil.isNotBlank(webChatProperties.getSensitiveId())) {
                String authHeader = "Bearer " + webChatProperties.getSensitiveId();
                balance = openAiExtApi.billing(authHeader).blockingGet().getTotal_available();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return balance;
    }

    @Override
    public void streamChat(SseEmitter sseEmitter, ChatProcessRequest chatProcessRequest) {
        this.checkService();
        LocalDateTime startTime = LocalDateTime.now();
        StringBuilder receiveMsgBuilder = new StringBuilder("");

        Double temperature = chatProcessRequest.getTemperature() == null ? 0.8 : chatProcessRequest.getTemperature();
        Double topP = chatProcessRequest.getTopP();
        Integer maxTokens = 4096;
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(ObjUtil.getNotBlankValSequential("gpt-3.5-turbo", webChatProperties.getApiModel()))
                .messages(buildSendMsgBody(chatProcessRequest))
                .temperature(temperature)
                .topP(topP)
               // .maxTokens(maxTokens)
                .build();
        openAiService.streamChatCompletion(request)
                .blockingForEach(chunk -> {
                    String backMsg = null;
                    boolean stop = false;

                    if (ObjectUtil.isNotEmpty(chunk.getChoices())) {
                        ChatCompletionChoice choice = chunk.getChoices().get(0);
                        String finishReason = choice.getFinishReason();
                        if(finishReason == null && choice.getMessage().getContent() != null) {
                            backMsg = choice.getMessage().getContent();
                        } else if (finishReason != null){
                            backMsg = choice.getMessage().getContent();
                            stop = true;
                        }
                        if (backMsg != null) {
                            receiveMsgBuilder.append(backMsg);

                            ChatProcessResponse.Choice choiceResp = ChatProcessResponse.Choice.builder()
                                    .index(choice.getIndex())
                                    .finish_reason(choice.getFinishReason())
                                    .delta(ChatProcessResponse.Delta.builder().content(backMsg).build())
                                    .build();
                            ChatProcessResponse.Detail detailResp = ChatProcessResponse.Detail.builder()
                                    .id(chunk.getId())
                                    .created(chunk.getCreated())
                                    .object(chunk.getObject())
                                    .model(chunk.getModel())
                                    .choices(Arrays.asList(choiceResp))
                                    .build();
                            ChatProcessResponse chatProcessResponse = ChatProcessResponse.builder()
                                    .id(chunk.getId())
                                    .role(choice.getMessage().getRole())
                                    .text(receiveMsgBuilder.toString())
                                    .delta(backMsg)
                                    .detail(detailResp)
                                    .build();
                            super.pushClient(sseEmitter, chatProcessResponse);
                            log.debug("push message to client：{}", chatProcessResponse);
                        }
                        if (stop) {
                            // save message to store
                            String sendMessageId = IdUtil.simpleUUID();
                            super.saveMessages(
                                    // send message
                                    ChatMessageMemoryPO.builder()
                                            .messageId(sendMessageId)
                                            .text(chatProcessRequest.getPrompt())
                                            .parentMessageId(chatProcessRequest.getOptions() != null ? chatProcessRequest.getOptions().getParentMessageId() : null)
                                            .role(ChatMessageRole.USER.value())
                                            .created(startTime)
                                            .build(),
                                    // receive message
                                    ChatMessageMemoryPO.builder()
                                            .messageId(chunk.getId())
                                            .text(receiveMsgBuilder.toString())
                                            .parentMessageId(sendMessageId)
                                            .role(ChatMessageRole.ASSISTANT.value())
                                            .created(LocalDateTime.now())
                                            .build()
                            );

                            sseEmitter.complete();
                        }
                    }

                });

    }

    /**
     * 构建消息Body
     * @param chatProcessRequest
     * @return
     */
    private List<ChatMessage> buildSendMsgBody(ChatProcessRequest chatProcessRequest) {
        List<ChatMessage> messages = new ArrayList<>();
        // system message
        if (StrUtil.isNotBlank(chatProcessRequest.getSystemMessage())) {
            ChatMessage msg = new ChatMessage();
            msg.setRole(ChatMessageRole.SYSTEM.value());
            msg.setContent(chatProcessRequest.getSystemMessage());
            messages.add(msg);
        }

        // history message
        if (chatProcessRequest.getOptions() != null && StrUtil.isNotBlank(chatProcessRequest.getOptions().getParentMessageId())) {
            List<ChatMessageMemoryPO> historyMessages = super.getParentMessages(chatProcessRequest.getOptions().getParentMessageId());
            historyMessages.forEach(row -> {
                ChatMessage msg = new ChatMessage();
                msg.setRole(row.getRole());
                msg.setContent(row.getText());
                messages.add(msg);
            });
            if (ObjectUtil.isEmpty(historyMessages)) {
                // not found parent messages
                chatProcessRequest.getOptions().setParentMessageId(null);
            }
        }

        // user send message
        ChatMessage latestMsg = new ChatMessage();
        latestMsg.setRole(ChatMessageRole.USER.value());
        latestMsg.setContent(chatProcessRequest.getPrompt());
        messages.add(latestMsg);

        return messages;
    }

    interface OpenAiExtApi {
        @GET("/dashboard/billing/credit_grants")
        Single<BillingResult> billing(@Header("Authorization") String authHeader);
    }

    @Data
    static class BillingResult {
        Object error;
        @JsonProperty("total_available")
        Double total_available;
    }

}

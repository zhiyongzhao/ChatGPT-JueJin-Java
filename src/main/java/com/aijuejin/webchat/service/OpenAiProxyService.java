package com.aijuejin.webchat.service;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.aijuejin.webchat.common.exception.ProxyApiServiceNotInitializedRRException;
import com.aijuejin.webchat.common.exception.RRException;
import com.aijuejin.webchat.entity.po.ProxyChatChunk;
import com.aijuejin.webchat.entity.po.ProxyResponseBodyCallback;
import com.aijuejin.webchat.entity.po.ProxySSEPO;
import com.aijuejin.webchat.entity.request.ChatProcessRequest;
import com.aijuejin.webchat.entity.response.ChatProcessResponse;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.*;

/**
 * @Description: 代理模式下的接口服务
 * @Title: OpenAiProxyService
 * @Package com.aijuejin.webchat.service
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/20 17:43
 */
@Slf4j
@Service
public class OpenAiProxyService extends AbstractOpenAiApiService implements CommandLineRunner {

    private ProxyApi proxyApi;


    @Override
    public void run(String... args) throws Exception {
        if (StrUtil.isBlank(webChatProperties.getAccessToken())) {
            log.warn("The [ access-token ] configuration option was not found, init of openAi peoxy service has been skipped");
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost/") // placeholder
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(OpenAiService.defaultObjectMapper()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        proxyApi = retrofit.create(ProxyApi.class);

        log.info("Successfully created the openAi proxy service instance");
    }

    public void checkService() {
        if (StrUtil.isBlank(webChatProperties.getAccessToken()) || StrUtil.isBlank(webChatProperties.getApiProxyUrl())) {
            throw new ProxyApiServiceNotInitializedRRException();
        }
    }

    @Override
    public void streamChat(SseEmitter sseEmitter, ChatProcessRequest chatProcessRequest){
        this.checkService();
        String authHeader = "Bearer " + webChatProperties.getAccessToken();
        ObjectNode body = buildSendMsgBody(chatProcessRequest);
        String sendMsgId = body.findValues("messages").get(0).findValue("id").asText();

        Flowable.<ProxySSEPO>create(emitter -> proxyApi.conversation(webChatProperties.getApiProxyUrl(), body, authHeader).enqueue(new ProxyResponseBodyCallback(emitter, false)), BackpressureStrategy.BUFFER)
                .map(sse -> okHttpObjectMapper.readValue(sse.getData(), ProxyChatChunk.class))
                .blockingForEach(chunk -> {
                    try {
                        if (StrUtil.isNotBlank(chunk.getError())) {
                            log.debug(chunk.getError());
                            sseEmitter.completeWithError(new RRException(chunk.getError()));
                        }

                        if (!ChatMessageRole.ASSISTANT.value().equalsIgnoreCase(chunk.getMessage().getAuthor().getRole())) return;

                        boolean stop = BooleanUtil.isTrue(chunk.getMessage().getEndTurn());

                        if (!stop) {
                            ChatProcessResponse resp = ChatProcessResponse.builder()
                                    .id(chunk.getMessage().getId())
                                    .role(chunk.getMessage().getAuthor().getRole())
                                    .text(chunk.getMessage().getContent().getParts().get(0))
                                    .parentMessageId(sendMsgId)
                                    .conversationId(chunk.getConversationId())
                                    .build();
                            super.pushClient(sseEmitter, resp);
                            log.debug("push message to client：{}", resp);
                        } else {
                            sseEmitter.complete();
                        }
                    } catch (Exception e) {
                        sseEmitter.completeWithError(e);
                        throw e;
                    }
                });
    }

    private ObjectNode buildSendMsgBody(ChatProcessRequest chatProcessRequest) {
        String sendMsg = chatProcessRequest.getPrompt();
        String msgId = IdUtil.randomUUID();
        String parentMessageId = IdUtil.randomUUID();
        String conversationId = null;

        ChatProcessRequest.Options options = chatProcessRequest.getOptions();
        if (options != null) {
            if (StrUtil.isNotBlank(options.getParentMessageId())) {
                parentMessageId = options.getParentMessageId();
            }
            if (StrUtil.isNotBlank(options.getConversationId())) {
                conversationId = options.getConversationId();
            }
        }

        ObjectNode message = okHttpObjectMapper.createObjectNode();
        message.put("id", msgId);
        message.put("author", okHttpObjectMapper.createObjectNode().put("role", ChatMessageRole.USER.value()));
        ObjectNode content = okHttpObjectMapper.createObjectNode().put("content_type", "text");
        content.putArray("parts").add(sendMsg);
        message.put("content", content);

        ObjectNode root = okHttpObjectMapper.createObjectNode();
        root.put("action", "next");
        root.put("model", "text-davinci-002-render-sha");
        root.putArray("messages").add(message);
        root.put("conversation_id", conversationId);
        root.put("parent_message_id", parentMessageId);

        return root;
    }

    interface ProxyApi {
        @Streaming
        @Headers({"Cache-Control: no-cache", "X-Accel-Buffering: no"})
        @POST
        Call<ResponseBody> conversation(@Url String url, @Body ObjectNode body, @Header("Authorization") String authHeader);
    }
}

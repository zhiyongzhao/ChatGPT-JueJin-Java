package com.aijuejin.webchat.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aijuejin.webchat.common.annotation.ApiRequestRateLimiter;
import com.aijuejin.webchat.common.exception.OpenAiApiRequestErrorRRException;
import com.aijuejin.webchat.common.exception.RRException;
import com.aijuejin.webchat.common.utils.ObjUtil;
import com.aijuejin.webchat.entity.request.AuthVerifyRequest;
import com.aijuejin.webchat.entity.request.ChatProcessRequest;
import com.aijuejin.webchat.entity.response.ConfigResponse;
import com.aijuejin.webchat.entity.response.SessionResponse;
import com.aijuejin.webchat.common.base.R;
import com.aijuejin.webchat.common.constant.ApiRunConstant;
import com.aijuejin.webchat.common.properties.WebChatProperties;
import com.aijuejin.webchat.service.OpenAiApiService;
import com.aijuejin.webchat.service.OpenAiProxyService;
import com.theokanning.openai.OpenAiHttpException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * @Description: Chat API调用
 * @Title: WebChatController
 * @Package com.aijuejin.webchat.controller
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/17 23:16
 */
@Tag(name = "Chat聊天模块")
@RestController
@RequestMapping("/api")
@Slf4j
public class ChatController {

    @Resource
    private WebChatProperties webChatProperties;
    @Resource
    private OpenAiApiService openAiApiService;
    @Resource
    private OpenAiProxyService openAiProxyService;
    @Resource
    private ThreadPoolTaskExecutor asyncTaskExecutor;

    @Operation(summary = "session")
    @PostMapping("/session")
    public R<SessionResponse> session() {
        return R.ok(
                SessionResponse.builder()
                        .auth(StrUtil.isNotBlank(webChatProperties.getAuthSecretKey()))
                        .model(ApiRunConstant.get(webChatProperties).getName())
                        .build()
        );
    }

    @Operation(summary = "加载后端配置信息")
    @PostMapping("/config")
    public R<ConfigResponse> config() {
        String socksProxy;
        if (webChatProperties.getSocksProxy() != null && ObjectUtil.isAllNotEmpty(webChatProperties.getSocksProxy().getHost(), webChatProperties.getSocksProxy().getPort())) {
            socksProxy = StrUtil.format("{}:{}", webChatProperties.getSocksProxy().getHost(), webChatProperties.getSocksProxy().getPort());
        } else {
            socksProxy = "-";
        }

        String httpProxy;
        if (webChatProperties.getHttpProxy() != null && ObjectUtil.isAllNotEmpty(webChatProperties.getHttpProxy().getHost(), webChatProperties.getHttpProxy().getPort())) {
            httpProxy = StrUtil.format("{}:{}", webChatProperties.getHttpProxy().getHost(), webChatProperties.getHttpProxy().getPort());
        } else {
            httpProxy = "-";
        }

        Double balance = null;
        ApiRunConstant apiRunConstant = ApiRunConstant.get(webChatProperties);
        if (apiRunConstant == ApiRunConstant.API) {
            balance = openAiApiService.queryBalance();
        }

        return R.ok(
                ConfigResponse.builder()
                        .apiModel(apiRunConstant.getName())
                        .timeout(webChatProperties.getTimeout())
                        .httpsProxy(httpProxy)
                        .socksProxy(socksProxy)
                        .proxy(ObjUtil.getNotBlankValSequential("-", webChatProperties.getApiProxyUrl()))
                        .balance(ObjUtil.getNotNullValSequential("-", balance))
                        .build()
        );
    }

    @Operation(summary = "邀请码验证")
    @PostMapping("/verify")
    public R<SessionResponse> authVerify(@RequestBody @Validated AuthVerifyRequest authVerifyRequest) {

        if (!StrUtil.equals(webChatProperties.getAuthSecretKey(), authVerifyRequest.getInvitationCode())) {
            return R.error("邀请码无效，请关注上方公众号获取！");
        }
        return R.ok("验证成功！");
    }

    @Operation(summary = "聊天过程")
    @ApiRequestRateLimiter
    @PostMapping("/chat-process")
    public SseEmitter chatProcess(@RequestBody @Validated ChatProcessRequest chatProcessRequest) {
        SseEmitter sseEmitter = new SseEmitter(webChatProperties.getTimeout());
        asyncTaskExecutor.execute(() -> {
            try {
                switch (ApiRunConstant.get(webChatProperties)) {
                    case API:
                        openAiApiService.streamChat(sseEmitter, chatProcessRequest);
                        break;
                    case PROXY:
                        openAiProxyService.streamChat(sseEmitter, chatProcessRequest);
                        break;
                }
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
                RRException thrEx;
                if (e instanceof RRException) {
                    thrEx = (RRException) e;
                } else if (e instanceof OpenAiHttpException) {
                    OpenAiHttpException exception = (OpenAiHttpException) e;
                    thrEx = new OpenAiApiRequestErrorRRException(exception.statusCode, exception.getMessage());
                } else if (e.getCause() instanceof SocketTimeoutException) {
                    thrEx = new OpenAiApiRequestErrorRRException(0);
                } else if (e.getCause() instanceof SocketException || e.getCause() instanceof IOException) {
                    thrEx = new OpenAiApiRequestErrorRRException(-1);
                } else {
                    thrEx = new OpenAiApiRequestErrorRRException();
                }
                sseEmitter.completeWithError(thrEx);
            }
        });
        return sseEmitter;
    }


}
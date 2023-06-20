package com.aijuejin.webchat.entity.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * @Description: Config Response参数
 * @Title: ConfigResponse
 * @Package com.aijuejin.webchat.entity.response
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/18 16:11
 */
@Builder
@Getter
public class ConfigResponse {

    @Schema(description = "后台调用是否走代理接口，取值：ChatGPTAPI  ChatGPTProxyAPI")
    private String apiModel;

    @Schema(description = "代理url")
    private String proxy;

    @Schema(description = "接口超时时间")
    private Long timeout;

    @Schema(description = "socksd代理信息")
    private String socksProxy;

    @Schema(description = "http代理信息")
    private String httpsProxy;

    @Schema(description = "openai接口调用账户余额")
    private String balance;
}

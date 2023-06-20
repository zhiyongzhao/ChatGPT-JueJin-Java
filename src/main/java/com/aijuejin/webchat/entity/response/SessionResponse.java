package com.aijuejin.webchat.entity.response;

import com.aijuejin.webchat.common.constant.ApiRunConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * @Description: 鉴权Response参数
 * @Title: SessionResponse
 * @Package com.aijuejin.webchat.entity.response
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/17 23:22
 */
@Builder
@Getter
public class SessionResponse {
    private boolean auth;

    /**
     * @see ApiRunConstant
     */
    @Schema(description = "后台调用是否走代理接口，取值：ChatGPTAPI  ChatGPTProxyAPI")
    private String model;

}

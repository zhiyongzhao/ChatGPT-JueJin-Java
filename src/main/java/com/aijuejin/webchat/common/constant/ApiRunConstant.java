package com.aijuejin.webchat.common.constant;

import cn.hutool.core.util.StrUtil;
import com.aijuejin.webchat.common.properties.WebChatProperties;
import lombok.Getter;

/**
 * @Description: 常量
 * @Title: ApiRunConstant
 * @Package com.aijuejin.webchat.common.constant
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/17 23:27
 */
@Getter
public enum ApiRunConstant {

    API("ChatGPTAPI"),
    PROXY("ChatGPTProxyAPI"),
            ;
    ApiRunConstant(String name) {
        this.name = name;
    }

    private String name;

    public static ApiRunConstant get(WebChatProperties properties) {
        return StrUtil.isNotBlank(properties.getApiKey()) ? ApiRunConstant.API : ApiRunConstant.PROXY;
    }
}

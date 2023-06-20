package com.aijuejin.webchat.common.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Title: OpenAiApiRequestErrorRRException
 * @Package com.aijuejin.webchat.common.exception
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/20 16:48
 */
public class OpenAiApiRequestErrorRRException extends RRException{

    public static final Map<Integer, String> OPENAI_HTTP_ERROR;
    static {
        OPENAI_HTTP_ERROR = new HashMap<>();
        OPENAI_HTTP_ERROR.put(-1, "[OpenAI] 发送请求失败 | Failed to send request");
        OPENAI_HTTP_ERROR.put(0, "[OpenAI] 请求超时 | Request timeout");
        OPENAI_HTTP_ERROR.put(401, "[OpenAI] 提供错误的API密钥 | Incorrect API key provided");
        OPENAI_HTTP_ERROR.put(403, "[OpenAI] 服务器拒绝访问，请稍后再试 | Server refused to access, please try again later");
        OPENAI_HTTP_ERROR.put(429, "[OpenAI] 访问速率受限，请稍后再试 | Access rate is limited, please try again later");
        OPENAI_HTTP_ERROR.put(500, "[OpenAI] 服务器繁忙，请稍后再试 | Internal Server Error");
        OPENAI_HTTP_ERROR.put(502, "[OpenAI] 错误的网关 |  Bad Gateway");
        OPENAI_HTTP_ERROR.put(503, "[OpenAI] 服务器繁忙，请稍后再试 | Server is busy, please try again later");
        OPENAI_HTTP_ERROR.put(504, "[OpenAI] 网关超时 | Gateway Time-out");
    }

    public OpenAiApiRequestErrorRRException() {
        this(500);
    }

    public OpenAiApiRequestErrorRRException(int statusCode) {
        this(statusCode, OPENAI_HTTP_ERROR.get(500));
    }

    public OpenAiApiRequestErrorRRException(int statusCode, String defaultMsg) {
        super(OPENAI_HTTP_ERROR.getOrDefault(statusCode, defaultMsg));
    }

}

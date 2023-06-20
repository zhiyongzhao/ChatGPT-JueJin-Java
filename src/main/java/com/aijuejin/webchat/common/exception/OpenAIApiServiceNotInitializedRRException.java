package com.aijuejin.webchat.common.exception;

/**
 * @Description: API Service 异常
 * @Title: OpenAIApiServiceNotInitializedRRException
 * @Package com.aijuejin.webchat.common.exception
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/19 13:49
 */
public class OpenAIApiServiceNotInitializedRRException extends RRException{
    public OpenAIApiServiceNotInitializedRRException() {
        super("API Service not initialized");
    }
}

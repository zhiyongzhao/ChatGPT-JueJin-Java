package com.aijuejin.webchat.common.exception;

/**
 * @Description:chat api请求太多
 * @Title: ChatApiRequestTooManyBizException
 * @Package com.aijuejin.webchat.common.exception
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/17 22:15
 */

public class OpenAiApiRequestTooManyRRException extends RRException{
    public OpenAiApiRequestTooManyRRException() {
        super("Too many request from this IP in 2 hour");
    }
}

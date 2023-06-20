package com.aijuejin.webchat.common.exception;

/**
 * @Description: Proxy Service异常
 * @Title: ProxyApiServiceNotInitializedRRException
 * @Package com.aijuejin.webchat.common.exception
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/20 18:06
 */
public class ProxyApiServiceNotInitializedRRException extends RRException{
    public ProxyApiServiceNotInitializedRRException() {
        super("Proxy Service not initialized");
    }
}

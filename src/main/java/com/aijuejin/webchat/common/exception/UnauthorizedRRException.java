package com.aijuejin.webchat.common.exception;

import org.springframework.http.HttpStatus;

/**
 * @Description: 未授权异常
 * @Title: UnauthorizedRRException
 * @Package com.aijuejin.webchat.common.exception
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/17 22:41
 */
public class UnauthorizedRRException extends RRException{
    public UnauthorizedRRException() {
        super(HttpStatus.UNAUTHORIZED, "Error: 无访问权限！");
    }
}

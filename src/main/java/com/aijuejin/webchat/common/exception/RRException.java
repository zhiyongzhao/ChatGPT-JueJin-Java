package com.aijuejin.webchat.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @Description: 自定义异常
 * @Title: RRException
 * @Package com.aijuejin.webchat.common.exception
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/17 22:17
 */
@Getter
public class RRException extends RuntimeException{

    private HttpStatus httpStatus = HttpStatus.OK;

    public RRException(String msg) {
        super(msg);
    }

    public RRException(HttpStatus status) {
        this(status, status.getReasonPhrase());
    }

    public RRException(HttpStatus httpStatus, String msg) {
        super(msg);
        this.httpStatus = httpStatus;
    }
}

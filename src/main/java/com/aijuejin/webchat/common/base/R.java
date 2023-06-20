package com.aijuejin.webchat.common.base;


import cn.hutool.core.util.StrUtil;
import com.aijuejin.webchat.common.constant.ApiCodeConstant;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 统一返回结果
 * @Title: AsyncTaskPoolConfig
 * @Package com.aijuejin.webchat.common.config
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/17 19:25
 */
@Builder
@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<T> {

    private String status;
    private String code;
    private String msg;
    private T data;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    /**
     * 设置待定结果
     * @param code
     * @param msg
     * @return
     */
    public static R result(String code, String msg,String status) {
        return R.builder()
                .status(status)
                .code(code)
                .msg(msg)
                .time(new Date())
                .build();
    }
    public static R result(ApiCodeConstant apiCode, String msg, String status,Object data) {
        String message = apiCode.getMsg();
        if (StrUtil.isNotBlank(msg)) {
            message = msg;
        }
        return R.builder()
                .status(status)
                .code(apiCode.getCode())
                .msg(message)
                .data(data)
                .time(new Date())
                .build();
    }
    public static R result(ApiCodeConstant apiCode,String status ,Object data) {
        return result(apiCode, null,status, data);
    }
    public static R result(ApiCodeConstant apiCode,String status ) {
        return R.builder()
                .status(status)
                .code(apiCode.getCode())
                .msg(apiCode.getMsg())
                .time(new Date())
                .build();
    }

    /**
     * 设置成功结果
     * @return
     */
    public static R ok() {
        return result(ApiCodeConstant.SUCCESS,"Success");
    }

    public static R ok(String msg) {
        return result(ApiCodeConstant.SUCCESS.getCode(), msg,"Success");
    }

    public static <T> R<T> ok(Object data) {
        return result(ApiCodeConstant.SUCCESS,"Success", data);
    }

    public static <T> R<T> ok(String msg, T data) {
        return result(null,msg,"Success",data);
    }

    /**
     * 设置失败结果
     * @return
     */
    public static R error() {
        return result(ApiCodeConstant.ERROR,"Fail");
    }

    public static R error(String code,String msg) {
        return result(code,msg,"Fail");
    }

    public static R error(String msg) {
        return result(ApiCodeConstant.ERROR.getCode(), msg,"Fail");
    }
}

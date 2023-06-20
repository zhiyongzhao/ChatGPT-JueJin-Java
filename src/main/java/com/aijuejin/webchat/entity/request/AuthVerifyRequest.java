package com.aijuejin.webchat.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * @Description: verify request参数
 * @Title: AuthVerifyRequest
 * @Package com.aijuejin.webchat.entity.request
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/19 22:48
 */
@Schema(name = "邀请码name",description = "邀请码对象")
@Data
public class AuthVerifyRequest {

    @Schema(description = "邀请码")
    @NotBlank(message = "邀请码为空！")
    private String InvitationCode;
}

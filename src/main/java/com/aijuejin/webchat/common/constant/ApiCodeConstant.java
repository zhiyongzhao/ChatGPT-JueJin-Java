package com.aijuejin.webchat.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiCodeConstant {
    SUCCESS("0", "成功!"),
    ERROR("-1", "服务异常 | server exception!"),
    LIMITERROR("1001", "您今天的免费次数使用完了，请添加微信1257572500购买"),
    ;

    // 响应状态码
    private String code;
    // 响应信息
    private String msg;
}

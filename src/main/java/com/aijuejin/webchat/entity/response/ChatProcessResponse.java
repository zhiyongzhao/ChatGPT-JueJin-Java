package com.aijuejin.webchat.entity.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * @Description: ChatProcess Response参数
 * @Title: ChatProcessResponse
 * @Package com.aijuejin.webchat.entity.response
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/20 17:33
 */
@Schema(description = "Chat响应消息")
@Builder
@Getter
@ToString
public class ChatProcessResponse {

    @Schema(description = "消息id")
    private String id;

    @Schema(description = "消息内容")
    private String text;

    @Schema(description = "角色：system、user、assistant三种")
    private String role;

    private Detail detail;

    private String delta;

    @Schema(description = "上条消息中的parentMessageId")
    private String parentMessageId;

    @Schema(description = "，对话保持id，上条消息中的parentMessageId")
    private String conversationId;

    @Builder
    @Getter
    public static class Detail {
        private String id;
        private String object;
        private Long created;
        private String model;
        private List<Choice> choices;
    }

    @Builder
    @Getter
    public static class Choice {
        private Integer index;
        private String finish_reason;
        private Delta delta;
    }

    @Builder
    @Getter
    public static class Delta {
        private String content;
    }

}

package com.aijuejin.webchat.entity.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;



/**
 * @Description: chat request参数
 * @Title: ChatProcessRequest
 * @Package com.aijuejin.webchat.entity.request
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/20 15:40
 */
@Schema(name = "聊天过程name",description = "聊天过程对象")
@Data
public class ChatProcessRequest {

    @Schema(description = "提示语")
    @NotBlank(message = "prompt.json is empty")
    private String prompt;

    @Schema(description = "帮助设置assistant的行为，暂时不用，可以为空")
    private String systemMessage;

    private Options options;

    @Schema(description = "采样温度，介于 0 和 2 之间,例如：0.8")
    private Double temperature;

    @Schema(description = "核心采样 ,介于0到1之间，例如： 0.1")
    @JsonProperty(value = "top_p")
    private Double topP;

    @Data
    @Schema(description = "从上次消息中获取，父消息id：parentMessageId ，对话保持id：conversationId")
    public static class Options {
        private String parentMessageId;
        private String conversationId;
    }

}

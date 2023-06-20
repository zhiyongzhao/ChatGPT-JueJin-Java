package com.aijuejin.webchat.entity.po;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.springframework.http.MediaType.TEXT_PLAIN;

/**
 * @Description:
 * @Title: SsePushEventBuilderPO
 * @Package com.aijuejin.webchat.entity.po
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/18 22:57
 */
public class SsePushEventBuilderPO implements SseEmitter.SseEventBuilder{

    private final Set<ResponseBodyEmitter.DataWithMediaType> dataToSend = new LinkedHashSet<>(1);

    @Nullable
    private StringBuilder sb;

    @Override
    public SseEmitter.SseEventBuilder id(String id) {
        return this;
    }

    @Override
    public SseEmitter.SseEventBuilder name(String eventName) {
        return this;
    }

    @Override
    public SseEmitter.SseEventBuilder reconnectTime(long reconnectTimeMillis) {
        return this;
    }

    @Override
    public SseEmitter.SseEventBuilder comment(String comment) {
        return this;
    }

    @Override
    public SseEmitter.SseEventBuilder data(Object object) {
        return data(object, null);
    }


    public SseEmitter.SseEventBuilder data(Object object, @Nullable MediaType mediaType) {
        saveAppendedText();
        this.dataToSend.add(new ResponseBodyEmitter.DataWithMediaType(object, mediaType));
        append('\n');
        return this;
    }

    SsePushEventBuilderPO append(char ch) {
        if (this.sb == null) {
            this.sb = new StringBuilder();
        }
        this.sb.append(ch);
        return this;
    }

    @Override
    public Set<ResponseBodyEmitter.DataWithMediaType> build() {
        if (!StringUtils.hasLength(this.sb) && this.dataToSend.isEmpty()) {
            return Collections.emptySet();
        }
        saveAppendedText();
        return this.dataToSend;
    }

    private void saveAppendedText() {
        if (this.sb != null) {
            this.dataToSend.add(new ResponseBodyEmitter.DataWithMediaType(this.sb.toString(), TEXT_PLAIN));
            this.sb = null;
        }
    }
}

package com.aijuejin.webchat;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aijuejin.webchat.common.properties.WebChatProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ChatgptAiJuejinApplication implements CommandLineRunner {

	@Resource
	private WebChatProperties webChatProperties;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ChatgptAiJuejinApplication.class, args);
	}

	@Override
	public void run(String... args) {
		if (StrUtil.isAllBlank(webChatProperties.getApiKey(), webChatProperties.getAccessToken())) {
			throw new RuntimeException("[ chat.openai-api-key ] and [ chat.openai-access-token ] configure at least one");
		}
	}

}

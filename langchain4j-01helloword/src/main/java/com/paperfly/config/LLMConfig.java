package com.paperfly.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMConfig {
    @Bean
    public ChatModel chatModel() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("aliAi-key"))
                .modelName("qwen-plus")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .build();
    }


}

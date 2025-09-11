package paperfly.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import paperfly.service.ChatAssistant;

@Configuration
public class AiServiceConfig {

    @Bean
    public ChatAssistant chatAssistant(@Qualifier("qwen") ChatModel chatModel) {
        return AiServices.create(ChatAssistant.class, chatModel);
    }
}

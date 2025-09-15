package paperfly.config;


import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import paperfly.persistence.RedisChatMemoryStore;
import paperfly.service.ChatPersistenceAssistant;

@Configuration
public class LLMConfig {
    @Bean
    public ChatModel chatModel() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("aliAi-key"))
                .modelName("qwen-plus")
                .logRequests(true)
                .logResponses(true)
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .build();
    }

    @Bean("chatPersistenceWindowChatMemory")
    public ChatPersistenceAssistant chatPersistenceWindowChatMemory(ChatModel chatModel, RedisChatMemoryStore store) {
        return AiServices
                .builder(ChatPersistenceAssistant.class)
                .chatModel(chatModel)
                .chatMemoryProvider(memoryId -> {
                    return MessageWindowChatMemory
                            .builder()
                            .id(memoryId)
                            .maxMessages(10)
                            .chatMemoryStore(store)
                            .build();
                })
                .build();
    }
}

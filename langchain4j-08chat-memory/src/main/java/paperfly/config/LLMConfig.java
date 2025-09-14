package paperfly.config;


import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import paperfly.service.ChatAssistant;
import paperfly.service.ChatMemoryAssistant;

@Configuration
public class LLMConfig {
    @Bean
    public ChatModel chatModel() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("aliAi-key"))
                .modelName("qwen-long")
                .logRequests(true)
                .logResponses(true)
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .build();
    }

    @Bean("chat")
    public ChatAssistant chatAssistant(ChatModel chatModel) {
        return AiServices.create(ChatAssistant.class, chatModel);
    }

    @Bean("chatMemoryWindowChatMemory")
    public ChatMemoryAssistant chatMemoryWindowChatMemory(ChatModel chatModel) {
        return AiServices
                .builder(ChatMemoryAssistant.class)
                .chatModel(chatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    @Bean("chatMemoryTokenWindowChatMemory")
    public ChatMemoryAssistant chatMemoryTokenWindowChatMemory(ChatModel chatModel) {
        return AiServices
                .builder(ChatMemoryAssistant.class)
                .chatModel(chatModel)
                .chatMemoryProvider(memoryId -> TokenWindowChatMemory.withMaxTokens(1000,new OpenAiTokenCountEstimator("gpt-4")))
                .build();
    }
}

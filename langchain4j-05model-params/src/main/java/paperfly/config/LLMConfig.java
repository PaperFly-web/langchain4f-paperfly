package paperfly.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import paperfly.listener.TestChatModelListener;

import java.time.Duration;
import java.util.Arrays;

@Configuration
public class LLMConfig {
    @Bean(name = "qwen")
    public ChatModel qwenChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("aliAi-key"))
                .modelName("qwen-plus")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .logRequests(true)
                .logResponses(true)
                .listeners(Arrays.asList(new TestChatModelListener()))
                .maxRetries(2)
                .timeout(Duration.ofSeconds(2))
                .build();
    }

    @Bean(name = "deepseek")
    public ChatModel deepseekchatModel() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("deepseek-key"))
//                .modelName("deepseek-chat")
                .modelName("deepseek-reasoner")
                .baseUrl("https://api.deepseek.com/v1")
                .logRequests(true)
                .logResponses(true)
                .build();
    }


}

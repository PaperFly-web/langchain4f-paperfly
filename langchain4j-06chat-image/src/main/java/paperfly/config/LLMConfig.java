package paperfly.config;

import dev.langchain4j.community.model.dashscope.WanxImageModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMConfig {
    @Bean
    public WanxImageModel imageModel() {
        WanxImageModel build = WanxImageModel.builder()
                .apiKey(System.getenv("aliAi-key"))
                .modelName("wanx2.0-t2i-turbo")
                .watermark(true)
                .build();
        return build;
    }

    @Bean(name = "qwen")
    public ChatModel qwenChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("aliAi-key"))
                .modelName("qwen-vl-max")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .logRequests(true)
                .logResponses(true)
                .build();
    }

}

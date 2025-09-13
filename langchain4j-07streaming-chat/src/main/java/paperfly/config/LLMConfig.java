package paperfly.config;


import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMConfig {
    @Bean
    public StreamingChatModel imageModel() {


        StreamingChatModel  model = OpenAiStreamingChatModel.builder()
                .apiKey(System.getenv("aliAi-key"))
                .modelName(GPT_4_O_MINI)
                .build();
        return build;
    }


}

package paperfly.config;

import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMConfig {
    @Bean
    public ImageModel qwenChatModel() {
        ImageModel model = OpenAiImageModel.builder()
                .apiKey(System.getenv("aliAi-key"))
                .modelName("qwen-vl-max")
                .build();
        return model;
    }

}

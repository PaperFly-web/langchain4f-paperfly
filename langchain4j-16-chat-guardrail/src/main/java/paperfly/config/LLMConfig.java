package paperfly.config;


import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import paperfly.bean.SafeInputGuardrail;
import paperfly.service.ChatAssistant;

@Configuration
@Slf4j
public class LLMConfig {

    @Bean
    public ChatModel chatModel() {

        return OpenAiChatModel.builder()
                .apiKey(System.getenv("aliAi-key"))
                .modelName("qwen-plus")
//                .responseFormat(ResponseFormat.builder().type(JSON).build()) // 见下面的 [2]
                .logRequests(true)
                .logResponses(true)
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .build();
    }

    @Bean
    public ChatAssistant assistant(ChatModel chatModel) {

        return AiServices
                .builder(ChatAssistant.class)
                .chatModel(chatModel)
                .inputGuardrailClasses(SafeInputGuardrail.class)
                .build();
    }

}

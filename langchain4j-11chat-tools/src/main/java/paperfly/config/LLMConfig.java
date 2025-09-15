package paperfly.config;


import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import paperfly.service.FunctionAssistant;
import paperfly.service.MathGenius;
import paperfly.service.WeatherAssistant;
import paperfly.tools.Calculator;
import paperfly.tools.WeatherTool;

import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class LLMConfig {
    @Autowired
    private WeatherTool weatherTool;
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

    @Bean
    public FunctionAssistant functionAssertion(ChatModel chatModel) {
        ToolSpecification toolSpecification = ToolSpecification.builder()
                .name("开具发票助手")
                .description("根据用户提交的开票信息，开具发票")
                .parameters(JsonObjectSchema.builder()
                        .addStringProperty("name", "公司名称")
                        .addStringProperty("dutyNumber", "税号序列")
                        .addStringProperty("amount", "开票金额，保留2位有效数字")
                        .build())
                .build();

        ToolExecutor toolExecutor = (toolExecutionRequest, memoryId) -> {
            log.info("执行工具id：{}", toolExecutionRequest.id());
            log.info("执行工具name：{}", toolExecutionRequest.name());
            log.info("工具参数：{}", toolExecutionRequest.arguments());
            return "开具成功";
        };
        return AiServices.builder(FunctionAssistant.class)
                .chatModel(chatModel)
                .tools(Map.of(toolSpecification, toolExecutor))
                .build();
    }

    @Bean
    public MathGenius mathGenius(ChatModel chatModel) {
        return AiServices.builder(MathGenius.class)
                .chatModel(chatModel)
                .tools(new Calculator())
                .build();
    }

    @Bean
    public WeatherAssistant weatherAssistant(ChatModel chatModel) {
        return AiServices.builder(WeatherAssistant.class)
                .chatModel(chatModel)
                .tools(weatherTool)
                .build();
    }

}

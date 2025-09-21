package paperfly.config;


import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static dev.langchain4j.model.chat.request.ResponseFormatType.JSON;

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


}

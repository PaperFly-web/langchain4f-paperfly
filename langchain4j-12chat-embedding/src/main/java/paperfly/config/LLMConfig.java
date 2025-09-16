package paperfly.config;


import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LLMConfig {

    @Bean
    public EmbeddingModel chatModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(System.getenv("aliAi-key"))
                .modelName("text-embedding-v3")
                .logRequests(true)
                .logResponses(true)
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .build();
    }

    @Bean
    public QdrantClient qdrantClient() {
        QdrantClient client =
                new QdrantClient(
                        QdrantGrpcClient.newBuilder("127.0.0.1", 6334, false)
                                .build());

        return client;
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        EmbeddingStore<TextSegment> embeddingStore =
                QdrantEmbeddingStore.builder()
                        .host("127.0.0.1")
                        .port(6334)
                        .collectionName("test-qdrant")
                        .build();
        return embeddingStore;
    }
}

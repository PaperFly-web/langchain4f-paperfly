package paperfly.config;


import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import paperfly.service.ChatAssistant;

@Configuration
@Slf4j
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
//    @Bean
//    public EmbeddingModel embeddingModel() {
//        return OpenAiEmbeddingModel.builder()
//                .apiKey(System.getenv("aliAi-key"))
//                .modelName("text-embedding-v3")
//                .logRequests(true)
//                .logResponses(true)
//                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
//                .build();
//    }

    @Bean("allMiniLmL6V2EmbeddingModel")
    public EmbeddingModel allMiniLmL6V2EmbeddingModel() {
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        return embeddingModel;
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
                        .collectionName("wx-estate")
                        .build();
        return embeddingStore;
    }


    @Bean
    public ChatAssistant chatAssistant(ChatModel chatModel, EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5).build();
        return AiServices
                .builder(ChatAssistant.class)
                .chatModel(chatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(contentRetriever)
                .build();
    }
}

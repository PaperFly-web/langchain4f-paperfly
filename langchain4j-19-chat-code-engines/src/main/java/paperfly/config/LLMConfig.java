package paperfly.config;


import dev.langchain4j.code.judge0.Judge0JavaScriptExecutionTool;
import dev.langchain4j.community.web.search.searxng.SearXNGWebSearchEngine;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.WebSearchTool;
import dev.langchain4j.web.search.searchapi.SearchApiWebSearchEngine;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import paperfly.service.ChatAssistant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class LLMConfig {
    @Autowired
    @Qualifier("mongodbChatMemoryStore")
    private ChatMemoryStore mongodbChatMemoryStore;

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
    public StreamingChatModel streamingChatModel() {
        return OpenAiStreamingChatModel.builder()
                .apiKey(System.getenv("aliAi-key"))
                .modelName("qwen-plus")
                .logRequests(true)
                .logResponses(true)
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .build();
    }
/*    @Bean
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(System.getenv("aliAi-key"))
                .modelName("text-embedding-v3")
                .logRequests(true)
                .logResponses(true)
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .build();
    }*/

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
                        .collectionName("estate-fcss")
                        .build();
        return embeddingStore;
    }

//    @Bean
//    SearchApiWebSearchEngine searchApiWebSearchEngine() {
//        Map<String, Object> optionalParameters = new HashMap<>();
//
//
//        return SearchApiWebSearchEngine.builder()
//                //https://www.searchapi.io/api/v1/search?engine=baidu
//                .apiKey(System.getenv("web-search-key"))
//                .engine("baidu")
////                .optionalParameters(optionalParameters)
//                .build();
//    }

    @Bean
    SearXNGWebSearchEngine searchApiWebSearchEngine() {
        Map<String, Object> optionalParameters = new HashMap<>();
        optionalParameters.put("language", "zh-CN");
        optionalParameters.put("format", "json");
        optionalParameters.put("enabled_engines", List.of("baidu"));
        SearXNGWebSearchEngine searXNGWebSearchEngine = SearXNGWebSearchEngine.builder()
                .baseUrl("http://localhost:8888")
                .logRequests(true)
                .logResponses(true)

                .build();

        return searXNGWebSearchEngine;
    }

    @Bean
    public ChatAssistant chatAssistant(ChatModel chatModel, StreamingChatModel streamingChatModel
            , EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel
            , WebSearchEngine webSearchEngine) {
        DefaultContentInjector contentInjector = DefaultContentInjector.builder()
                //自定义增强检索拼接逻辑
                .promptTemplate(PromptTemplate.from("用户消息：{{userMessage}}\n回复依据：{{contents}}"))
                .build();

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
//                .maxResults(3)
                // maxResults 也可以根据查询动态指定
                .dynamicMaxResults(query -> 3)
                .minScore(0.75)
                // minScore 也可以根据查询动态指定
//                .dynamicMinScore(query -> 0.75)
//                .filter(metadataKey("userId").isEqualTo("12345"))
                // filter 也可以根据查询动态指定
                /*.dynamicFilter(query -> {
                    String userId = getUserId(query.metadata().chatMemoryId());
                    return metadataKey("userId").isEqualTo(userId);
                })*/
                .build();

        //压缩历史对话
        CompressingQueryTransformer queryTransformer = new CompressingQueryTransformer(chatModel);

        //增强检索辅助器
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .contentInjector(contentInjector)
                .contentRetriever(contentRetriever)
                .queryTransformer(queryTransformer)
                .build();

        WebSearchTool webTool = WebSearchTool.from(webSearchEngine);

//        Judge0JavaScriptExecutionTool judge0Tool = new Judge0JavaScriptExecutionTool(ApiKeys.RAPID_API_KEY);

        return AiServices
                .builder(ChatAssistant.class)

                .chatModel(chatModel)
                .chatMemoryProvider(memoryId -> {
                    return MessageWindowChatMemory
                            .builder()
                            .id(memoryId)
                            .maxMessages(10)
                            .chatMemoryStore(mongodbChatMemoryStore)
                            .build();
                })
                .tools(webTool)
                .streamingChatModel(streamingChatModel)
                .retrievalAugmentor(retrievalAugmentor)
                .build();
    }
}

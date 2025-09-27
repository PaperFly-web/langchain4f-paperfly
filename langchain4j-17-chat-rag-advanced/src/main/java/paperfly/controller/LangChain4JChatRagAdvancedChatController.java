package paperfly.controller;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import io.qdrant.client.QdrantClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paperfly.service.ChatAssistant;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/lc4j")
@Slf4j
public class LangChain4JChatRagAdvancedChatController {
    @Autowired
    private EmbeddingModel embeddedModel;
    @Autowired
    private ChatAssistant chatAssistant;
    @Autowired
    private QdrantClient qdrantClient;
    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @RequestMapping(value = "/rag/advanced/ask")
    public Object ask() throws IOException {
        return chatAssistant.chat("00000是什么?");
    }

    @RequestMapping(value = "/rag/advanced/query")
    public Object query() throws IOException {
        Embedding queryEmbedding = embeddedModel.embed("00000是什么?").content();
        System.out.println("维度: " + queryEmbedding.vector().length);
        EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(1)
                .build();
        List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(embeddingSearchRequest).matches();
        EmbeddingMatch<TextSegment> embeddingMatch = matches.get(0);
        System.out.println(embeddingMatch.score());
        System.out.println(embeddingMatch.embedded().text());
        return embeddingMatch.embedded().text();
    }

    @RequestMapping(value = "/rag/advanced/query2")
    public Object query2() throws IOException {
        Embedding queryEmbedding = embeddedModel.embed("咏鸡说的是什么?").content();
        EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .filter(MetadataFilterBuilder.metadataKey("author").isEqualTo("paperfly2"))
                .maxResults(1)
                .build();
        List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(embeddingSearchRequest).matches();
        EmbeddingMatch<TextSegment> embeddingMatch = matches.get(0);
        System.out.println(embeddingMatch.score());
        System.out.println(embeddingMatch.embedded().text());
        return embeddingMatch.embedded().text();
    }
}

package paperfly.controller;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

@RestController
@RequestMapping("/lc4j")
@Slf4j
public class LangChain4JChatEmbeddingChatController {
    @Autowired
    private EmbeddingModel embeddedModel;
    @Autowired
    private QdrantClient qdrantClient;
    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @RequestMapping(value = "/embedding/chat")
    public Object chat(String prompt) throws IOException {
        Response<Embedding> embed = embeddedModel.embed(prompt);


        Embedding content = embed.content();
        String string = content.toString();
        log.info("string: {}", string);
        return string;
    }

    @RequestMapping(value = "/embedding/createCollection")
    public void createCollection() throws IOException {
        Collections.VectorParams vectorParams = Collections.VectorParams.newBuilder()
                .setDistance(Collections.Distance.Cosine)
                .setSize(1024)
                .build();
        qdrantClient.createCollectionAsync("test-qdrant", vectorParams);
    }

    @RequestMapping(value = "/embedding/add")
    public String add() throws IOException {
        String prompt = """
                咏鸭
                池塘日当午，
                鸭子叫声苦。
                水草觅身忙，
                羽毛随波舞。
                """;
        TextSegment segment1 = TextSegment.from(prompt);
        segment1.metadata().put("author","paperfly");
        Embedding embedding1 = embeddedModel.embed(segment1).content();
        String add = embeddingStore.add(embedding1, segment1);
        return add;
    }

    @RequestMapping(value = "/embedding/query")
    public Object query() throws IOException {
        Embedding queryEmbedding = embeddedModel.embed("咏鸡说的是什么?").content();
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

    @RequestMapping(value = "/embedding/query2")
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

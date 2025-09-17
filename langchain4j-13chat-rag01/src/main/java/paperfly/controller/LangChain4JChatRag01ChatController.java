package paperfly.controller;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.*;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
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
public class LangChain4JChatRag01ChatController {
    @Autowired
    private EmbeddingModel embeddedModel;
    @Autowired
    private ChatAssistant chatAssistant;
    @Autowired
    private QdrantClient qdrantClient;
    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @RequestMapping(value = "/rag01/chat")
    public Object chat(String prompt) throws IOException {
        Response<Embedding> embed = embeddedModel.embed(prompt);


        Embedding content = embed.content();
        String string = content.toString();
        log.info("string: {}", string);
        return string;
    }

    @RequestMapping(value = "/rag01/createCollection")
    public void createCollection() throws IOException {
        Collections.VectorParams vectorParams = Collections.VectorParams.newBuilder()
                .setDistance(Collections.Distance.Cosine)
                .setSize(1024)
                .build();
        qdrantClient.createCollectionAsync("doc-qdrant", vectorParams);
    }

    @RequestMapping(value = "/rag01/add")
    public String add() throws IOException {
        Document document = FileSystemDocumentLoader.loadDocument("D:\\01-doc\\公司文件\\2024_08_16_软件产品研发实施准则与规范v1.1\\Java开发手册(黄山版).pdf", new ApacheTikaDocumentParser());
        document.metadata().put("author", "paperfly");
        // 2. 按段落切分
        DocumentByParagraphSplitter splitter = new DocumentByParagraphSplitter(500,50);
        List<TextSegment> segments = splitter.split(document);

        // 3. 分批调用 embedding（一次最多 10 条）
        int batchSize = 10;
        for (int i = 0; i < segments.size(); i += batchSize) {
            int end = Math.min(i + batchSize, segments.size());
            List<TextSegment> batch = segments.subList(i, end);

            // 调用 embedding API
            List<Embedding> embeddings = embeddedModel.embedAll(batch).content();

            // 存入向量数据库
            embeddingStore.addAll(embeddings, batch);
        }
        return "Inserted " + segments.size() + " chunks into Qdrant";
    }

    @RequestMapping(value = "/rag01/ask")
    public Object ask() throws IOException {
        return chatAssistant.chat("00000是什么?");
    }

    @RequestMapping(value = "/rag01/query")
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

    @RequestMapping(value = "/rag01/query2")
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

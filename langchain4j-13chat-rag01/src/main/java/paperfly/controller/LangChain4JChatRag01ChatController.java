package paperfly.controller;

import com.google.common.util.concurrent.ListenableFuture;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Points;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paperfly.service.ChatAssistant;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
                .setSize(embeddedModel.dimension())
                .build();
        qdrantClient.createCollectionAsync("wx-estate", vectorParams);
    }

    @RequestMapping("/embeddingFile2")
    public String embeddingFile2() throws IOException, ExecutionException, InterruptedException {// 创建临时文件

        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.{java,jsp,html,css,xml,properties}");


        List<Document> documents = FileSystemDocumentLoader.loadDocumentsRecursively(
                "D:/project/estate-wx-20241122/estate-wx",
                pathMatcher,
                new TextDocumentParser());
        //下面这个循环：主要是测试从向量数据库查询数据，然后删除（场景：更新文档了后，删除之前的向量数据）
        for (Document document : documents) {
            document.metadata().put("doc_id", document.metadata().getString("absolute_directory_path") + "\\" + document.metadata().getString("file_name"));

            Points.FieldCondition fieldCondition = Points.FieldCondition.newBuilder()
                    .setKey("file_name")
                    .setMatch(Points.Match.newBuilder().setText(document.metadata().getString("file_name")))
                    .build();
            Points.Condition condition = Points.Condition.newBuilder()
                    .setField(fieldCondition)
                    .build();

            Points.Filter filter = Points.Filter.newBuilder()
                    .addMust(condition)
                    .build();
            //测试查询
            Points.ScrollPoints request = Points.ScrollPoints.newBuilder()
                    .setCollectionName("wx-estate")
                    .setFilter(filter)
                    .setLimit(5)
                    .build();
            Points.ScrollResponse scrollResponse = qdrantClient.scrollAsync(request).get();
            int resultCount = scrollResponse.getResultCount();
            System.out.println("resultCount:" + resultCount);

            //测试删除
            ListenableFuture<Points.UpdateResult> updateResultListenableFuture = qdrantClient.deleteAsync("wx-estate", filter);
            Points.UpdateResult updateResult = updateResultListenableFuture.get();
            System.out.println("updateResult:" + updateResult.getStatus());


        }
//        Document.from("sss", Metadata.from())
        DocumentByParagraphSplitter splitter = new DocumentByParagraphSplitter(500, 50);
        EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddedModel)
                .embeddingStore(embeddingStore)
                .documentSplitter(splitter)
                .build().ingest(documents);

        return "success";
    }

    @RequestMapping(value = "/rag01/add")
    public String add() throws IOException {
        Document document = FileSystemDocumentLoader.loadDocument("D:\\01-doc\\公司文件\\2024_08_16_软件产品研发实施准则与规范v1.1\\Java开发手册(黄山版).pdf", new ApacheTikaDocumentParser());
        document.metadata().put("author", "paperfly");
        // 2. 按段落切分
        DocumentByParagraphSplitter splitter = new DocumentByParagraphSplitter(500, 50);
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

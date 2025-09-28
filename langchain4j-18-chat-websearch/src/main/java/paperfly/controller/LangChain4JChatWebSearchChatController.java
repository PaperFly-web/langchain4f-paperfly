package paperfly.controller;

import cn.hutool.json.JSONUtil;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import io.qdrant.client.QdrantClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import paperfly.common.SseResponse;
import paperfly.service.ChatAssistant;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lc4j")
@Slf4j
public class LangChain4JChatWebSearchChatController {
    @Autowired
    private EmbeddingModel embeddedModel;
    @Autowired
    private ChatModel chatModel;
    @Autowired
    private ChatAssistant chatAssistant;
    @Autowired
    private QdrantClient qdrantClient;
    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @RequestMapping(value = "/rag/advanced/ask")
    public Object ask(@RequestParam String  question) throws IOException {
        Result<String> result = chatAssistant.chatResult(question);
        String answer = result.content();
        List<Content> sources = result.sources();
        return Map.of("answer", answer, "sources", sources.toString());
    }

    @RequestMapping(value = "/rag/advanced/askFlux")
    public Flux<String> askFlux(@RequestParam String  question) throws IOException {
        return chatAssistant.chatFluxString(question);
    }

    @RequestMapping(value = "/rag/advanced/ask2")
    public Flux<String> ask2(@RequestParam String  question) throws IOException {


        TokenStream tokenStream = chatAssistant.chatTokenStream(9l, question);
        return Flux.create(emitter -> {
            //最先执行,把检索到的内容发送给前端
            tokenStream.onRetrieved(sources -> {
                for (Content source : sources) {
                    emitter.next(SseResponse.builder()
                            .event("source")
                            .data(JSONUtil.toJsonStr(source.textSegment().toString())).build().toJsonString());
                }
                //有深度思考的时候执行
            }).onPartialThinking(partialThinking -> {
                emitter.next(SseResponse.thinking(partialThinking.text()).toJsonString());
                //有部分回答时候执行
            }).onPartialResponse(partialResponse -> {
                emitter.next(SseResponse.message(partialResponse).toJsonString());
                //回答完毕执行
            }).onCompleteResponse(completeResponse -> {
                emitter.next(SseResponse.end().toJsonString());
                emitter.complete();
            }).onError(error -> {
                emitter.next(SseResponse.error(error.getMessage()).toJsonString());
                emitter.error(error);
            }).start();
        });

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

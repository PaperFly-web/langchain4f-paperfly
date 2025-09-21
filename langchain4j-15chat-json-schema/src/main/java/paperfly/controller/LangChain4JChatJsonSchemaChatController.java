package paperfly.controller;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
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

import static dev.langchain4j.model.chat.request.ResponseFormatType.JSON;

@RestController
@RequestMapping("/lc4j")
@Slf4j
//DashScope  不支持 json-schema  详见：https://docs.langchain4j.dev/integrations/language-models/
public class LangChain4JChatJsonSchemaChatController {
    @Autowired
    private ChatModel chatModel;


    @RequestMapping(value = "/jsonSchema/chat")
    public Object chat(String prompt) throws IOException {
        ResponseFormat responseFormat = ResponseFormat.builder()
                .type(JSON) // 类型可以是 TEXT（默认）或 JSON
                .jsonSchema(JsonSchema.builder()
                        .name("Person") // OpenAI 要求为 schema 指定名称
                        .rootElement(JsonObjectSchema.builder() // 见下面的 [1]
                                .addStringProperty("name")
                                .addIntegerProperty("age","年龄")
                                .addNumberProperty("height","身高")
                                .addBooleanProperty("married")
                                .required("name", "age", "height", "married") // 见下面的 [2]
                                .build())
                        .build())
                .build();
        UserMessage userMessage = UserMessage.from(
//                必读包含json关键字
                "请以 JSON 格式返回一个 Person 对象，字段包括 name, age, height, married。内容如下：\n" +
                        "John is 42 years old and lives an independent life.\n" +
                        "He stands 1.75 meters tall and carries himself with confidence.\n" +
                        "Currently unmarried, he enjoys the freedom to focus on his personal goals and interests."
        );


        ChatRequest chatRequest = ChatRequest.builder()
                .responseFormat(responseFormat)
                .messages(userMessage)
                .build();
        ChatResponse chatResponse = chatModel.chat(chatRequest);
        return chatResponse.aiMessage().text();
    }
}

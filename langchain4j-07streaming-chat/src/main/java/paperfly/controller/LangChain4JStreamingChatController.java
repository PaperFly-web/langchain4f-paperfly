package paperfly.controller;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paperfly.service.ChatAssistant;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/lc4j")
@Slf4j
public class LangChain4JStreamingChatController {

    @Autowired
    private StreamingChatModel streamingChatModel;
    @Autowired
    private ChatAssistant chatAssistant;

    @RequestMapping(value = "/streaming/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatImage(String prompt) throws IOException {
        log.info("prompt: {}", prompt);
        return Flux.create(emitter -> {
            streamingChatModel.chat(prompt, new StreamingChatResponseHandler() {

                @Override
                public void onPartialResponse(String partialResponse) {
                    emitter.next(partialResponse);
                }

                @Override
                public void onCompleteResponse(ChatResponse completeResponse) {
                    emitter.complete();
                }

                @Override
                public void onError(Throwable error) {
                    emitter.error(error);
                }
            });
        });
    }

    @RequestMapping(value = "/streaming/chat2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatImage2(String prompt) throws IOException {
        log.info("prompt: {}", prompt);
        return chatAssistant.chat(prompt);
    }
}

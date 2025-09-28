package paperfly.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface ChatAssistant {
    Result<String> chatResult(String userMessage);
    String chatString(String userMessage);
    Flux<String> chatFluxString(String userMessage);
    TokenStream chatTokenStream(@MemoryId Long memoryId,@UserMessage String userMessage);
}

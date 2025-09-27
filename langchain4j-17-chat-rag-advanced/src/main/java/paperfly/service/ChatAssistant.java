package paperfly.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface ChatAssistant {
    Result<String> chatResult(String userMessage);
    String chatString(String userMessage);
    TokenStream chatTokenStream(@MemoryId Long memoryId,@UserMessage String userMessage);
}

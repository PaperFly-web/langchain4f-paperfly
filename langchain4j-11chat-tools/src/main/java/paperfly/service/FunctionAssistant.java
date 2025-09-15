package paperfly.service;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

public interface FunctionAssistant {
    String chat(String userMessage);

}

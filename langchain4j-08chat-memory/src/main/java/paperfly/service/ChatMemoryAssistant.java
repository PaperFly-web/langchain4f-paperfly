package paperfly.service;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

public interface ChatMemoryAssistant {
    String chatWithMemory(@MemoryId int memoryId, @UserMessage String userMessage);
}

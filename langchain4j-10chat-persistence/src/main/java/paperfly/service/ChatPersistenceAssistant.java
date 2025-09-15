package paperfly.service;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

public interface ChatPersistenceAssistant {
    String chat(@MemoryId int memoryId, @UserMessage String userMessage);

}

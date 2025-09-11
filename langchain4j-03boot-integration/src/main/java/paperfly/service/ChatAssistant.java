package paperfly.service;

import dev.langchain4j.service.spring.AiService;

@AiService
public interface ChatAssistant {
    String chat(String question);
}

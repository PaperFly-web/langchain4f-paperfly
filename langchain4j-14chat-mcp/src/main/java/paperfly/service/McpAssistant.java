package paperfly.service;

import reactor.core.publisher.Flux;

public interface McpAssistant {
    Flux<String> chat(String userMessage);
}

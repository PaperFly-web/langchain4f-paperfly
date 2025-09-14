package paperfly.service;


import reactor.core.publisher.Flux;

public interface ChatAssistant {
    Flux<String> chat(String question);
}

package paperfly.controller;

import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import paperfly.service.ChatAssistant;

@RestController
@Slf4j
public class LangChain4JBootController {
    @Autowired
    private ChatAssistant chatAssistant;

    @RequestMapping("/lc4f/boot/advanced")
    public String hello(@RequestParam(value = "question", defaultValue = "你是谁") String question) {
        String result = chatAssistant.chat(question);
        log.info("result:{}", result);
        return result;
    }
}

package paperfly.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paperfly.service.ChatAssistant;

import java.io.IOException;

@RestController
@RequestMapping("/lc4j")
@Slf4j
public class LangChain4JChatGuardrailChatController {
    @Autowired
    private ChatAssistant assistant;


    @RequestMapping(value = "/guardrail/chat2")
    public Object chat2(String prompt) throws IOException {
        return assistant.chat(prompt);
    }
}

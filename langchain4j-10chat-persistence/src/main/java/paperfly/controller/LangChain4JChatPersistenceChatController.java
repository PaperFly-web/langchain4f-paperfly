package paperfly.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paperfly.service.ChatPersistenceAssistant;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/lc4j")
@Slf4j
public class LangChain4JChatPersistenceChatController {

    @Autowired
    private ChatPersistenceAssistant chatPersistenceWindowChatMemory;

    @RequestMapping(value = "/prompt/chat")
    public Object chatImage(String prompt) throws IOException {
        String answer01 = chatPersistenceWindowChatMemory.chat(1, "我是cpp");
        String answer02 = chatPersistenceWindowChatMemory.chat(2, "我是java");

        String answer03 = chatPersistenceWindowChatMemory.chat(1, "我是谁？");
        String answer04 = chatPersistenceWindowChatMemory.chat(2, "你是谁？");

        Map<String, String> answerMap = new LinkedHashMap<>(); // 保证顺序
        answerMap.put("answer1", answer01);
        answerMap.put("answer2", answer02);
        answerMap.put("answer3", answer03);
        answerMap.put("answer4", answer04);
        return answerMap;
    }


}

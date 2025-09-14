package paperfly.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paperfly.service.ChatAssistant;
import paperfly.service.ChatMemoryAssistant;

import java.io.IOException;

@RestController
@RequestMapping("/lc4j")
@Slf4j
public class LangChain4JChatMemoryChatController {


    @Autowired
    private ChatAssistant chatAssistant;

    @Autowired
    @Qualifier("chatMemoryWindowChatMemory")
    private ChatMemoryAssistant chatMemoryWindowChatMemory;

    @Autowired
    @Qualifier("chatMemoryTokenWindowChatMemory")
    private ChatMemoryAssistant chatMemoryTokenWindowChatMemory;

    @RequestMapping(value = "/memory/chat")
    public String chatImage(String prompt) throws IOException {
        log.info("prompt1: {}", prompt);
        String chat = chatAssistant.chat("你好，我叫张三");
        String chat1 = chatAssistant.chat("我叫什么啊？");

        return "answer01:" + chat + "\nanswer2" + chat1;
    }

    @RequestMapping(value = "/memory/chat2")
    public String chatMemoryWindowChatMemory(String prompt) throws IOException {
        log.info("prompt2: {}", prompt);
        String chat = chatMemoryWindowChatMemory.chatWithMemory(1, "你好，我叫java");
        String s = chatMemoryWindowChatMemory.chatWithMemory(1, "我叫什么啊？");
        //换行展示

        return "answer02:" + chat + "\nanswer3" + s;
    }

    @RequestMapping(value = "/memory/chat3")
    public String chatMemoryTokenWindowChatMemory(String prompt) throws IOException {
        log.info("prompt3: {}", prompt);

        String chat = chatMemoryTokenWindowChatMemory.chatWithMemory(1, "你好，我叫java");
        String s = chatMemoryTokenWindowChatMemory.chatWithMemory(1, "我叫什么啊？");

        String chat2 = chatMemoryTokenWindowChatMemory.chatWithMemory(2, "你好，我叫Cpp");
        String s2 = chatMemoryTokenWindowChatMemory.chatWithMemory(2, "我叫什么啊？");
        return "answer03:" + chat + "\nanswer4" + s + "\nanswer5" + chat2 + "\nanswer6" + s2;
    }
}

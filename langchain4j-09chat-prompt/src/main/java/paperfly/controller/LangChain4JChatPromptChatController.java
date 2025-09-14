package paperfly.controller;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paperfly.bean.LawPrompt;
import paperfly.service.ChatAssistant;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/lc4j")
@Slf4j
public class LangChain4JChatPromptChatController {


    @Autowired
    private ChatAssistant chatAssistant;
    @Autowired
    private ChatModel chatModel;

    @RequestMapping(value = "/prompt/chat")
    public String chatImage(String prompt) throws IOException {
        log.info("prompt1: {}", prompt);
        String chat = chatAssistant.chat("什么是知识产权?", 100, "md");
        String chat2 = chatAssistant.chat("什么是java？", 100, "md");
        String chat3 = chatAssistant.chat("介绍一下水果西瓜和芒果", 100, "md");
        String chat4 = chatAssistant.chat("飞机发动机原理", 100, "md");

        return "answer01:" + chat + "\nanswer2" + chat2 + "\nanswer3" + chat3 + "\nanswer4" + chat4;
    }


    @RequestMapping(value = "/prompt/chat2")
    public String chat2(String prompt) throws IOException {
        LawPrompt lawPrompt = new LawPrompt();
        lawPrompt.setLegal("知识产权");
        lawPrompt.setQuestion("什么是知识产权?");
        String s = chatAssistant.chat2(lawPrompt);
        return s;
    }


    @RequestMapping(value = "/prompt/chat3")
    public String chat3(String prompt) throws IOException {
        PromptTemplate promptTemplate = PromptTemplate.from("根据中国{{legal}}法律，解答以下问题：{{question}}");
        Prompt apply = promptTemplate.apply(Map.of("legal", "知识产权", "question", "什么是知识产权?"));
        UserMessage userMessage = apply.toUserMessage();
        ChatResponse chatResponse = chatModel.chat(userMessage);
        return chatResponse.aiMessage().text();
    }
}

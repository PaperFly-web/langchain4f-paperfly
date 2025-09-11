package paperfly.controller;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.TokenUsage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import paperfly.service.ChatAssistant;

@RestController
@RequestMapping("/lc4j")
@Slf4j
public class LangChain4JChatImageController {
    @Autowired
    @Qualifier("deepseek")
    private ChatModel deepseekChatModel;

    @Autowired
    @Qualifier("qwen")
    private ChatModel qwenChatModel;

    @Autowired
    private ChatAssistant chatAssistant;

    @RequestMapping("/chat")
    public String chat(@RequestParam(value = "question", defaultValue = "你是谁") String question) {
        String result = qwenChatModel.chat(question);
        log.info("result:{}", result);
        return result;
    }

    @RequestMapping("/lowapi/api02")
    public String api02(@RequestParam(value = "question", defaultValue = "你是谁") String question){
        ChatResponse chatResponse = qwenChatModel.chat(UserMessage.from(question));
        String result = chatResponse.aiMessage().text();
        log.info("result:{}", result);

        TokenUsage tokenUsage = chatResponse.tokenUsage();
        log.info("tokenUsage:{}", tokenUsage);
        return result;
    }

    @RequestMapping("/hightapi/api03")
    public String api03(@RequestParam(value = "question", defaultValue = "你是谁") String question){
        String result = chatAssistant.chat(question);
        log.info("result:{}", result);
        return result;
    }

}

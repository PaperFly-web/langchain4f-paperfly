package paperfly.controller;

import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/langchain4j")
@Slf4j
public class HelloLangChain4JMultiModelTogetherController {
    @Autowired
    @Qualifier("deepseek")
    private ChatModel deepseekChatModel;

    @Autowired
    @Qualifier("qwen")
    private ChatModel qwenChatModel;

    @RequestMapping("/multimodel/qwen")
    public String qwenChat(@RequestParam(value = "question", defaultValue = "你是谁") String question) {
        String result = qwenChatModel.chat(question);
        log.info("result:{}", result);
        return result;
    }

    @RequestMapping("/multimodel/deepseek")
    public String deepseekChat(@RequestParam(value = "question", defaultValue = "你是谁") String question) {
        String result = deepseekChatModel.chat(question);
        log.info("result:{}", result);
        return result;
    }
}

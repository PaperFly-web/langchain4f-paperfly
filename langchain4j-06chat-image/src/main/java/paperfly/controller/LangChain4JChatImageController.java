package paperfly.controller;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.image.ImageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/lc4j")
@Slf4j
public class LangChain4JChatImageController {

    @Autowired
    private ImageModel imageModel;
    @Autowired
    private ChatModel chatModel;
    @Value("classpath:static/img.png")
    private Resource resource;

    @RequestMapping("/chatImage")
    public String chatImage() throws IOException {
        byte[] contentAsByteArray = resource.getContentAsByteArray();
        String base64Data = Base64.getEncoder().encodeToString(contentAsByteArray);

        UserMessage userMessage = UserMessage.from(
                TextContent.from("从下面图片中获取9.30的交易量"),
                ImageContent.from(base64Data, "image/png"));

        ChatResponse chatResponse = chatModel.chat(userMessage);

        String text = chatResponse.aiMessage().text();
        log.info("text:{}", text);
        return text;
    }
}

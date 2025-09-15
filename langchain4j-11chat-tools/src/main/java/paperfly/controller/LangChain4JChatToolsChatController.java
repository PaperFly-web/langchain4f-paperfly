package paperfly.controller;

import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.tool.ToolExecution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paperfly.service.FunctionAssistant;
import paperfly.service.MathGenius;
import paperfly.service.WeatherAssistant;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/lc4j")
@Slf4j
public class LangChain4JChatToolsChatController {
    @Autowired
    private FunctionAssistant functionAssistant;
    @Autowired
    private MathGenius mathGenius;
    @Autowired
    private WeatherAssistant weatherAssistant;

    @RequestMapping(value = "/function/chat")
    public Object chat(String prompt) throws IOException {
        String message = "请帮我开一张发票，公司名称是“金桥科技有限公司”，税号是“1234567890ABCDEF”，开票金额是15800.50元。";
        return functionAssistant.chat(message);

    }


    @RequestMapping(value = "/function/chat2")
    public Object chat2(String prompt) throws IOException {
        return mathGenius.ask(prompt);
    }

    @RequestMapping(value = "/function/chat3")
    public Object chat3(String prompt) throws IOException {
        Result<String> ask = weatherAssistant.ask(prompt);
        List<ToolExecution> toolExecutions = ask.toolExecutions();
        for (ToolExecution toolExecution : toolExecutions) {
            log.info(toolExecution.result());
        }
        return ask.content();
    }
}

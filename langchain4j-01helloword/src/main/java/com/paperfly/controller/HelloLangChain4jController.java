package com.paperfly.controller;

import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/langchain4j")
@Slf4j
public class HelloLangChain4jController {
    @Autowired
    private ChatModel chatModel;

    @RequestMapping("/hello")
    public String hello(@RequestParam(value = "question", defaultValue = "你是谁") String question) {
        String result = chatModel.chat(question);
        log.info("result:{}", result);
        return result;
    }
}

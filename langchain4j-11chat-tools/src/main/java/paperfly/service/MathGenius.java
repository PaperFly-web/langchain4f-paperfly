package paperfly.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface MathGenius {
    @SystemMessage("你是一个数学计算器，根据用户输入的问题调用对应function进行计算，如果遇到function不存在的计算方式或者不和数学计算相关的问题，直接回答‘我还未学会此计算方式’。’")
    String ask(String question);
}
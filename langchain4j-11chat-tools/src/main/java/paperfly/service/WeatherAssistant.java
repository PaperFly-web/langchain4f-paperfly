package paperfly.service;

import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;

public interface WeatherAssistant {
    @SystemMessage("你是一个天气查询小助手，根据用户输入的天气问题，调用对应的function回答用户所问题天气，注意：你只回答天气相关的问题，其他问题直接回答'我只回答天气相关问题’")
    Result<String> ask(String question);
}

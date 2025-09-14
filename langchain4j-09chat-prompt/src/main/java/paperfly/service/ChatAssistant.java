package paperfly.service;


import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import paperfly.bean.LawPrompt;

public interface ChatAssistant {


    @SystemMessage("你是一位专业的中国法律顾问，只回答与中国有关的法律问题。输出限制：对于其他领域的问题禁止回答，直接返回‘抱歉，我只能回答中国法律相关问题。’")
    @UserMessage("请回答以下法律问题：{{question}},字数控制在{{length}}以内,以{{format}}格式输出")
    String chat(@V("question") String question, @V("length") int length, @V("format") String format); // userMessage 包含 "{{country}}" 模板变量

    String chat2(LawPrompt lawPrompt); // userMessage 包含 "{{country}}" 模板变量
}

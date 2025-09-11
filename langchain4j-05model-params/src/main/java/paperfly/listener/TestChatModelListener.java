package paperfly.listener;

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class TestChatModelListener implements ChatModelListener {

    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        String uuid = UUID.randomUUID().toString();
        requestContext.attributes().put("requestId", uuid);
        log.info("请求--》requestId: {}", uuid);
    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        Object requestId = responseContext.attributes().get("requestId");
        log.info("响应--》requestId: {}", requestId);
    }

    @Override
    public void onError(ChatModelErrorContext errorContext) {
        log.error("error: {}", errorContext.error());
    }
}

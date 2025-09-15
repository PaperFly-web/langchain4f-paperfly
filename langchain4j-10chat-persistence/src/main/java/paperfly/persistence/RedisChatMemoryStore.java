package paperfly.persistence;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class RedisChatMemoryStore implements ChatMemoryStore {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static final String KEY_PREFIX = "chat_memory:";

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String retValue = redisTemplate.opsForValue().get(KEY_PREFIX + memoryId);
        return ChatMessageDeserializer.messagesFromJson(retValue);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String retValue = ChatMessageSerializer.messagesToJson(messages);
        redisTemplate.opsForValue().set(KEY_PREFIX + memoryId, retValue);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        redisTemplate.delete(KEY_PREFIX + memoryId);
    }
}
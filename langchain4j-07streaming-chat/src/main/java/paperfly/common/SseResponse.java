package paperfly.common;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

@Getter
public class SseResponse<T> {
    private String event;
    private T data;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private SseResponse() {
    }

    /**
     * 创建 message 类型事件
     */
    public static <T> SseResponse<T> message(T data) {
        SseResponse<T> resp = new SseResponse<>();
        resp.event = "message";
        resp.data = data;
        return resp;
    }

    /**
     * 创建业务数据事件
     */
    public static <T> SseResponse<T> businessData(T data) {
        SseResponse<T> resp = new SseResponse<>();
        resp.event = "businessData";
        resp.data = data;
        return resp;
    }

    /**
     * 创建结束事件
     */
    public static SseResponse<String> end() {
        SseResponse<String> resp = new SseResponse<>();
        resp.event = "end";
        resp.data = "[DONE]";
        return resp;
    }

    /**
     * 创建错误事件
     */
    public static SseResponse<String> error(String msg) {
        SseResponse<String> resp = new SseResponse<>();
        resp.event = "error";
        resp.data = msg;
        return resp;
    }

    /**
     * 转换为 SSE 格式字符串
     */
    public String toSseString() {
        try {
            return "event: " + event + "\n" +
                    "data: " + OBJECT_MAPPER.writeValueAsString(data) + "\n\n";
        } catch (Exception e) {
            throw new RuntimeException("序列化 SSE 数据失败", e);
        }
    }

    /**
     * 转换为 SSE 格式字符串
     */
    public String toJsonString() {
        String jsonStr = JSONUtil.toJsonStr(this);
        return jsonStr;
    }
}

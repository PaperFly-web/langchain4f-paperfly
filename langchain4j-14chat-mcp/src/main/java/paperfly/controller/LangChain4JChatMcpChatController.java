package paperfly.controller;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paperfly.service.McpAssistant;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lc4j")
@Slf4j
public class LangChain4JChatMcpChatController {
    @Autowired
    private StreamingChatModel streamingChatModel;


    @RequestMapping(value = "/mcp/chat")
    public Flux<String> chat(String prompt) throws IOException {
        /*McpTransport transport = new StdioMcpTransport.Builder()
                .command(List.of("cmd", "/c", "npx", "-y", "@amap/amap-maps-mcp-server"))
                .environment(Map.of("AMAP_MAPS_API_KEY", System.getenv("GAODE_MAP_KEY")))
                .logEvents(true) // only if you want to see the traffic in the log
                .build();*/

        McpTransport transport2 = new StdioMcpTransport.Builder()
                .command(List.of("cmd", "/c", "npx", "-y", "@smithery/cli@latest","run"
                        ,"@leehanchung/bing-search-mcp","--key","653d496e-5ac2-422b-96c8-a563ad35cda0",
                        "--profile","related-toucan-1vlWpf"))
//                .environment(Map.of("AMAP_MAPS_API_KEY", System.getenv("GAODE_MAP_KEY")))
                .logEvents(true) // only if you want to see the traffic in the log
                .build();


        McpClient mcpClient = new DefaultMcpClient.Builder()
//                .key("MyMCPClient")
                .transport(transport2)
                .build();


        McpToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(mcpClient)
//                .filterToolNames("get_issue", "get_issue_comments", "list_issues")
                .build();

        McpAssistant mcpAssistant = AiServices.builder(McpAssistant.class)
                .streamingChatModel(streamingChatModel)
                .toolProvider(toolProvider)
                .build();
        return mcpAssistant.chat(prompt);
    }


    @RequestMapping(value = "/mcp/chat2")
    public Flux<String> chat2(String prompt) throws IOException {
        McpTransport transport = new StdioMcpTransport.Builder()
                .command(List.of("cmd", "/c", "python", "-m", "mcp_server_wechat","--folder-path=F:\\xwechat_files"))
                .logEvents(true) // only if you want to see the traffic in the log
                .build();


        McpClient mcpClient = new DefaultMcpClient.Builder()
//                .key("MyMCPClient")
                .transport(transport)
                .build();


        McpToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(mcpClient)
//                .filterToolNames("get_issue", "get_issue_comments", "list_issues")
                .build();

        McpAssistant mcpAssistant = AiServices.builder(McpAssistant.class)
                .streamingChatModel(streamingChatModel)
                .toolProvider(toolProvider)
                .build();
        return mcpAssistant.chat(prompt);
    }


}

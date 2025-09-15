package paperfly.tools;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.output.structured.Description;
import lombok.extern.slf4j.Slf4j;

@Description("数学计算器")
@Slf4j
public class Calculator {
    
    @Tool(name = "加法计算器")
    double add(int a, int b) {
        log.info("加法计算器: {} + {}", a, b);
        return a + b;
    }

    @Tool(name = "平方根计算器")
    double squareRoot(double x) {
        log.info("平方根计算器: {}", x);
        return Math.sqrt(x);
    }
}
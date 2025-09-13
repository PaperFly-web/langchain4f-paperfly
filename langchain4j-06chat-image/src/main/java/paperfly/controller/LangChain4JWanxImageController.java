package paperfly.controller;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import dev.langchain4j.community.model.dashscope.WanxImageModel;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;

@RestController
@RequestMapping("/lc4j")
@Slf4j
public class LangChain4JWanxImageController {

    @Autowired
    private WanxImageModel wanxImageModel;

    @RequestMapping("/wanxImage")
    public String chatImage() throws IOException {
        Response<Image> generate = wanxImageModel.generate("生成一个美女");
        Image content = generate.content();
        URI url = content.url();
        log.info("url: {}", url);
        return url.toString();
    }
    @RequestMapping("/wanxImage2")
    public String wanxImage2() throws IOException {
        String prompt = "一间有着精致雕花窗户的花店，漂亮的深色木质门微微敞开。店内摆放着各式各样的鲜花，包括玫瑰、百合和向日葵，色彩鲜艳，香气扑鼻。背景是温馨的室内场景，光线柔和，透过窗户洒在花朵上。高清写实摄影，中景构图。";
        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        .apiKey(System.getenv("aliAi-key"))
                        .model(ImageSynthesis.Models.WANX_V1)
                        .prompt(prompt)
                        .style("<watercolor>")
                        .n(1)
                        .size("1024*1024")
                        .build();

        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            System.out.println("---sync call, please wait a moment----");
            result = imageSynthesis.call(param);
        } catch (ApiException | NoApiKeyException e){
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(JsonUtils.toJson(result));
        return JsonUtils.toJson(result);
    }

}

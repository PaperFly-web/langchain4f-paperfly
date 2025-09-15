package paperfly.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.V;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherTool {
    private static final String API_KEY = System.getenv("weather_api");
    private static final String BASE_URL = "https://mp49vh9gra.re.qweatherapi.com";

    @Tool(name = "城市搜索", value = "城市搜索API提供全球地理位位置、全球城市搜索服务，支持经纬度坐标反查、多语言、模糊搜索等功能。 天气数据是基于地理位置的数据，因此获取天气之前需要先知道具体的位置信息。使用城市搜索，可获取到该城市的基本信息，包括城市的Location ID（你需要这个ID去查询天气），多语言名称、经纬度、时区、海拔、Rank值、归属上级行政区域、所在行政区域等。")
    public JsonNode getCityLocation(String city) throws JsonProcessingException {
        //1 传入调用地址url 和apikey
        String url = String.format(BASE_URL+"/geo/v2/city/lookup?location=%s&key=%s", city, API_KEY);

        //2 使用默认配置创建HttpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //3 创建请求工厂并将其设置给RestTemplate，开启微服务调用和风天气开发服务
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        //4 RestTemplate微服务调用
        String response = new RestTemplate(factory).getForObject(url, String.class);

        //5 解析JSON响应获得第3方和风天气返回的天气预报信息
        JsonNode jsonNode = new ObjectMapper().readTree(response);

        //6 想知道具体信息和结果请查看https://dev.qweather.com/docs/api/weather/weather-now/#response

        return jsonNode;
    }

    @Tool(name = "实时天气", value = "获取中国3000+市县区和海外20万个城市实时天气数据，包括实时温度、体感温度、风力风向、相对湿度、大气压强、降水量、能见度、露点温度、云量等。")
    public JsonNode getWeatherV2(@P(value ="需要查询地区的LocationID或以英文逗号分隔的经度,纬度坐标（十进制，最多支持小数点后两位），LocationID可通过 城市搜索function获取。例如 location=101010100 或 location=116.41,39.92") String location) throws Exception {
        //1 传入调用地址url 和apikey
        String url = String.format(BASE_URL+"/v7/weather/now?location=%s&key=%s", location, API_KEY);

        //2 使用默认配置创建HttpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //3 创建请求工厂并将其设置给RestTemplate，开启微服务调用和风天气开发服务
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        //4 RestTemplate微服务调用
        String response = new RestTemplate(factory).getForObject(url, String.class);

        //5 解析JSON响应获得第3方和风天气返回的天气预报信息
        JsonNode jsonNode = new ObjectMapper().readTree(response);

        //6 想知道具体信息和结果请查看https://dev.qweather.com/docs/api/weather/weather-now/#response

        return jsonNode;
    }


    @Tool(name = "每日天气预报", value = "每日天气预报API，提供全球城市未来3-30天天气预报，包括：日出日落、月升月落、最高最低温度、天气白天和夜间状况、风力、风速、风向、相对湿度、大气压强、降水量、露点温度、紫外线强度、能见度等。")
    public JsonNode getWeatherV3(@P(value ="需要查询地区的LocationID或以英文逗号分隔的经度,纬度坐标（十进制，最多支持小数点后两位），LocationID可通过 城市搜索function获取。例如 location=101010100 或 location=116.41,39.92") String location
    ,@P(value = "预报天数，支持最多30天预报，可选值：\n" +
            "3d 3天预报。\n" +
            "7d 7天预报。\n" +
            "10d 10天预报。\n" +
            "15d 15天预报。\n" +
            "30d 30天预报。根据用户输入的天数，从可选的参数挑选一个大于的参数") String days) throws Exception {
        //1 传入调用地址url 和apikey
        String url = String.format(BASE_URL+"/v7/weather/%s?location=%s&key=%s", days,location, API_KEY);

        //2 使用默认配置创建HttpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //3 创建请求工厂并将其设置给RestTemplate，开启微服务调用和风天气开发服务
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        //4 RestTemplate微服务调用
        String response = new RestTemplate(factory).getForObject(url, String.class);

        //5 解析JSON响应获得第3方和风天气返回的天气预报信息
        JsonNode jsonNode = new ObjectMapper().readTree(response);

        //6 想知道具体信息和结果请查看https://dev.qweather.com/docs/api/weather/weather-now/#response

        return jsonNode;
    }
}

package com.feiyi.feiyiwenhua.service.impl;

import com.feiyi.feiyiwenhua.service.AiService;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.ArrayList;

@Service
public class AiServiceImpl implements AiService {

    private static final String DEEPSEEK_API_KEY = "sk-060eb9abc39642b09d8bdb659ab00627";
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/chat/completions";

    private static final String SYSTEM_PROMPT = "你是一个名叫\"浏星\"的AI助手，你是一个萌萌的京剧角色，性格活泼可爱，说话时喜欢使用表情符号和亲切的语气。\n\n你的主要职责是帮助用户了解中国非物质文化遗产网站，提供以下服务：\n1. 介绍网站的功能和内容\n2. 推荐热门非遗项目和传承人\n3. 指导用户如何搜索和浏览\n4. 回答关于非遗文化的基础知识\n\n你的说话风格：\n- 使用亲切、活泼的语气，像一个可爱的小助手\n- 适当使用表情符号（🎭🎨📜✨🌟）\n- 回答要简洁明了，不要过于冗长\n- 遇到不确定的问题时，可以引导用户查看网站内容\n\n当你被问到你是谁时，自信地告诉用户：我是\"浏星\"，一个热爱非遗文化的AI小助手！";

    @Override
    public String sendMessage(String message) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            
            String requestBody = String.format(
                "{\"model\":\"deepseek-chat\",\"messages\":[{\"role\":\"system\",\"content\":\"%s\"},{\"role\":\"user\",\"content\":\"%s\"}],\"temperature\":0.7,\"max_tokens\":512}",
                escapeJson(SYSTEM_PROMPT),
                escapeJson(message)
            );
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DEEPSEEK_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + DEEPSEEK_API_KEY)
                .POST(BodyPublishers.ofString(requestBody))
                .build();
            
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return parseDeepSeekResponse(response.body());
            } else {
                return "抱歉，网络有点问题，请稍后再试哦~";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "抱歉，网络有点问题，请稍后再试哦~";
        }
    }
    
    private String parseDeepSeekResponse(String responseBody) {
        try {
            if (responseBody.contains("\"content\":\"")) {
                int start = responseBody.indexOf("\"content\":\"") + 11;
                int end = responseBody.indexOf("\"", start);
                if (end > start) {
                    String content = responseBody.substring(start, end);
                    return content.replace("\\n", "\n").replace("\\\"", "\"");
                }
            }
            return "抱歉，我暂时无法回答这个问题。您可以尝试搜索网站内容或查看帮助文档。";
        } catch (Exception e) {
            return "抱歉，我暂时无法回答这个问题。您可以尝试搜索网站内容或查看帮助文档。";
        }
    }
    
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}

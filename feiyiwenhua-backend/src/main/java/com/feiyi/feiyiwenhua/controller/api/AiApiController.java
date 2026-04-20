package com.feiyi.feiyiwenhua.controller.api;

import com.feiyi.feiyiwenhua.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiApiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody AiMessage request) {
        try {
            String response = aiService.sendMessage(request.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("抱歉，网络有点问题，请稍后再试哦~");
        }
    }

    public static class AiMessage {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

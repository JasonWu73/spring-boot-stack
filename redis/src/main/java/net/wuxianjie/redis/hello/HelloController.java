package net.wuxianjie.redis.hello;

import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HelloController {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper jsonMapper;

    public HelloController(StringRedisTemplate redisTemplate, ObjectMapper jsonMapper) {
        this.redisTemplate = redisTemplate;
        this.jsonMapper = jsonMapper;
    }

    @GetMapping("/hello")
    public Hello hello() throws JsonProcessingException {
        Hello data = new Hello("你好 Redis", LocalDateTime.now());
        String json = jsonMapper.writeValueAsString(data);
        redisTemplate.opsForValue().set("hello", json);
        String response = redisTemplate.opsForValue().get("hello");
        return jsonMapper.readValue(response, Hello.class);
    }
}

package net.wuxianjie.rabbitmqproducer.picture;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mypicture")
@RequiredArgsConstructor
public class MyPictureProducer {

    private final List<String> types = List.of("jpg", "png", "svg");
    private final List<String> sources = List.of("web", "mobile");

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @GetMapping
    public String sendMessage() throws JsonProcessingException {
        for (var i = 1; i <= 10; i++) {
            var picture = new Picture(
                    "pic-" + i,
                    types.get(i % types.size()),
                    sources.get(i % sources.size()),
                    ThreadLocalRandom.current().nextLong(9_001, 10_000)
            );
            rabbitTemplate.convertAndSend(
                    "x.mypicture", picture.getType(),
                    objectMapper.writeValueAsString(picture)
            );
        }
        return "发送图片数据成功";
    }

}
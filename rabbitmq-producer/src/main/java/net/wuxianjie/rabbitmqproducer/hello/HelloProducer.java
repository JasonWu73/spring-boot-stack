package net.wuxianjie.rabbitmqproducer.hello;

import java.util.concurrent.ThreadLocalRandom;

import lombok.RequiredArgsConstructor;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HelloProducer {

    private final RabbitTemplate rabbitTemplate;

    @GetMapping("/hello")
    public String hello() {
        rabbitTemplate.convertAndSend(
                "course.hello",
                "你好，" + ThreadLocalRandom.current().nextInt()
        );
        return "发送消息至 course.hello 队列";
    }

}
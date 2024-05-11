package net.wuxianjie.rabbitmqproducer.hello;

import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HelloProducer {

    private final static Logger LOG = LoggerFactory.getLogger(HelloProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public HelloProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/hello")
    public String hello() {
        rabbitTemplate.convertAndSend(
            "q.hello",
            "你好，" + ThreadLocalRandom.current().nextInt()
        );
        return "发送消息至 q.hello 队列";
    }
}

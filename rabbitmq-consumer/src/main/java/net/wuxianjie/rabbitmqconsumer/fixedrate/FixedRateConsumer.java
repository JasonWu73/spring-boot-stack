package net.wuxianjie.rabbitmqconsumer.fixedrate;

import lombok.RequiredArgsConstructor;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FixedRateConsumer {

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "course.fixedrate")
    public void receive(String message) {
        System.out.println("接收消息：" + message);
    }

}
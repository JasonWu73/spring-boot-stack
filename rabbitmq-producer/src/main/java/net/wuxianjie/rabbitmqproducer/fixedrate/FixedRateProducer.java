package net.wuxianjie.rabbitmqproducer.fixedrate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

//@Service
@RequiredArgsConstructor
@Slf4j
public class FixedRateProducer {

    private final RabbitTemplate rabbitTemplate;

    private int count = 0;

    @Scheduled(fixedRate = 500)
    public void send() {
        count++;
        log.info("发送消息：{}", count);
        rabbitTemplate.convertAndSend("course.fixedrate", "这是第" + count + "条消息");
    }

}
package net.wuxianjie.rabbitmqconsumer.fixedrate;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

//@Service
@Slf4j
public class FixedRateConsumer {

    @RabbitListener(queues = "course.fixedrate", concurrency = "3-7")
    public void receive(String message) throws InterruptedException {
        log.info("线程 [{}] 接收消息：{}", Thread.currentThread().getName(), message);
        TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextLong(2_000));
    }

}
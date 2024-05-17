package net.wuxianjie.rabbitmqconsumer.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class HelloConsumer {

    private final static Logger LOG = LoggerFactory.getLogger(HelloConsumer.class);

    @RabbitListener(queues = "q.hello")
    public void receive(String message) {
        LOG.info("接收到消息：{}", message);
    }
}

package net.wuxianjie.rabbitmqconsumer.hello;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

//@Service
public class HelloConsumer {

    @RabbitListener(queues = "course.hello")
    public void receive(String message) {
        System.out.println("接收到消息：" + message);
    }

}

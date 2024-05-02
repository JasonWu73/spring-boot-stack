package net.wuxianjie.rabbitmqconsumer.picture;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.rabbitmq.client.Channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPictureImageConsumer {

    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "q.mypicture.image")
    public void listen(
            String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag
    ) throws IOException {
        var picture = objectMapper.readValue(message, Picture.class);
        if (picture.getSize() > 9_000) {
            channel.basicReject(tag, false);
            return;
        }
        log.info("处理图片: {}", picture);
        channel.basicAck(tag, false);
    }

}
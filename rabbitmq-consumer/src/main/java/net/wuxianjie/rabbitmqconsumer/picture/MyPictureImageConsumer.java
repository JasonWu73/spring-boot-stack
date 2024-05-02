package net.wuxianjie.rabbitmqconsumer.picture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPictureImageConsumer {

    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "q.mypicture.image")
    public void listen(String message) throws JsonProcessingException {
        var picture = objectMapper.readValue(message, Picture.class);
        if (picture.getSize() > 9_000) {
            throw new AmqpRejectAndDontRequeueException("图片太大: " + picture);
        }
        log.info("处理图片: {}", picture);
    }

}
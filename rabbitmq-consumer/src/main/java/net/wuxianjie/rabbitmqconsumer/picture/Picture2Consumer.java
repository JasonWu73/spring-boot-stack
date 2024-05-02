package net.wuxianjie.rabbitmqconsumer.picture;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class Picture2Consumer {

    private final ObjectMapper objectMapper;

    @RabbitListener(queues = {
            "q.picture.image", "q.picture.vector", "q.picture.filter", "q.picture.log"
    })
    public void listen(Message message) throws IOException {
        var picture = objectMapper.readValue(message.getBody(), Picture.class);
        log.info("处理图片: {}, 路由键: {}",
                picture, message.getMessageProperties().getReceivedRoutingKey());
    }

}
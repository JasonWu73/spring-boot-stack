package net.wuxianjie.rabbitmqconsumer.picture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PictureVectorConsumer {

    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "q.picture.vector")
    public void listen(String message) throws JsonProcessingException {
        var picture = objectMapper.readValue(message, Picture.class);
        log.info("处理矢量: {}", picture);
    }

}
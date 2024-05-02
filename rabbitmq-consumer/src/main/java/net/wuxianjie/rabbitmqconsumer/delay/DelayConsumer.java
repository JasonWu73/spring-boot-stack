package net.wuxianjie.rabbitmqconsumer.delay;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

//@Service
@RequiredArgsConstructor
@Slf4j
public class DelayConsumer {

    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "q.delayed")
    public void listener(String message) {
        try {
            var report = objectMapper.readValue(message, ReportRequest.class);
            log.info("Received: {}", report);
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

}
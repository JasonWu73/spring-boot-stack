package net.wuxianjie.rabbitmqproducer.delay;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DelayProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @GetMapping("/delay")
    public void delay() {
        for (int i = 0; i < 4; i++) {
            var report = new ReportRequest();
            report.setReportName("报告 " + i);
            report.setLarge(i % 2 == 0);
            sendMessage(report);
        }
    }

    private void sendMessage(ReportRequest report) {
        var props = new MessageProperties();
        var delayMillis = 0;
        if (report.getLarge()) {
            delayMillis =  30 * 1000;
        }
        props.setHeader("x-delay", delayMillis);
        String json;
        try {
            json = objectMapper.writeValueAsString(report);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        var msg = new Message(json.getBytes(StandardCharsets.UTF_8), props);
        rabbitTemplate.send("x.delayed.message", "delayThis", msg);
    }

}
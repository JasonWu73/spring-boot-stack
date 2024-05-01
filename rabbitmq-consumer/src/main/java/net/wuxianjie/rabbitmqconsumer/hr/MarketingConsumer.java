package net.wuxianjie.rabbitmqconsumer.hr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.rabbitmqconsumer.employee.Employee;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

//@Service
@RequiredArgsConstructor
@Slf4j
public class MarketingConsumer {

    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "q.hr.marketing")
    public void listen(String message) throws JsonProcessingException {
        var emp = objectMapper.readValue(message, Employee.class);
        log.info("市场营销部门收到消息: {}", emp);
    }

}
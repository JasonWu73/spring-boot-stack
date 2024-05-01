package net.wuxianjie.rabbitmqconsumer.hr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import net.wuxianjie.rabbitmqconsumer.employee.Employee;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountingConsumer {

    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "q.hr.accounting")
    public void listen(String message) throws JsonProcessingException {
        var emp = objectMapper.readValue(message, Employee.class);
        log.info("会计部门收到消息: {}", emp);
    }

}
package net.wuxianjie.rabbitmqproducer.employee;

import java.time.LocalDate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @GetMapping
    public String send() throws JsonProcessingException {
        for (var i = 0; i < 5; i++) {
            var emp = new Employee("emp-" + i, "员工-" + i, LocalDate.now());
            var json = objectMapper.writeValueAsString(emp);
            rabbitTemplate.convertAndSend("course.employee", json);
        }
        return "发送成功";
    }

}
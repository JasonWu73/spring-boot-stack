package net.wuxianjie.rabbitmqproducer.employee;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Employee {

    private String employeeId;
    private String name;
    private LocalDate birthDate;

}
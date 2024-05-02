package net.wuxianjie.rabbitmqconsumer.delay;

import lombok.Data;

@Data
public class ReportRequest {

    private String reportName;
    private Boolean large;

}
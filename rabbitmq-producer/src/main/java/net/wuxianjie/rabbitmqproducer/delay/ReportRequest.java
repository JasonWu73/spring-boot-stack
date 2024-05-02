package net.wuxianjie.rabbitmqproducer.delay;

import lombok.Data;

@Data
public class ReportRequest {

    private String reportName;
    private Boolean large;

}
package net.wuxianjie.rabbitmqproducer.sweepdirty;

import lombok.Data;

@Data
public class RabbitmqQueue {

    private String name;
    private long messages;

    public boolean isDirty() {
        return messages > 0;
    }

}
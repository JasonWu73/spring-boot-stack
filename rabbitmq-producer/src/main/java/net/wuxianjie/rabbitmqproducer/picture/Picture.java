package net.wuxianjie.rabbitmqproducer.picture;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Picture {

    private String name;
    private String type;
    private String source;
    private Long size;

}
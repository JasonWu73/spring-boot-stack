package net.wuxianjie.rabbitmqproducer.furniture;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Furniture {

    private String color;
    private String material;
    private String name;
    private Double price;

}
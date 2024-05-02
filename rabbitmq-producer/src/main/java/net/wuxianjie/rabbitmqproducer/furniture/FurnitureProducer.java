package net.wuxianjie.rabbitmqproducer.furniture;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FurnitureProducer {

    private final List<String> colors = List.of("white", "red", "green");
    private final List<String> materials = List.of("wood", "plastic", "steel");

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @RequestMapping("/furniture")
    public void sendFurniture() {
        for (var i = 0; i < 10; i++) {
            var fur = new Furniture(
                    colors.get(i % colors.size()),
                    materials.get(i % materials.size()),
                    "家具-" + i,
                    i * 100.0
            );
            sendMessage(fur);
        }
    }

    private void sendMessage(Furniture fur) {
        var props = new MessageProperties();
        props.setHeader("color", fur.getColor());
        props.setHeader("material", fur.getMaterial());
        String json;
        try {
            json = objectMapper.writeValueAsString(fur);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        var message = new Message(json.getBytes(StandardCharsets.UTF_8), props);
        rabbitTemplate.send("x.promotion", "", message);
    }
}
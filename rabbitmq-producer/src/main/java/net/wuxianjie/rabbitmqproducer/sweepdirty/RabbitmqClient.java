package net.wuxianjie.rabbitmqproducer.sweepdirty;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class RabbitmqClient {

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    private final RestClient restClient;

    public List<RabbitmqQueue> getAllQueues() {
        return restClient.get()
                .uri("http://127.0.0.1:15672/api/queues")
                .header("Authorization", createBasicAuthHeader())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<RabbitmqQueue>>() {
                })
                .getBody();
    }

    private String createBasicAuthHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (username + ":" + password).getBytes(StandardCharsets.UTF_8)
        );
    }

}
package net.wuxianjie.currencyexchangeservice.circuitbreaker;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import net.wuxianjie.myspringbootstarter.exception.ApiException;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentController.class);

    private final RestClient restClient;

    public PaymentController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping
    // @Retry(name = "paymentService", fallbackMethod = "makePaymentFallback")
    @CircuitBreaker(name = "paymentService", fallbackMethod = "makePaymentFallback")
    public String makePayment() {
        try {
            ResponseEntity<String> entity = restClient.get()
                .uri("http://localhost:9090/fake-payments")
                .retrieve()
                .toEntity(String.class);
            return entity.getBody();
        } catch (Exception e) {
            // 因为 Resilience4j 的重试机制，这里抛出的异常并不会被打印，故需要手动打印
            String message = "支付 API 调用失败";
            LOG.error("{}：{}", message, e.getMessage());
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, message, e);
        }
    }

    @SuppressWarnings("unused")
    private String makePaymentFallback(Throwable throwable) {
        return "支付服务暂时不可用，请稍后再试";
    }
}

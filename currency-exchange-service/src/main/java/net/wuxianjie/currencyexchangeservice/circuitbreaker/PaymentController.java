package net.wuxianjie.currencyexchangeservice.circuitbreaker;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import net.wuxianjie.commonkit.exception.ApiException;

/**
 * 用于测试 Resilience4j 断路器功能的控制器。
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final RestClient restClient;

    /**
     * 假设场景：应用需要调用一个外部的支付服务，这个服务偶尔会因为网络波动或服务升级而暂时无法访问。
     *
     * @return 表示接口调用成功的信息
     */
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
            log.error("{}: {}", message, e.getMessage());
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, message, e);
        }
    }

    /**
     * 断路器触发时的备用响应（Fallback Response）方法。
     *
     * @param throwable 断路器触发时的异常
     * @return 备用响应
     */
    @SuppressWarnings("unused")
    private String makePaymentFallback(Throwable throwable) {
        return "支付服务暂时不可用，请稍后再试";
    }

}

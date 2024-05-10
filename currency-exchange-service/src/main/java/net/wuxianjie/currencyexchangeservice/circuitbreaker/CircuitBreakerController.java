package net.wuxianjie.currencyexchangeservice.circuitbreaker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import net.wuxianjie.commonkit.exception.ApiException;

import io.github.resilience4j.retry.annotation.Retry;

/**
 * 用于测试 Resilience4j 断路器的控制器。
 */
@RestController
@RequestMapping("/api/v1/circuit-breaker")
@RequiredArgsConstructor
@Slf4j
public class CircuitBreakerController {

    private final RestClient restClient;

    /**
     * 测试断路器的接口。
     *
     * @return 表示接口调用成功的无意义信息
     */
    @GetMapping("/sample-api")
    // 默认重试 3 次
    @Retry(name = "sample-api", fallbackMethod = "sampleApiFallback")
    public String sampleApi() {
        try {
            ResponseEntity<String> entity = restClient.get()
                .uri("http://localhost:9090/fake-api")
                .retrieve()
                .toEntity(String.class);
            return entity.getBody();
        } catch (Exception e) {
            // 由于使用了 Resilience4j 的重试机制，这里抛出的异常并不会被打印，故需要手动打印
            log.error("Sample API 调用失败: {}", e.getMessage());
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Sample API 调用失败", e);
        }
    }

    /**
     * 断路器触发时的回调方法。
     *
     * @param throwable 断路器触发时的异常
     * @return 回调信息
     */
    @SuppressWarnings("unused")
    private String sampleApiFallback(Throwable throwable) {
        return "Fallback Response: " + throwable.getMessage();
    }

}

package net.wuxianjie.currencyexchangeservice.circuitbreaker;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/v1")
public class RealtimeExchangeRatesController {

    private static final Logger LOG = LoggerFactory.getLogger(RealtimeExchangeRatesController.class);

    private final RestClient restClient;

    public RealtimeExchangeRatesController(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    // `watch -n 0.1 curl http://127.0.0.1:8000/api/v1/realtime-exchange-rates`
    @GetMapping("/realtime-exchange-rates")
    // @Retry(name = "#root.methodName", fallbackMethod = "realtimeExchangeRatesFallback")
    // @CircuitBreaker(name = "#root.methodName", fallbackMethod = "realtimeExchangeRatesFallback")
    // @RateLimiter(name = "#root.methodName")
    @Bulkhead(name = "#root.methodName")
    public String getRealtimeExchangeRates() {
        LOG.info("调用实时汇率服务");
        return "Ok";
    }

    @SuppressWarnings("unused")
    private String requestDummyApi() {
        ResponseEntity<String> entity = restClient.get()
            .uri("http://127.0.0.1:9090/dummy-dummy")
            .retrieve()
            .toEntity(String.class);
        return entity.getBody();
    }

    @SuppressWarnings("unused")
    private String realtimeExchangeRatesFallback(Throwable throwable) {
        return "最近汇率为 1 比 7.23";
    }
}

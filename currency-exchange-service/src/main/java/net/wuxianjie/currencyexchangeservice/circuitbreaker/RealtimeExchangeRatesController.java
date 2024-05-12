package net.wuxianjie.currencyexchangeservice.circuitbreaker;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

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

    public RealtimeExchangeRatesController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping("/realtime-exchange-rates")
    // @Retry(name = "realtimeExchangeRatesService", fallbackMethod = "realtimeExchangeRatesFallback")
    @CircuitBreaker(name = "realtimeExchangeRatesService", fallbackMethod = "realtimeExchangeRatesFallback")
    public String getRealtimeExchangeRates() {
        LOG.info("调用实时汇率服务");
        ResponseEntity<String> entity = restClient.get()
            .uri("http://127.0.0.1:9090/dummy-dummy")
            .retrieve()
            .toEntity(String.class);
        return entity.getBody();
    }

    @SuppressWarnings("unused")
    private String realtimeExchangeRatesFallback(Throwable throwable) {
        // LOG.error(
        //     "调用实时汇率服务失败 [{}: {}]",
        //     throwable.getClass().getName(), throwable.getMessage()
        // );
        return "最近汇率为 1 比 7.23";
    }
}

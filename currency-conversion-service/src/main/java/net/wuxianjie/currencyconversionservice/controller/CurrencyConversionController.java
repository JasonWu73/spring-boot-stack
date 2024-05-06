package net.wuxianjie.currencyconversionservice.controller;

import java.math.BigDecimal;
import java.util.Objects;

import lombok.RequiredArgsConstructor;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import net.wuxianjie.webkit.exception.ApiException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CurrencyConversionController {

    private final RestClient restClient;
    private final Environment environment;

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversion(
            @PathVariable String from, @PathVariable String to,
            @PathVariable BigDecimal quantity
    ) {
        var conversionMultiple = getConversionMultipleFromApi(from, to);
        return new CurrencyConversion(
                1000L, from, to, conversionMultiple,
                quantity, quantity.multiply(conversionMultiple),
                environment.getProperty("local.server.port")
        );
    }

    private BigDecimal getConversionMultipleFromApi(String from, String to) {
        var url = "http://localhost:8000/api/v1/currency-exchange/from/{from}/to/{to}";
        try {
            var currencyConversion = restClient.get()
                    .uri(url, from, to)
                    .retrieve()
                    .toEntity(CurrencyConversion.class);
            return Objects.requireNonNull(currencyConversion.getBody()).getConversionMultiple();
        } catch (Exception e) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "无法获取汇率数据", e);
        }
    }

}
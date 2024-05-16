package net.wuxianjie.currencyconversionservice.controller;

import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import net.wuxianjie.myspringbootstarter.exception.ApiException;

@RestController
@RequestMapping("/api/v1")
public class CurrencyConversionController {

    private final RestClient restClient;
    private final CurrencyExchangeProxy currencyExchangeProxy;

    public CurrencyConversionController(
        RestClient.Builder builder,
        CurrencyExchangeProxy currencyExchangeProxy
    ) {
        this.restClient = builder.build();
        this.currencyExchangeProxy = currencyExchangeProxy;
    }

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversion(
        @PathVariable String from, @PathVariable String to,
        @PathVariable BigDecimal quantity
    ) {
        CurrencyConversion currencyConversion = getCurrencyExchangeFromApi(from, to);
        BigDecimal conversionMultiple = currencyConversion.exchangeRate();
        return new CurrencyConversion(
            1000L, from, to, conversionMultiple,
            quantity, quantity.multiply(conversionMultiple),
            currencyConversion.environment() + " via RestClient"
        );
    }

    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversionByFeign(
        @PathVariable String from, @PathVariable String to,
        @PathVariable BigDecimal quantity
    ) {
        CurrencyConversion currencyConversion = getCurrencyExchangeByFeign(from, to);
        BigDecimal currencyMultiple = currencyConversion.exchangeRate();
        return new CurrencyConversion(
            1000L, from, to, currencyMultiple,
            quantity, quantity.multiply(currencyMultiple),
            currencyConversion.environment() + " via Feign"
        );
    }

    private CurrencyConversion getCurrencyExchangeFromApi(String from, String to) {
        var url = "http://localhost:8000/api/v1/currency-exchange/from/{from}/to/{to}";
        try {
            var currencyConversion = restClient.get()
                .uri(url, from, to)
                .retrieve()
                .toEntity(CurrencyConversion.class);
            return Objects.requireNonNull(currencyConversion.getBody());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "无法获取汇率数据", e);
        }
    }

    private CurrencyConversion getCurrencyExchangeByFeign(String from, String to) {
        try {
            return currencyExchangeProxy.getCurrencyExchange(from, to);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "无法获取汇率数据", e);
        }
    }
}

package net.wuxianjie.currencyconversionservice.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("currency-exchange-service")
public interface CurrencyExchangeProxy {

    @GetMapping("/api/v1/currency-exchange/from/{from}/to/{to}")
    CurrencyConversion getCurrencyExchange(
        @PathVariable String from, @PathVariable String to
    );
}

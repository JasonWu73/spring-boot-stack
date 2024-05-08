package net.wuxianjie.currencyexchangeservice.controller;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.wuxianjie.commonkit.exception.ApiException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class CurrencyExchangeController {

    private final Environment environment;
    private final CurrencyExchangeMapper currencyExchangeMapper;

    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public CurrencyExchange getCurrencyExchange(
            @PathVariable String from, @PathVariable String to
    ) {
        log.info("from: {}, to: {}", from, to);
        var exchange = Optional.ofNullable(currencyExchangeMapper.selectCurrencyExchangeByFromAndTo(from, to))
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "从 [%s] 到 [%s] 的汇率数据不存在".formatted(from, to)
                ));
        var port = environment.getProperty("local.server.port");
        exchange.setEnvironment(port);
        return exchange;
    }

}
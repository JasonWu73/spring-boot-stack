package net.wuxianjie.currencyexchangeservice;

import java.math.BigDecimal;

import lombok.RequiredArgsConstructor;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CurrencyExchangeController {

    private final Environment environment;

    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public CurrencyExchange getCurrencyExchange(
            @PathVariable String from, @PathVariable String to
    ) {
        var exchange = new CurrencyExchange(1000L, from, to, BigDecimal.valueOf(7.24));
        var port = environment.getProperty("local.server.port");
        exchange.setEnvironment(port);
        return exchange;
    }

}
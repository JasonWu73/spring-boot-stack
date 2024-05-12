package net.wuxianjie.currencyconversionservice.controller;

import java.math.BigDecimal;

public record CurrencyConversion(
    Long id,
    String from,
    String to,
    BigDecimal exchangeRate,
    BigDecimal quantity,
    BigDecimal totalCalculatedAmount,
    String environment
) {
}

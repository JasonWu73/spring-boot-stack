package net.wuxianjie.currencyexchangeservice.controller;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyExchange {

    private Long id;
    private String from;
    private String to;
    private BigDecimal conversionMultiple;
    private String environment;

}

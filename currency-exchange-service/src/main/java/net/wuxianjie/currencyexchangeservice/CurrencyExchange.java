package net.wuxianjie.currencyexchangeservice;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyExchange {

    public CurrencyExchange(Long id, String from, String to, BigDecimal conversionMultiple) {
        this(id, from, to, conversionMultiple, null);
    }

    private Long id;
    private String from;
    private String to;
    private BigDecimal conversionMultiple;
    private String environment;

}
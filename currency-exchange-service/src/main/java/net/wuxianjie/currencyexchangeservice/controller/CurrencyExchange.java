package net.wuxianjie.currencyexchangeservice.controller;

import java.math.BigDecimal;
import java.util.Objects;

public class CurrencyExchange {

    private Long id;
    private String from;
    private String to;
    private BigDecimal exchangeRate;
    private String environment;

    public CurrencyExchange() {
    }

    public CurrencyExchange(
        Long id, String from, String to, BigDecimal exchangeRate,
        String environment
    ) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.exchangeRate = exchangeRate;
        this.environment = environment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyExchange that = (CurrencyExchange) o;
        return Objects.equals(id, that.id) && Objects.equals(from, that.from) && Objects.equals(to, that.to) && Objects.equals(exchangeRate, that.exchangeRate) && Objects.equals(environment, that.environment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, from, to, exchangeRate, environment);
    }

    @Override
    public String toString() {
        return "CurrencyExchange{" +
            "id=" + id +
            ", from='" + from + '\'' +
            ", to='" + to + '\'' +
            ", exchangeRate=" + exchangeRate +
            ", environment='" + environment + '\'' +
            '}';
    }
}

package net.wuxianjie.currencyexchangeservice;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CurrencyExchangeMapper {

    CurrencyExchange selectCurrencyExchangeByFromAndTo(String from, String to);

}
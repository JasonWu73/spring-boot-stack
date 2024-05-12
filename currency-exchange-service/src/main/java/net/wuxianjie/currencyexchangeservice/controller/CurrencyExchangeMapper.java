package net.wuxianjie.currencyexchangeservice.controller;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CurrencyExchangeMapper {

    CurrencyExchange selectExchangeByFromAndTo(String from, String to);
}

package net.wuxianjie.currencyconversionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CurrencyConversionApp {

    public static void main(String[] args) {
        SpringApplication.run(CurrencyConversionApp.class, args);
    }
}

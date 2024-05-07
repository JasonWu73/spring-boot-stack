package net.wuxianjie.currencyconversionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class NamingServerApp {

    public static void main(String[] args) {
        SpringApplication.run(NamingServerApp.class, args);
    }

}
package net.wuxianjie.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(p -> p.path("/get")
                .filters(f -> f.addRequestHeader("WXJ-NAME", "JasonWu")
                    .addRequestParameter("myName", "吴仙杰")
                )
                .uri("http://httpbin.org:80"))
            .route(p -> p.path("/api/v1/currency-exchange/**")
                .uri("lb://currency-exchange-service")
            )
            .route(p -> p.path("/api/v1/currency-conversion/**")
                .uri("lb://currency-conversion-service")
            )
            .route(p -> p.path("/api/v1/currency-conversion-feign/**")
                .uri("lb://currency-conversion-service")
            )
            .route(p -> p.path("/api/v1/currency-conversion-new/**")
                .filters(f -> f.rewritePath(
                    "/api/v1/currency-conversion-new/(?<segment>.*)",
                    "/api/v1/currency-conversion-feign/${segment}"))
                .uri("lb://currency-conversion-service")
            )
            .build();
    }

}

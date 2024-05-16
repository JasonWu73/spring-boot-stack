package net.wuxianjie.apigateway.router;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterConfig {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicate -> predicate.path("/get")
                .filters(filter -> filter.addRequestHeader("WXJ-NAME", "JasonWu")
                    .addRequestParameter("myName", "吴仙杰"))
                .uri("http://httpbin.org:80"))
            .route(predicate -> predicate.path("/api/v1/currency-exchange/**")
                .uri("lb://currency-exchange-service"))
            .route(predicate -> predicate.path("/api/v1/currency-conversion/**")
                .uri("lb://currency-conversion-service"))
            .route(predicate -> predicate.path("/api/v1/currency-conversion-feign/**")
                .uri("lb://currency-conversion-service"))
            .route(predicate -> predicate.path("/api/v1/currency-conversion-new/**")
                .filters(filter -> filter.rewritePath(
                    "/api/v1/currency-conversion-new/(?<segment>.*)",
                    "/api/v1/currency-conversion-feign/${segment}")
                ).uri("lb://currency-conversion-service"))
            .build();
    }
}

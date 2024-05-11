package net.wuxianjie.myspringbootstarter.gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import net.wuxianjie.myspringbootstarter.json.JsonConfiguration;

@AutoConfiguration
@AutoConfigureAfter(JsonConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(WebFluxConfigurer.class)
public class GatewayExceptionHandlerConfiguration {

    /**
     * 覆盖 Spring Boot WebFlux 默认的全局异常处理器。
     *
     * <p>确保该 Bean 优先级比默认的 {@link ErrorWebFluxAutoConfiguration#errorWebExceptionHandler} 高。</p>
     */
    @Bean
    @Order(-2)
    public DefaultErrorWebExceptionHandler defaultErrorWebExceptionHandler(
        ErrorAttributes errorAttributes,
        WebProperties webProperties,
        ServerProperties serverProperties,
        ObjectProvider<ViewResolver> viewResolvers,
        ServerCodecConfigurer serverCodecConfigurer,
        ApplicationContext applicationContext,
        @Qualifier("jsonMapper") ObjectMapper objectMapper
    ) {
        GatewayExceptionHandler handler = new GatewayExceptionHandler(
            errorAttributes, webProperties.getResources(),
            serverProperties.getError(), applicationContext
        );
        handler.setViewResolvers(viewResolvers.orderedStream().toList());
        setCustomJsonMapper(handler, serverCodecConfigurer, objectMapper);
        return handler;
    }

    private void setCustomJsonMapper(
        GatewayExceptionHandler handler, ServerCodecConfigurer configurer,
        ObjectMapper objectMapper
    ) {
        configurer.defaultCodecs().jackson2JsonDecoder(
            new Jackson2JsonDecoder(objectMapper)
        );
        configurer.defaultCodecs().jackson2JsonEncoder(
            new Jackson2JsonEncoder(objectMapper)
        );
        handler.setMessageWriters(configurer.getWriters());
        handler.setMessageReaders(configurer.getReaders());
    }
}

package net.wuxianjie.gatewaykit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.result.view.ViewResolver;

/**
 * 全局异常处理器配置。
 */
@Configuration
public class GatewayExceptionHandlerConfig {

    /**
     * 覆盖 Spring Boot WebFlux 默认的全局异常处理器。
     *
     * <p>确保该 Bean 优先级比默认的
     * {@link ErrorWebFluxAutoConfiguration#errorWebExceptionHandler} 高。</p>
     *
     * @param errorAttributes 错误属性
     * @param webProperties Web 配置属性
     * @param serverProperties 服务器配置属性
     * @param viewResolvers 视图解析器
     * @param serverCodecConfigurer 服务器编解码器配置器
     * @param applicationContext 应用程序上下文
     * @return 全局异常处理器
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
        @Qualifier("objectMapper") ObjectMapper objectMapper
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
        GatewayExceptionHandler handler, ServerCodecConfigurer config,
        ObjectMapper objectMapper
    ) {
        config.defaultCodecs().jackson2JsonDecoder(
            new Jackson2JsonDecoder(objectMapper)
        );
        config.defaultCodecs().jackson2JsonEncoder(
            new Jackson2JsonEncoder(objectMapper)
        );
        handler.setMessageWriters(config.getWriters());
        handler.setMessageReaders(config.getReaders());
    }

}

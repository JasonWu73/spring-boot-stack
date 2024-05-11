package net.wuxianjie.myspringbootstarter.gateway.dns;

import io.netty.resolver.DefaultAddressResolverGroup;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * 修复 Spring Cloud Gateway 无法解析域名的问题。
 */
@AutoConfiguration
@ConditionalOnClass(HttpClientCustomizer.class)
public class GatewayAddressResolverConfiguration {

    /**
     * 配置使用 Netty 的默认地址解析器组。
     */
    @Bean
    public HttpClientCustomizer httpClientCustomizer() {
        return client -> client.resolver(DefaultAddressResolverGroup.INSTANCE);
    }
}

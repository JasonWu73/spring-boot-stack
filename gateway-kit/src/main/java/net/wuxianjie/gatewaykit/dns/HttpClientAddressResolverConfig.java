package net.wuxianjie.gatewaykit.dns;

import java.net.http.HttpClient;

import io.netty.resolver.DefaultAddressResolverGroup;

import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 修复 Spring Cloud Gateway 无法解析域名的问题。
 */
@Configuration
public class HttpClientAddressResolverConfig {

    /**
     * 配置使用 Netty 的默认地址解析器组。
     *
     * <p>通过让 Gateway 在创建 {@link HttpClient}
     * 实例时使用该自定义配置，从而修复无法解析域名的问题。</p>
     *
     * @return {@link HttpClientCustomizer} 实例
     */
    @Bean
    public HttpClientCustomizer httpClientResolverCustomizer() {
        return httpClient ->
            httpClient.resolver(DefaultAddressResolverGroup.INSTANCE);
    }

}

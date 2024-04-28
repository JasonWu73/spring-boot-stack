package net.wuxianjie.webkit.security;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import net.wuxianjie.webkit.config.WebKitProperties;
import net.wuxianjie.webkit.exception.ApiException;

/**
 * Spring Security 配置。
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final HandlerExceptionResolver handlerExceptionResolver;

    private final WebKitProperties webKitProperties;
    private final TokenAuth tokenAuth;

    /**
     * 配置 Spring Security 过滤器链。
     *
     * @param http Spring Security HTTP 配置
     * @return Spring Security 过滤器链
     * @throws Exception 配置错误时抛出
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 以下配置仅对 API 请求生效
        http.securityMatcher(webKitProperties.getSecurity().getApiPathPrefix() + "**")
                // 注意：顺序很重要，前面的规则先匹配
                .authorizeHttpRequests(r -> {
                    // 开放 API
                    for (var p : webKitProperties.getSecurity().getPermitAllPaths()) {
                        r.requestMatchers(p).permitAll();
                    }
                    // 默认其他 API 都需要登录才能访问
                    r.requestMatchers("/**").authenticated();
                })
                // 添加自定义 Token 身份验证过滤器
                .addFilterBefore(
                        new TokenAuthFilter(handlerExceptionResolver, tokenAuth),
                        UsernamePasswordAuthenticationFilter.class
                );

        // 以下配置对所有请求生效
        http.authorizeHttpRequests(r -> {
                    // 默认所有请求所有人都可访问（保证 SPA 前端资源可用）
                    r.requestMatchers("/**").permitAll();
                })
                // 支持 CORS
                .cors(Customizer.withDefaults())
                // 禁用 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 允许浏览器在同源策略下使用 `<frame>` 或 `<iframe>`
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                // 无状态会话，即不向客户端发送 `JSESSIONID` Cookies
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 身份验证失败和没有访问权限的处理
                .exceptionHandling(c -> {
                    // 未通过身份验证，对应 401 HTTP 状态码
                    c.authenticationEntryPoint((req, res, e) -> handlerExceptionResolver
                            .resolveException(req, res, null,
                                    new ApiException(HttpStatus.UNAUTHORIZED, "身份验证失败", e)));

                    // 通过身份验证，但没有访问权限，对应 403 HTTP 状态码
                    c.accessDeniedHandler((req, res, e) -> handlerExceptionResolver
                            .resolveException(req, res, null,
                                    new ApiException(HttpStatus.FORBIDDEN, "没有访问权限", e)));
                });
        return http.build();
    }

    /**
     * 配置 Spring Security CORS。
     *
     * @return CORS 配置
     */
    @Bean
    public CorsConfigurationSource corsConfig() {
        var cfg = new CorsConfiguration();

        // 以下配置缺一不可
        cfg.setAllowedOriginPatterns(List.of("*"));
        cfg.setAllowedMethods(List.of("*"));
        cfg.setAllowCredentials(true);
        cfg.setAllowedHeaders(List.of("*"));

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    /**
     * 密码哈希算法。
     *
     * @return 密码哈希算法
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置拥有上下级关系的功能权限。
     * <p>
     * Spring Boot 3.x（即 Spring Security 6.x）开始，还需要创建 {@link #expressionHandler()}。
     *
     * @return 拥有上下级关系的功能权限
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        var hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(String.join("\n", webKitProperties.getSecurity().getHierarchies()));
        return hierarchy;
    }

    /**
     * Spring Boot 3.x（即 Spring Security 6）开始，这是配置 {@link #roleHierarchy()} 的必要 Bean。
     *
     * @return {@link #roleHierarchy()} 的必要 Bean
     */
    @Bean
    public DefaultMethodSecurityExpressionHandler expressionHandler() {
        var handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy());
        return handler;
    }

}
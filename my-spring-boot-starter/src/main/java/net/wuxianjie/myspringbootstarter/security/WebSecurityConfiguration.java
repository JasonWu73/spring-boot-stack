package net.wuxianjie.myspringbootstarter.security;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
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

import net.wuxianjie.myspringbootstarter.exception.ApiException;
import net.wuxianjie.myspringbootstarter.shared.MyConfigurationProperties;

/**
 * 基于 Spring Security 的令牌身份验证机制自动配置类。
 *
 * <p>注意：要激活该配置必须要在 Spring IoC 中存在实现了 {@link TokenAuth} 接口的 Bean，否则使用 Spring Security 默认配置。</p>
 */
@AutoConfiguration
@ConditionalOnClass(SecurityFilterChain.class)
@ConditionalOnBean(TokenAuth.class)
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        HandlerExceptionResolver handlerExceptionResolver,
        MyConfigurationProperties myConfigurationproperties,
        TokenAuth tokenAuth
    ) throws Exception {
        // 以下配置仅对 API 请求生效
        String apiPathPrefix = myConfigurationproperties.getSecurity().getApiPathPrefix();
        String[] permitAllPaths = myConfigurationproperties.getSecurity().getPermitAllPaths();

        http.securityMatcher(apiPathPrefix + "**")
            // 注意：顺序很重要，即前面的规则匹配后则不再进行后续比较
            .authorizeHttpRequests(r -> {
                // 配置公共 API
                for (String path : permitAllPaths) {
                    r.requestMatchers(path).permitAll();
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
            .headers(c -> c.frameOptions(
                HeadersConfigurer.FrameOptionsConfig::sameOrigin
            ))
            // 不需要会话状态，即不向客户端发送 `JSESSIONID` Cookies
            .sessionManagement(c -> c.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS
            ))
            // 身份验证失败和没有访问权限的处理
            .exceptionHandling(c -> {
                // 未通过身份验证，对应 401 HTTP 状态码
                c.authenticationEntryPoint((req, res, authEx) ->
                    handlerExceptionResolver.resolveException(
                        req, res, null,
                        new ApiException(
                            HttpStatus.UNAUTHORIZED, "身份验证失败", authEx
                        )
                    )
                );

                // 通过身份验证，但没有访问权限，对应 403 HTTP 状态码
                c.accessDeniedHandler((req, res, acsDenEx) ->
                    handlerExceptionResolver.resolveException(
                        req, res, null,
                        new ApiException(
                            HttpStatus.FORBIDDEN, "没有访问权限", acsDenEx
                        )
                    )
                );
            });
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 以下配置缺一不可
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * 密码哈希算法。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy(MyConfigurationProperties myConfigurationproperties) {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(
            String.join("\n", myConfigurationproperties.getSecurity().getHierarchies())
        );
        return hierarchy;
    }

    /**
     * Spring Boot 3.x（即 Spring Security 6）开始，需要手动配置使用上下级关系的功能权限。
     */
    @Bean
    public DefaultMethodSecurityExpressionHandler DefaultMethodSecurityExpressionHandler(
        RoleHierarchy roleHierarchy
    ) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    /**
     * 没有实际作用，仅为了避免打印警告信息：
     *
     * <pre>{@code
     * 2024-05-01T13:19:01.973+08:00  WARN 63739 --- [           main] .s.s.UserDetailsServiceAutoConfiguration :
     *
     * Using generated security password: d2bdf20a-33b7-4578-b939-2692580def92
     *
     * This generated password is for development use only. Your security configuration must be updated before running your application in production.
     * }</pre>
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return u -> u;
    }
}

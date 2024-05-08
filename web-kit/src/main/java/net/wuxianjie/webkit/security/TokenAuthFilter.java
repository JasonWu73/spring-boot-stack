package net.wuxianjie.webkit.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import net.wuxianjie.commonkit.exception.ApiException;

/**
 * Access Token 身份验证过滤器。
 */
@RequiredArgsConstructor
public class TokenAuthFilter extends OncePerRequestFilter {

    /**
     * 携带 Access Token 的请求头值前缀。
     *
     * <pre>{@code
     * "Authorization: Bearer {{accessToken}}"
     * }</pre>
     */
    private static final String BEARER_PREFIX = "Bearer ";

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final TokenAuth tokenAuth;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain chain
    ) throws ServletException, IOException {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearer == null) {
            chain.doFilter(request, response);
            return;
        }
        if (!bearer.startsWith(BEARER_PREFIX)) {
            handlerExceptionResolver.resolveException(
                request, response, null,
                new ApiException(
                    HttpStatus.UNAUTHORIZED, "Access Token 格式错误"
                )
            );
            return;
        }
        var accessToken = bearer.substring(BEARER_PREFIX.length());

        CurrentUserInfo user;
        try {
            user = tokenAuth.authenticate(accessToken);
        } catch (TokenAuthException e) {
            handlerExceptionResolver.resolveException(
                request, response, null,
                new ApiException(
                    HttpStatus.UNAUTHORIZED, "Access Token 验证失败", e
                )
            );
            return;
        }

        AuthUtils.setAuthenticatedContext(user, request);
        chain.doFilter(request, response);
    }

}

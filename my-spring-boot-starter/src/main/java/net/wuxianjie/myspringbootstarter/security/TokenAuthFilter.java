package net.wuxianjie.myspringbootstarter.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import net.wuxianjie.myspringbootstarter.exception.ApiException;

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

    public TokenAuthFilter(
        HandlerExceptionResolver handlerExceptionResolver,
        TokenAuth tokenAuth
    ) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.tokenAuth = tokenAuth;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        @SuppressWarnings("NullableProblems") HttpServletResponse response,
        @SuppressWarnings("NullableProblems") FilterChain chain
    ) throws ServletException, IOException {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken == null) {
            chain.doFilter(request, response);
            return;
        }
        if (!bearerToken.startsWith(BEARER_PREFIX)) {
            handlerExceptionResolver.resolveException(
                request, response, null,
                new ApiException(HttpStatus.UNAUTHORIZED, "Bearer Token 格式错误")
            );
            return;
        }
        var accessToken = bearerToken.substring(BEARER_PREFIX.length());

        CurrentUser user;
        try {
            user = tokenAuth.authenticate(accessToken);
        } catch (TokenAuthException e) {
            handlerExceptionResolver.resolveException(
                request, response, null,
                new ApiException(HttpStatus.UNAUTHORIZED, "Token 验证不通过", e)
            );
            return;
        }

        AuthUtils.setAuthenticatedContext(user, request);
        chain.doFilter(request, response);
    }
}

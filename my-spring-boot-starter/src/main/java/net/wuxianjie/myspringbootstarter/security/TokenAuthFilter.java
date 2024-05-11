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
        HttpServletRequest req,
        @SuppressWarnings("NullableProblems") HttpServletResponse res,
        @SuppressWarnings("NullableProblems") FilterChain chain
    ) throws ServletException, IOException {
        String bearer = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearer == null) {
            chain.doFilter(req, res);
            return;
        }
        if (!bearer.startsWith(BEARER_PREFIX)) {
            handlerExceptionResolver.resolveException(
                req, res, null,
                new ApiException(
                    HttpStatus.UNAUTHORIZED, "Access Token 格式错误"
                )
            );
            return;
        }
        var accessToken = bearer.substring(BEARER_PREFIX.length());

        CurUser user;
        try {
            user = tokenAuth.auth(accessToken);
        } catch (TokenAuthException ex) {
            handlerExceptionResolver.resolveException(
                req, res, null,
                new ApiException(
                    HttpStatus.UNAUTHORIZED, "Access Token 验证失败", ex
                )
            );
            return;
        }

        AuthUtils.setAuthenticatedContext(user, req);
        chain.doFilter(req, res);
    }
}

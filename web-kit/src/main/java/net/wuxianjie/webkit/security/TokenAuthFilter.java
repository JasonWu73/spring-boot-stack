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

import net.wuxianjie.webkit.exception.ApiException;

/**
 * 访问令牌（Access Token）身份验证过滤器。
 */
@RequiredArgsConstructor
public class TokenAuthFilter extends OncePerRequestFilter {

    /**
     * 携带访问令牌（Access Token）的请求头值前缀：
     *
     * <pre>{@code "Authorization: Bearer {{accessToken}}" }</pre>
     */
    private static final String BEARER_PREFIX = "Bearer ";

    private final HandlerExceptionResolver handlerExceptionResolver;

    private final TokenAuth tokenAuth;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req,
                                    @NonNull HttpServletResponse res,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {
        var bearer = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearer == null) {
            chain.doFilter(req, res);
            return;
        }
        if (!bearer.startsWith(BEARER_PREFIX)) {
            handlerExceptionResolver.resolveException(req, res, null,
                    new ApiException(HttpStatus.UNAUTHORIZED, "accessToken 格式错误"));
            return;
        }
        var accessToken = bearer.substring(BEARER_PREFIX.length());

        CurrentUserInfo user;
        try {
            user = tokenAuth.authenticate(accessToken);
        } catch (TokenAuthException e) {
            handlerExceptionResolver.resolveException(req, res, null,
                    new ApiException(HttpStatus.UNAUTHORIZED, "Token 身份验证失败", e));
            return;
        }

        AuthUtils.setAuthenticatedContext(user, req);
        chain.doFilter(req, res);
    }

}
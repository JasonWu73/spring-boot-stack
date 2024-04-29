package net.wuxianjie.webkit.security;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;

/**
 * 身份验证工具类。
 */
public class AuthUtils {

    /**
     * 从 Spring Security Context 中获取当前登录用户信息。
     *
     * @return 当前登录用户信息
     */
    public static Optional<CurrentUserInfo> getCurrentUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(a -> !(a instanceof AnonymousAuthenticationToken))
                .map(a -> (CurrentUserInfo) a.getPrincipal());
    }

    /**
     * 将登录信息写入 Spring Security Context。
     *
     * @param user 已通过身份验证的用户信息
     * @param req HTTP 请求对象
     */
    public static void setAuthenticatedContext(
            CurrentUserInfo user, HttpServletRequest req
    ) {
        var authorities = user.authorities()
                .stream()
                .filter(StringUtils::hasText)
                .map(SimpleGrantedAuthority::new)
                .toList();
        var token = new UsernamePasswordAuthenticationToken(user, null, authorities);
        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

}
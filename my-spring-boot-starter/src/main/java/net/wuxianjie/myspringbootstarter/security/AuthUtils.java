package net.wuxianjie.myspringbootstarter.security;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;

public class AuthUtils {

    /**
     * 从 Spring Security Context 中获取当前登录用户信息。
     */
    public static Optional<CurrentUser> getCurrentUser() {
        return Optional.ofNullable(
                SecurityContextHolder.getContext().getAuthentication()
            )
            .filter(u -> !(u instanceof AnonymousAuthenticationToken))
            .map(u -> (CurrentUser) u.getPrincipal());
    }

    /**
     * 将登录信息写入 Spring Security Context。
     */
    public static void setAuthenticatedContext(
        CurrentUser user, HttpServletRequest request
    ) {
        List<SimpleGrantedAuthority> authorities = user.authorities().stream()
            .filter(StringUtils::hasText)
            .map(SimpleGrantedAuthority::new)
            .toList();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            user, null, authorities
        );
        token.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}

package net.wuxianjie.myspringbootstarter.security;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
class TokenAuthTestImpl implements TokenAuth {

    static final Map<String, CurrentUser> users = Map.of(
        "root", new CurrentUser(1, "root", "超级管理员", List.of("root"), "root-token", "root-refresh-token"),
        "admin", new CurrentUser(2, "admin", "管理员", List.of("admin"), "admin-token", "admin-refresh-token"),
        "user", new CurrentUser(3, "user", "用户", List.of("user"), "user-token", "user-refresh-token"),
        "guest", new CurrentUser(4, "guest", "游客", List.of("guest"), "guest-token", "guest-refresh-token")
    );

    @Override
    public CurrentUser authenticate(String accessToken) throws TokenAuthException {
        return users.values().stream()
            .filter(u -> u.accessToken().equals(accessToken))
            .findFirst()
            .orElseThrow(() -> new TokenAuthException("无效的 Access Token"));
    }
}

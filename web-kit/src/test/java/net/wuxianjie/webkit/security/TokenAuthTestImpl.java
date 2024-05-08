package net.wuxianjie.webkit.security;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
class TokenAuthTestImpl implements TokenAuth {

    static final Map<String, CurrentUserInfo> users = Map.of(
        "root", new CurrentUserInfo(1, "root", "超级管理员", List.of("root"), "root-token", "root-refresh-token"),
        "admin", new CurrentUserInfo(2, "admin", "管理员", List.of("admin"), "admin-token", "admin-refresh-token"),
        "user", new CurrentUserInfo(3, "user", "用户", List.of("user"), "user-token", "user-refresh-token"),
        "guest", new CurrentUserInfo(4, "guest", "游客", List.of("guest"), "guest-token", "guest-refresh-token")
    );

    @Override
    public CurrentUserInfo authenticate(String accessToken)
        throws TokenAuthException {
        return users.values().stream()
            .filter(userInfo -> userInfo.accessToken().equals(accessToken))
            .findFirst()
            .orElseThrow(() -> new TokenAuthException("无效的 Access Token"));
    }

}

package net.wuxianjie.web.auth;

import java.util.List;

import org.springframework.stereotype.Service;

import net.wuxianjie.webkit.security.CurrentUserInfo;
import net.wuxianjie.webkit.security.TokenAuth;

@Service
public class TokenAuthImpl implements TokenAuth {

    @Override
    public CurrentUserInfo authenticate(String accessToken) {
        return new CurrentUserInfo(
                1, "root", "超级管理员", List.of("root"),
                "root-token", "root-refresh"
        );
    }

}
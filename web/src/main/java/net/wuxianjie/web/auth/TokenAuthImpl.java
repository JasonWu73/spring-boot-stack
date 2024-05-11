package net.wuxianjie.web.auth;

import java.util.List;

import org.springframework.stereotype.Service;

import net.wuxianjie.myspringbootstarter.security.CurrentUser;
import net.wuxianjie.myspringbootstarter.security.TokenAuth;

@Service
public class TokenAuthImpl implements TokenAuth {

    @Override
    public CurrentUser authenticate(String accessToken) {
        return new CurrentUser(
            1, "root", "超级管理员", List.of("root"),
            "root-token", "root-refresh"
        );
    }
}

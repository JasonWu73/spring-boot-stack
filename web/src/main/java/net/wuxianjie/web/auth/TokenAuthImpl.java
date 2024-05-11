package net.wuxianjie.web.auth;

import java.util.List;

import org.springframework.stereotype.Service;

import net.wuxianjie.myspringbootstarter.security.CurUser;
import net.wuxianjie.myspringbootstarter.security.TokenAuth;

@Service
public class TokenAuthImpl implements TokenAuth {

    @Override
    public CurUser auth(String accessToken) {
        return new CurUser(
            1, "root", "超级管理员", List.of("root"),
            "root-token", "root-refresh"
        );
    }
}

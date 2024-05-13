package net.wuxianjie.web.auth;

import java.util.List;

import org.springframework.stereotype.Service;

import net.wuxianjie.myspringbootstarter.security.CurrentUser;
import net.wuxianjie.myspringbootstarter.security.TokenAuth;
import net.wuxianjie.myspringbootstarter.security.TokenAuthException;

@Service
public class TokenAuthImpl implements TokenAuth {

    @Override
    public CurrentUser authenticate(String accessToken) throws TokenAuthException {
        if (accessToken.equals("root-token")) {
            return new CurrentUser(
                1, "root", "超级管理员", List.of("root"),
                "root-token", "root-refresh"
            );
        }
        throw new TokenAuthException("无效的令牌");
    }
}

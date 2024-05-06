package net.wuxianjie.apigateway.auth;

import org.springframework.stereotype.Service;

import net.wuxianjie.webkit.security.CurrentUserInfo;
import net.wuxianjie.webkit.security.TokenAuth;

@Service
public class TokenAuthImpl implements TokenAuth {

    @Override
    public CurrentUserInfo authenticate(String accessToken) {
        return null;
    }

}
package net.wuxianjie.web.auth;

import java.util.List;

import org.springframework.stereotype.Service;

import net.wuxianjie.webkit.security.CurrentUserInfo;
import net.wuxianjie.webkit.security.TokenAuth;

/**
 * 一个仅仅为了能让程序正常运行的 Token 身份验证实现。
 */
@Service
public class TokenAuthImpl implements TokenAuth {

    /**
     * 硬编码的 Token 身份验证。
     *
     * @param accessToken Access Token
     * @return 当前用户信息
     */
    @Override
    public CurrentUserInfo authenticate(String accessToken) {
        return new CurrentUserInfo(
            1, "root", "超级管理员", List.of("root"),
            "root-token", "root-refresh"
        );
    }

}

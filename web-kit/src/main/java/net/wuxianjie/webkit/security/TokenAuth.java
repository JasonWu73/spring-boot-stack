package net.wuxianjie.webkit.security;

/**
 * Access Token 身份验证接口。
 */
public interface TokenAuth {

    /**
     * 验证 Access Token 。
     *
     * @param accessToken Access Token
     * @return 身份验证通过后的用户信息
     * @throws TokenAuthException 令牌验证失败时抛出
     */
    CurrentUserInfo authenticate(String accessToken) throws TokenAuthException;
}

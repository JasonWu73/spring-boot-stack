package net.wuxianjie.webkit.security;

/**
 * 访问令牌身份验证接口。
 */
public interface TokenAuth {

    /**
     * 验证访问令牌。
     *
     * @param accessToken 访问令牌
     * @return 身份验证通过后的用户信息
     * @throws AccessTokenAuthException 令牌验证失败时抛出
     */
    CurrentUserInfo authenticate(String accessToken) throws AccessTokenAuthException;

}
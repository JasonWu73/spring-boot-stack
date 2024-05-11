package net.wuxianjie.myspringbootstarter.security;

/**
 * 要生效令牌身份验证机制，则必需实现此接口，否则使用 Spring Security 默认的身份验证机制。
 */
public interface TokenAuth {

    /**
     * 对访问令牌执行身份验证。
     */
    CurrentUser authenticate(String accessToken) throws TokenAuthException;
}

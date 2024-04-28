package net.wuxianjie.webkit.security;

/**
 * 访问令牌身份验证异常。
 */
public class TokenAuthException extends Exception {

    /**
     * 构造访问令牌身份验证异常。
     *
     * @param message 异常消息
     */
    public TokenAuthException(String message) {
        super(message);
    }

    /**
     * 构造访问令牌身份验证异常。
     *
     * @param message 异常消息
     * @param cause 异常原因
     */
    public TokenAuthException(String message, Throwable cause) {
        super(message, cause);
    }

}
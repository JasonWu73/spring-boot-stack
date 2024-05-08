package net.wuxianjie.webkit.security;

/**
 * Access Token 身份验证异常。
 */
public class TokenAuthException extends Exception {

    /**
     * 构造 Access Token 身份验证异常。
     *
     * @param message 异常消息
     */
    public TokenAuthException(String message) {
        super(message);
    }

    /**
     * 构造 Access Token 身份验证异常。
     *
     * @param message 异常消息
     * @param cause 异常原因
     */
    public TokenAuthException(String message, Throwable cause) {
        super(message, cause);
    }

}

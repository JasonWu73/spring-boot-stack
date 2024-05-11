package net.wuxianjie.myspringbootstarter.security;

public class TokenAuthException extends Exception {

    public TokenAuthException(String message) {
        super(message);
    }

    public TokenAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}

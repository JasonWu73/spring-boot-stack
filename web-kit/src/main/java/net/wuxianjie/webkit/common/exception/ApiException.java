package net.wuxianjie.webkit.common.exception;

import java.util.Optional;

import org.springframework.http.HttpStatus;

/**
 * API 调用异常。
 */
public class ApiException extends RuntimeException {

    /**
     * 多个异常消息之间的分隔符。
     */
    public static final String MESSAGE_SEPARATOR = "; ";

    private final HttpStatus status;
    private final String error;
    private final String fullMessage;

    /**
     * 构造 API 调用异常。
     *
     * @param status HTTP 错误状态码
     * @param error 错误原因的简短描述
     * @param cause 导致 API 调用失败的异常
     */
    public ApiException(HttpStatus status, String error, Throwable cause) {
        super(error, cause);
        this.status = status;
        this.error = error;
        this.fullMessage = buildFullMessage();
    }

    /**
     * 构造 API 调用异常。
     *
     * @param status HTTP 错误状态码
     * @param error 错误原因的简短描述
     */
    public ApiException(HttpStatus status, String error) {
        this(status, error, null);
    }

    @Override
    public String getMessage() {
        return fullMessage;
    }

    /**
     * 获取 HTTP 响应状态码。
     *
     * @return HTTP 响应状态码
     */
    public HttpStatus getStatus() {
        return status;
    }

    private String buildFullMessage() {
        var message = "%s \"%s\"".formatted(status, this.error);
        return getNestedMessage(getCause())
                .map(m -> message + MESSAGE_SEPARATOR + m)
                .orElse(message);
    }

    private Optional<String> getNestedMessage(Throwable cause) {
        return Optional.ofNullable(cause).map(c -> {
            var sb = new StringBuilder();
            sb.append("嵌套异常 [")
                    .append(c.getClass().getName())
                    .append(": ")
                    .append(c.getMessage())
                    .append("]");
            getNestedMessage(c.getCause())
                    .ifPresent(m -> sb.append(MESSAGE_SEPARATOR).append(m));
            return sb.toString();
        });
    }

}
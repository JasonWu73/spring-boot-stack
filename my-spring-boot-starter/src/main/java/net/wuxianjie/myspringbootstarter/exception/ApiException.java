package net.wuxianjie.myspringbootstarter.exception;

import java.util.Optional;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    /**
     * 多条异常信息间的分隔符。
     */
    public static final String MESSAGE_SEPARATOR = "；";

    /**
     * HTTP 状态码。
     */
    private final HttpStatus status;

    /**
     * 错误信息。
     */
    private final String error;

    /**
     * 包含整条异常链错误信息的完整错误信息。
     */
    private final String fullMessage;

    public ApiException(HttpStatus status, String error, Throwable cause) {
        super(error, cause);
        this.status = status;
        this.error = error;
        this.fullMessage = buildFullMessage();
    }

    public ApiException(HttpStatus status, String error) {
        this(status, error, null);
    }

    @Override
    public String getMessage() {
        return error;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getFullMessage() {
        return fullMessage;
    }

    private String buildFullMessage() {
        return "%s \"%s\"".formatted(status, error) +
            Optional.ofNullable(getCause())
                .map(throwable ->
                    ApiException.MESSAGE_SEPARATOR + getNestedMessage(throwable)
                )
                .orElse("");
    }

    private String getNestedMessage(Throwable throwable) {
        if (throwable == null) return "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("嵌套异常 [")
            .append(throwable.getClass().getName())
            .append(": ")
            .append(throwable.getMessage())
            .append("]");
        // 递归调用以获取更深层次的异常信息
        String nestedMessage = getNestedMessage(throwable.getCause());
        if (!nestedMessage.isEmpty()) {
            stringBuilder
                .append(ApiException.MESSAGE_SEPARATOR).append(nestedMessage);
        }
        return stringBuilder.toString();
    }
}

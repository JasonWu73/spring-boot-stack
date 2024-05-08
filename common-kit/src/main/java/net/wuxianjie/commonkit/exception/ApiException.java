package net.wuxianjie.commonkit.exception;

import java.util.Optional;

import lombok.Getter;

import org.springframework.http.HttpStatus;

/**
 * API 调用异常。
 */
public class ApiException extends RuntimeException {

    /**
     * 多个异常消息之间的分隔符。
     */
    public static final String MESSAGE_SEPARATOR = "; ";

    /**
     * HTTP 错误响应状态码。
     */
    @Getter
    private final HttpStatus status;

    /**
     * 错误原因的简短描述，这用于返回给前端作为用户友好的错误提示。
     *
     * <p>无需为此字段生成 {@code getter} 方法，直接调用 {@link #getMessage()} 即可。</p>
     */
    private final String error;

    /**
     * 完整的异常消息。
     */
    @Getter
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
        return error;
    }

    private String buildFullMessage() {
        String message = "%s \"%s\"".formatted(status, error);
        return getNestedMessage(getCause())
            .map(nestedMessage ->
                message + ApiException.MESSAGE_SEPARATOR + nestedMessage
            )
            .orElse(message);
    }

    private Optional<String> getNestedMessage(Throwable throwable) {
        return Optional.ofNullable(throwable)
            .map(exc -> {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("嵌套异常 [")
                    .append(exc.getClass().getName())
                    .append(": ")
                    .append(exc.getMessage())
                    .append("]");
                getNestedMessage(exc.getCause())
                    .ifPresent(nestedMessage ->
                        stringBuilder
                            .append(ApiException.MESSAGE_SEPARATOR)
                            .append(nestedMessage)
                    );
                return stringBuilder.toString();
            });
    }

}

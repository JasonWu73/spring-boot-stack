package net.wuxianjie.myspringbootstarter.exception;

import java.util.Optional;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    /**
     * 多条异常信息间的分隔符。
     */
    public static final String MSG_SEP = "；";

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
    private final String fullMsg;

    public ApiException(HttpStatus status, String error, Throwable cause) {
        super(error, cause);
        this.status = status;
        this.error = error;
        this.fullMsg = buildFullMsg();
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

    public String getFullMsg() {
        return fullMsg;
    }

    private String buildFullMsg() {
        return String.format("%s \"%s\"", status, error) +
            Optional.ofNullable(getCause())
                .map(th -> MSG_SEP + getNestedMsg(th))
                .orElse("");
    }

    private String getNestedMsg(Throwable th) {
        if (th == null) return "";
        StringBuilder strBld = new StringBuilder();
        strBld.append("嵌套异常 [")
            .append(th.getClass().getName())
            .append(": ")
            .append(th.getMessage())
            .append("]");
        // 递归调用以获取更深层次的异常信息
        String nestedMsg = getNestedMsg(th.getCause());
        if (!nestedMsg.isEmpty()) {
            strBld.append(ApiException.MSG_SEP).append(nestedMsg);
        }
        return strBld.toString();
    }
}

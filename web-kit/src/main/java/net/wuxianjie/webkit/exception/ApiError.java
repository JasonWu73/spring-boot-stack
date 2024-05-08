package net.wuxianjie.webkit.exception;

import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * API 错误响应结果。
 *
 * @param timestamp 异常发生时间
 * @param status HTTP 响应状态码
 * @param error 错误原因的简短描述
 * @param path 发生异常的请求路径
 */
public record ApiError(
    LocalDateTime timestamp, int status, String error, String path
) {


    /**
     * 构造 API 错误响应结果。
     *
     * @param status HTTP 错误状态码
     * @param error 错误原因的简短描述
     */
    public ApiError(HttpStatus status, String error) {
        this(LocalDateTime.now(), status.value(), error, getRequestPath());
    }

    private static String getRequestPath() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
            .map(ServletRequestAttributes.class::cast)
            .map(ServletRequestAttributes::getRequest)
            .map(HttpServletRequest::getRequestURI)
            .orElse(null);
    }

}

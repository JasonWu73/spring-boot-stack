package net.wuxianjie.myspringbootstarter.exception;

import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * API 错误响应结果。
 *
 * @param timestamp 错误发生时间
 * @param status HTTP 状态码
 * @param error 错误信息
 * @param path 发生错误的请求路径
 */
public record ApiError(
    LocalDateTime timestamp, int status, String error, String path
) {


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

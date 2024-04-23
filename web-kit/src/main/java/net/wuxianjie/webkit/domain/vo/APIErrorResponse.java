package net.wuxianjie.webkit.domain.vo;

import java.time.LocalDateTime;

import net.wuxianjie.webkit.common.util.WebUtils;

/**
 * API 错误响应结果。
 *
 * @param timestamp 异常发生时间
 * @param status HTTP 响应状态码
 * @param error 错误原因的简短描述
 * @param path 发生异常的请求路径
 */
public record APIErrorResponse(LocalDateTime timestamp, int status, String error,
                               String path) {

    /**
     * 构造 API 错误响应结果。
     *
     * @param status HTTP 错误状态码
     * @param error 错误原因的简短描述
     */
    public APIErrorResponse(int status, String error) {
        this(LocalDateTime.now(), status, error, getRequestPath());
    }

    private static String getRequestPath() {
        return WebUtils.getCurrentRequest().getRequestURI();
    }

}
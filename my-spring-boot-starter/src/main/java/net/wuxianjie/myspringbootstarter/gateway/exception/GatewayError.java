package net.wuxianjie.myspringbootstarter.gateway.exception;

import java.time.LocalDateTime;

/**
 * 网关错误响应结果。
 *
 * @param timestamp 错误发生时间
 * @param status HTTP 状态码
 * @param error 错误信息
 * @param path 发生错误的请求路径
 * @param requestId 请求 ID，即当前请求的唯一标识
 */
public record GatewayError(
    LocalDateTime timestamp, int status, String error, String path,
    String requestId
) {
}
